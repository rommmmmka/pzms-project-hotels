package com.kravets.hotels.booker.ui.screen.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kravets.hotels.booker.R
import com.kravets.hotels.booker.misc.ErrorMessage
import com.kravets.hotels.booker.model.entity.CityEntity
import com.kravets.hotels.booker.model.entity.HotelEntity
import com.kravets.hotels.booker.service.api_object.HotelApiObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class HotelsListViewModel : ViewModel() {
    val displaySnackbarMessage: MutableStateFlow<Int> = MutableStateFlow(0)

    private val _hotelsList: MutableStateFlow<List<HotelEntity>> = MutableStateFlow(emptyList())

    val filterCity: MutableStateFlow<Long> = MutableStateFlow(0L)
    val sortingProperty: MutableStateFlow<String> = MutableStateFlow("creationDate")
    val sortingDirection: MutableStateFlow<Int> = MutableStateFlow(-1)

    val isCityDialogActive: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSortingPropertyDialogActive: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSortingDirectionDialogActive: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val filteredHotelsList: StateFlow<List<HotelEntity>> =
        combine(
            _hotelsList,
            filterCity,
            sortingProperty,
            sortingDirection
        ) { hotelsList, filterCity, sortingProperty, sortingDirection ->
            val filteredList: List<HotelEntity> =
                if (filterCity == 0L) hotelsList
                else hotelsList.filter { it.city.id == filterCity }
            filteredList.sortedBy {
                sortingDirection *
                        if (sortingProperty == "creationDate") it.id
                        else it.roomsCount
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val citiesList: StateFlow<List<CityEntity>> = _hotelsList.mapLatest { hotelsList ->
        hotelsList.map { it.city }.distinct().sortedBy { it.id }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val refreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        getHotelsList()
    }

    fun getHotelsList() {
        _hotelsList.value = emptyList()
        viewModelScope.launch {
            while (true) {
                try {
                    val response = HotelApiObject.getHotelsList()
                    if (response.isSuccessful) {
                        _hotelsList.value = response.body() ?: emptyList()
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

            getHotelsList()

            delay(300)
            refreshing.value = false
        }
    }
}