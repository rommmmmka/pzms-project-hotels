package com.kravets.hotels.booker.ui.screens.main_page

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

@ExperimentalCoroutinesApi
class MainPageViewModel : ViewModel() {
    private var _currentServerDate: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())
    var checkInDate: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())
    var checkOutDate: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now().plusDays(1))
    var adultsCount: MutableStateFlow<TextFieldValue> = MutableStateFlow(TextFieldValue("1"))
    var childrenCount: MutableStateFlow<TextFieldValue> = MutableStateFlow(TextFieldValue("0"))

    var dateValidationError1: StateFlow<Boolean> =
        combine(checkInDate, checkOutDate) { checkInDate, checkOutDate ->
            checkInDate >= checkOutDate
        }.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    var dateValidationError2: StateFlow<Boolean> =
        combine(
            _currentServerDate,
            checkInDate,
            checkOutDate
        ) { currentServerDate, checkInDate, checkOutDate ->
            currentServerDate > checkInDate || currentServerDate > checkOutDate
        }.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    var adultsCountValidationError: StateFlow<Boolean> =
        adultsCount.mapLatest {
            it.text == "" || it.text.toInt() !in 1..30
        }.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    var childrenCountValidationError: StateFlow<Boolean> =
        childrenCount.mapLatest {
            it.text == "" || it.text.toInt() !in 0..30
        }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    private var _isProcessingSearchRequest: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isProcessingSearchRequest: StateFlow<Boolean> = _isProcessingSearchRequest

    private var _isFormValid: StateFlow<Boolean> =
        combine(
            dateValidationError1,
            dateValidationError2,
            adultsCountValidationError,
            childrenCountValidationError
        ) { v1, v2, v3, v4 ->
            !(v1 || v2 || v3 || v4)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    var isSearchButtonEnabled: StateFlow<Boolean> =
        combine(isProcessingSearchRequest, _isFormValid) { isProcessingSearchRequest, isFormValid ->
            !isProcessingSearchRequest && isFormValid
        }.stateIn(viewModelScope, SharingStarted.Eagerly, true)


    fun updateCheckInDate(newCheckInDate: LocalDate) {
        checkInDate.value = newCheckInDate
    }

    fun updateCheckOutDate(newCheckOutDate: LocalDate) {
        checkOutDate.value = newCheckOutDate
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
            childrenCount.value = TextFieldValue(stringContentNoTrailingZeroes, TextRange(textRange))
        } catch (_: Exception) {
        }
    }

    fun onLoginPressed() {
        _isProcessingSearchRequest.value = true
        viewModelScope.launch {
            delay(1000)
            _isProcessingSearchRequest.value = false
        }
    }
}