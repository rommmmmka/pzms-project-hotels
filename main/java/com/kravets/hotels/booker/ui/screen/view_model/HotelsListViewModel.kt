package com.kravets.hotels.booker.ui.screen.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kravets.hotels.booker.model.entity.HotelEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HotelsListViewModel : ViewModel() {
    private val _hotelsList: MutableStateFlow<List<HotelEntity>> = MutableStateFlow(emptyList())
    val hotelsList: StateFlow<List<HotelEntity>> = _hotelsList

    init {
        getHotelsList()
    }

    fun getHotelsList() {
        _hotelsList.value = emptyList()
        viewModelScope.launch {
            while (true) {

            }
        }
    }
}