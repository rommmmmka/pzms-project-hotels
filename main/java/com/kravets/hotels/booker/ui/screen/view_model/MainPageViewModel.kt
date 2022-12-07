package com.kravets.hotels.booker.ui.screen.view_model

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.kravets.hotels.booker.R
import com.kravets.hotels.booker.misc.ErrorMessage
import com.kravets.hotels.booker.misc.navigateWithoutStack
import com.kravets.hotels.booker.model.entity.CityEntity
import com.kravets.hotels.booker.model.entity.HotelEntity
import com.kravets.hotels.booker.model.entity.RoomEntity
import com.kravets.hotels.booker.service.api_object.CityApiObject
import com.kravets.hotels.booker.service.api_object.DateApiObject
import com.kravets.hotels.booker.service.api_object.OrderApiObject
import com.kravets.hotels.booker.service.api_object.RoomApiObject
import com.kravets.hotels.booker.service.other.DataStore
import com.kravets.hotels.booker.ui.Routes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@ExperimentalCoroutinesApi
class MainPageViewModel(
    private val _navController: NavHostController,
    dataStore: DataStore,
    message: Int?
) : ViewModel() {
    val displaySnackbarMessage: MutableStateFlow<Int> = MutableStateFlow(message ?: 0)

    private val _citiesList: MutableStateFlow<List<CityEntity>> = MutableStateFlow(emptyList())
    val citiesList: StateFlow<List<CityEntity>> = _citiesList
    private val _isCitiesListLoaded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isCitiesListLoaded: StateFlow<Boolean> = _isCitiesListLoaded

    private val _currentServerDate: MutableStateFlow<LocalDate?> = MutableStateFlow(null)
    val currentServerDate: StateFlow<LocalDate?> = _currentServerDate
    private val _isServerDateLoaded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isServerDateLoaded: StateFlow<Boolean> = _isServerDateLoaded

    val cityId: MutableStateFlow<Long> = MutableStateFlow(1)
    val checkInDate: MutableStateFlow<LocalDate?> = MutableStateFlow(null)
    val checkOutDate: MutableStateFlow<LocalDate?> = MutableStateFlow(null)
    val adultsCount: MutableStateFlow<TextFieldValue> = MutableStateFlow(TextFieldValue("1"))
    val childrenCount: MutableStateFlow<TextFieldValue> = MutableStateFlow(TextFieldValue("0"))

    val adultsCountValidationError: StateFlow<Boolean> =
        adultsCount.mapLatest {
            it.text == "" || it.text.toInt() !in 1..30
        }.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val childrenCountValidationError: StateFlow<Boolean> =
        childrenCount.mapLatest {
            it.text == "" || it.text.toInt() !in 0..30
        }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val isCityDialogActive: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isCheckInDatePickerDialogActive: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isCheckOutDatePickerDialogActive: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val _searchCheckInDate: MutableStateFlow<LocalDate?> = MutableStateFlow(null)
    private val _searchCheckOutDate: MutableStateFlow<LocalDate?> = MutableStateFlow(null)
    private val _searchResults: MutableStateFlow<List<RoomEntity>> = MutableStateFlow(emptyList())
    val searchResults: StateFlow<List<RoomEntity>> = _searchResults
    private val _isProcessingSearchRequest: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isProcessingSearchRequest: StateFlow<Boolean> = _isProcessingSearchRequest

    private val _isProcessingOrderRequest: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isProcessingOrderRequest: StateFlow<Boolean> = _isProcessingOrderRequest

    private val _isFormValid: StateFlow<Boolean> =
        combine(
            isCitiesListLoaded,
            isServerDateLoaded,
            adultsCountValidationError,
            childrenCountValidationError
        ) { v1, v2, v3, v4 ->
            v1 && v2 && !v3 && !v4
        }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val isSearchButtonEnabled: StateFlow<Boolean> =
        combine(
            _isProcessingSearchRequest,
            _isProcessingOrderRequest,
            _isFormValid
        ) { isProcessingSearchRequest, isProcessingOrderRequest, isFormValid ->
            !isProcessingSearchRequest && !isProcessingOrderRequest && isFormValid
        }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val hotelInfoDialogEntity: MutableStateFlow<HotelEntity?> = MutableStateFlow(null)
    val hotelInfoDialogDisplay: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val sessionKey: StateFlow<String> =
        dataStore.sessionKey.stateIn(viewModelScope, SharingStarted.Eagerly, "")


    init {
        getCitiesList()
        getServerDate()
    }

    private fun getCitiesList() {
        viewModelScope.launch {
            while (true) {
                try {
                    val response = CityApiObject.getCitiesList()
                    if (response.isSuccessful) {
                        _citiesList.value = response.body() ?: emptyList()
                        _isCitiesListLoaded.value = true
                        break
                    } else {
                        displaySnackbarMessage.value = R.string.error_connecting_to_server
                    }
                } catch (_: Exception) {
                    displaySnackbarMessage.value = R.string.error_connecting_to_server
                }
            }
        }
    }

    private fun getServerDate() {
        viewModelScope.launch {
            while (true) {
                try {
                    val response = DateApiObject.getServerDate()
                    if (response.isSuccessful) {
                        val serverDate = LocalDate.parse(
                            response.body()!!["currentDate"],
                            DateTimeFormatter.ISO_DATE
                        )
                        _currentServerDate.value = serverDate
                        checkInDate.value = serverDate
                        checkOutDate.value = serverDate.plusDays(1)
                        _isServerDateLoaded.value = true
                        break
                    } else {
                        displaySnackbarMessage.value = R.string.error_connecting_to_server
                    }
                } catch (_: Exception) {
                    displaySnackbarMessage.value = R.string.error_connecting_to_server
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

    fun onSearchPressed() {
        _isProcessingSearchRequest.value = true
        _searchCheckInDate.value = checkInDate.value
        _searchCheckOutDate.value = checkOutDate.value
        _searchResults.value = emptyList()
        viewModelScope.launch {
            try {
                val response = RoomApiObject.searchRooms(
                    cityId.value,
                    adultsCount.value.text.toInt(),
                    childrenCount.value.text.toInt(),
                    checkInDate.value!!.format(DateTimeFormatter.ISO_DATE),
                    checkOutDate.value!!.format(DateTimeFormatter.ISO_DATE)
                )
                if (response.isSuccessful) {
                    _searchResults.value = response.body() ?: emptyList()
                } else {
                    displaySnackbarMessage.value = R.string.error_connecting_to_server
                }
            } catch (_: Exception) {
                displaySnackbarMessage.value = R.string.error_connecting_to_server
            }
            _isProcessingSearchRequest.value = false
        }
    }

    fun onHotelInfoPressed(hotelEntity: HotelEntity) {
        hotelInfoDialogEntity.value = hotelEntity
        hotelInfoDialogDisplay.value = true
    }

    fun onHotelInfoClose() {
        hotelInfoDialogDisplay.value = false
    }

    fun onOrderPressed(roomId: Long) {
        if (sessionKey.value == "") {
            displaySnackbarMessage.value = R.string.error_not_logged_in
            return
        }

        _isProcessingOrderRequest.value = true
        viewModelScope.launch {
            try {
                val response = OrderApiObject.addOrder(
                    sessionKey.value,
                    _searchCheckInDate.value!!,
                    _searchCheckOutDate.value!!,
                    roomId
                )
                if (response.isSuccessful) {
                    navigateWithoutStack(
                        _navController, viewModelScope,
                        "${Routes.Orders}?message=${R.string.message_order_added_successfully}"
                    )
                } else {
                    displaySnackbarMessage.value = ErrorMessage.getStringId(response.code())
                }
            } catch (_: Exception) {
                displaySnackbarMessage.value = R.string.error_connecting_to_server
            }
            _isProcessingOrderRequest.value = false
        }

    }
}