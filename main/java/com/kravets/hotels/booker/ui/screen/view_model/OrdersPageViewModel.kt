package com.kravets.hotels.booker.ui.screen.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kravets.hotels.booker.R
import com.kravets.hotels.booker.misc.ErrorMessage
import com.kravets.hotels.booker.model.entity.HotelEntity
import com.kravets.hotels.booker.model.entity.OrderEntity
import com.kravets.hotels.booker.model.entity.RoomEntity
import com.kravets.hotels.booker.service.api_object.OrderApiObject
import com.kravets.hotels.booker.service.other.DataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrdersPageViewModel(dataStore: DataStore, message: Int?) : ViewModel() {
    val displaySnackbarMessage: MutableStateFlow<Int> = MutableStateFlow(message ?: 0)

    val sessionKey: StateFlow<String> =
        dataStore.sessionKey.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    private val _ordersList: MutableStateFlow<List<OrderEntity>> = MutableStateFlow(emptyList())
    val ordersList: StateFlow<List<OrderEntity>> = _ordersList

    val hotelInfoDialogEntity: MutableStateFlow<HotelEntity?> = MutableStateFlow(null)
    val hotelInfoDialogDisplay: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val roomInfoDialogEntity: MutableStateFlow<RoomEntity?> = MutableStateFlow(null)
    val roomInfoDialogDisplay: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val refreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        getOrdersList()
    }

    private fun getOrdersList() {
        _ordersList.value = emptyList()
        viewModelScope.launch {
            while (true) {
                try {
                    val response = OrderApiObject.getOrdersList(sessionKey.value)
                    if (response.isSuccessful) {
                        _ordersList.value = response.body()?.reversed() ?: emptyList()
                        break
                    } else {
                        displaySnackbarMessage.value = ErrorMessage.getStringId(response.code())
                    }
                } catch (_: Exception) {
                    displaySnackbarMessage.value = R.string.error_connecting_to_server
                }
            }
        }
    }

    fun onHotelInfoPressed(hotelEntity: HotelEntity) {
        hotelInfoDialogEntity.value = hotelEntity
        hotelInfoDialogDisplay.value = true
    }

    fun onHotelInfoClose() {
        hotelInfoDialogDisplay.value = false
    }

    fun onRoomInfoPressed(roomEntity: RoomEntity) {
        roomInfoDialogEntity.value = roomEntity
        roomInfoDialogDisplay.value = true
    }

    fun onRoomInfoClose() {
        roomInfoDialogDisplay.value = false
    }

    fun removeOrder(orderEntity: OrderEntity): Boolean {
        return if (orderEntity.status.id > 2) {
            displaySnackbarMessage.value = R.string.error_cant_delete_order
            false
        } else {
            viewModelScope.launch {
                try {
                    OrderApiObject.removeOrder(sessionKey.value, orderEntity.id)
                    getOrdersList()
                } catch (_: Exception) {
                    displaySnackbarMessage.value = R.string.error_connecting_to_server
                }
            }
            true
        }
    }

    fun refresh() {
        viewModelScope.launch {
            refreshing.value = true

            getOrdersList()

            delay(300)
            refreshing.value = false
        }
    }
}

