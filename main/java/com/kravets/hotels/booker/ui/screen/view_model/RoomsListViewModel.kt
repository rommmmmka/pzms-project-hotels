package com.kravets.hotels.booker.ui.screen.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kravets.hotels.booker.R
import com.kravets.hotels.booker.misc.ErrorMessage
import com.kravets.hotels.booker.model.entity.CityEntity
import com.kravets.hotels.booker.model.entity.HotelEntity
import com.kravets.hotels.booker.model.entity.RoomEntity
import com.kravets.hotels.booker.service.api_object.HotelApiObject
import com.kravets.hotels.booker.service.api_object.RoomApiObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class RoomsListViewModel(hotel: Long?, city: Long?) : ViewModel() {
    val displaySnackbarMessage: MutableStateFlow<Int> = MutableStateFlow(0)

    private val _roomsList: MutableStateFlow<List<RoomEntity>> = MutableStateFlow(emptyList())

    val filterHotel: MutableStateFlow<Long> = MutableStateFlow(hotel ?: 0L)
    val filterCity: MutableStateFlow<Long> = MutableStateFlow(city ?: 0L)
    val sortingProperty: MutableStateFlow<String> = MutableStateFlow("creationDate")
    val sortingDirection: MutableStateFlow<Int> = MutableStateFlow(-1)

    val isHotelDialogActive: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isCityDialogActive: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSortingPropertyDialogActive: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSortingDirectionDialogActive: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val filteredRoomsList: StateFlow<List<RoomEntity>> =
        combine(
            _roomsList,
            filterHotel,
            filterCity,
            sortingProperty,
            sortingDirection
        ) { roomsList, filterHotel, filterCity, sortingProperty, sortingDirection ->
            val filteredList: List<RoomEntity> =
                roomsList.filter {
                    (filterHotel == 0L || it.hotel.id == filterHotel) && (filterCity == 0L || it.hotel.city.id == filterCity)
                }
            filteredList.sortedBy {
                var value = sortingDirection * 1L
                when (sortingProperty) {
                    "cost" -> value *= it.costPerNight
                    "roomsCount" -> value *= it.roomsCount
                    else -> value *= it.id
                }
                value
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val hotelsList: StateFlow<List<HotelEntity>> = _roomsList.mapLatest { roomsList ->
        roomsList.map { it.hotel }.distinct().sortedBy { it.id }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val citiesList: StateFlow<List<CityEntity>> = _roomsList.mapLatest { roomsList ->
        roomsList.map { it.hotel.city }.distinct().sortedBy { it.id }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val refreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val hotelInfoDialogEntity: MutableStateFlow<HotelEntity?> = MutableStateFlow(null)
    val hotelInfoDialogDisplay: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        getRoomsList()
    }

    private fun getRoomsList() {
        _roomsList.value = emptyList()
        viewModelScope.launch {
            while (true) {
                try {
                    val response = RoomApiObject.getRoomsList()
                    if (response.isSuccessful) {
                        _roomsList.value = response.body() ?: emptyList()
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

    fun refresh() {
        viewModelScope.launch {
            refreshing.value = true

            getRoomsList()

            delay(300)
            refreshing.value = false
        }
    }

    fun onHotelInfoPressed(hotelEntity: HotelEntity) {
        hotelInfoDialogEntity.value = hotelEntity
        hotelInfoDialogDisplay.value = true
    }

    fun onHotelInfoClose() {
        hotelInfoDialogDisplay.value = false
    }
}