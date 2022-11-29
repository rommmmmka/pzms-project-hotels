package com.kravets.hotels.booker.ui.screen.view_model

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kravets.hotels.booker.model.entity.CityEntity
import com.kravets.hotels.booker.model.entity.RoomEntity
import com.kravets.hotels.booker.service.api_object.CityApiObject
import com.kravets.hotels.booker.service.api_object.DateApiObject
import com.kravets.hotels.booker.service.api_object.RoomApiObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@ExperimentalCoroutinesApi
class MainPageViewModel : ViewModel() {
    private var _citiesList: MutableStateFlow<List<CityEntity>> = MutableStateFlow(emptyList())
    val citiesList: StateFlow<List<CityEntity>> = _citiesList
    private var _isCitiesListLoaded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isCitiesListLoaded: StateFlow<Boolean> = _isCitiesListLoaded

    private var _currentServerDate: MutableStateFlow<LocalDate?> = MutableStateFlow(null)
    var currentServerDate: StateFlow<LocalDate?> = _currentServerDate
    private var _isServerDateLoaded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isServerDateLoaded: StateFlow<Boolean> = _isServerDateLoaded

    var cityId: MutableStateFlow<Long> = MutableStateFlow(1)
    var checkInDate: MutableStateFlow<LocalDate?> = MutableStateFlow(null)
    var checkOutDate: MutableStateFlow<LocalDate?> = MutableStateFlow(null)
    var adultsCount: MutableStateFlow<TextFieldValue> = MutableStateFlow(TextFieldValue("1"))
    var childrenCount: MutableStateFlow<TextFieldValue> = MutableStateFlow(TextFieldValue("0"))

    var adultsCountValidationError: StateFlow<Boolean> =
        adultsCount.mapLatest {
            it.text == "" || it.text.toInt() !in 1..30
        }.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    var childrenCountValidationError: StateFlow<Boolean> =
        childrenCount.mapLatest {
            it.text == "" || it.text.toInt() !in 0..30
        }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    var displaySnackbarError: MutableStateFlow<Boolean> = MutableStateFlow(false)

    var isCityDialogActive: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isCheckInDatePickerDialogActive: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isCheckOutDatePickerDialogActive: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private var _searchResults: MutableStateFlow<List<RoomEntity>> = MutableStateFlow(emptyList())
    val searchResults: StateFlow<List<RoomEntity>> = _searchResults
    private var _isProcessingSearchRequest: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isProcessingSearchRequest: StateFlow<Boolean> = _isProcessingSearchRequest

    private var _isFormValid: StateFlow<Boolean> =
        combine(
            isCitiesListLoaded,
            isServerDateLoaded,
            adultsCountValidationError,
            childrenCountValidationError
        ) { v1, v2, v3, v4 ->
            v1 && v2 && !v3 && !v4
        }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    var isSearchButtonEnabled: StateFlow<Boolean> =
        combine(isProcessingSearchRequest, _isFormValid) { isProcessingSearchRequest, isFormValid ->
            !isProcessingSearchRequest && isFormValid
        }.stateIn(viewModelScope, SharingStarted.Eagerly, true)


    init {
        getCitiesList()
        getServerDate()
    }

    private fun getCitiesList() {
        viewModelScope.launch {
            while (true) {
                try {
                    val citiesList = CityApiObject.getCitiesList()
                    _citiesList.value = citiesList
                    _isCitiesListLoaded.value = true
                    break
                } catch (_: Exception) {
                    displaySnackbarError.value = true
                }
            }
        }
    }

    private fun getServerDate() {
        viewModelScope.launch {
            while (true) {
                try {
                    val serverDate = DateApiObject.getServerDate()
                    _currentServerDate.value = serverDate!!
                    checkInDate.value = serverDate
                    checkOutDate.value = serverDate.plusDays(1)
                    _isServerDateLoaded.value = true
                    break
                } catch (_: Exception) {
                    displaySnackbarError.value = true
                }
            }

        }
    }

    fun updateAdultsCount(changedValue: TextFieldValue) {
        if (changedValue.text == adultsCount.value.text || changedValue.text == "") {
            adultsCount.value = changedValue
            return
        }
        if (changedValue.text.length > 2)
            return

        val stringContent = changedValue.text
        try {
            val stringContentNoTrailingZeroes = stringContent.toInt().toString()
            val textRange =
                changedValue.selection.start + stringContentNoTrailingZeroes.length - stringContent.length
            adultsCount.value = TextFieldValue(stringContentNoTrailingZeroes, TextRange(textRange))
        } catch (_: Exception) {
        }
    }

    fun updateChildrenCount(changedValue: TextFieldValue) {
        if (changedValue.text == childrenCount.value.text || changedValue.text == "") {
            childrenCount.value = changedValue
            return
        }
        if (changedValue.text.length > 2)
            return

        val stringContent = changedValue.text
        try {
            val stringContentNoTrailingZeroes = stringContent.toInt().toString()
            val textRange =
                changedValue.selection.start + stringContentNoTrailingZeroes.length - stringContent.length
            childrenCount.value =
                TextFieldValue(stringContentNoTrailingZeroes, TextRange(textRange))
        } catch (_: Exception) {
        }
    }

    fun onLoginPressed() {
        _isProcessingSearchRequest.value = true
        viewModelScope.launch {
            try {
                val results = RoomApiObject.searchRooms(
                    cityId.value,
                    adultsCount.value.text.toInt(),
                    childrenCount.value.text.toInt(),
                    checkInDate.value!!.format(DateTimeFormatter.ISO_DATE),
                    checkOutDate.value!!.format(DateTimeFormatter.ISO_DATE)
                )
                _searchResults.value = results
            } catch (_: Exception) {
                displaySnackbarError.value = true
            }
            _isProcessingSearchRequest.value = false
        }
    }
}