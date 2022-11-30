package com.kravets.hotels.booker.ui.screen.view_model

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.kravets.hotels.booker.R
import com.kravets.hotels.booker.misc.navigateWithoutStack
import com.kravets.hotels.booker.service.api_object.UserApiObject
import com.kravets.hotels.booker.service.other.DataStore
import com.kravets.hotels.booker.ui.Routes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class LoginPageViewModel(
    private val _navController: NavHostController,
    private val _dataStore: DataStore,
    transferredLogin: String?,
    displaySnackbarSuccess: Int?
) : ViewModel() {
    val displaySnackbarError: MutableStateFlow<Int> = MutableStateFlow(200)
    val displaySnackbarSuccess: MutableStateFlow<Int> =
        MutableStateFlow(displaySnackbarSuccess ?: 0)

    val login: MutableStateFlow<TextFieldValue> =
        MutableStateFlow(TextFieldValue(transferredLogin ?: ""))
    val password: MutableStateFlow<TextFieldValue> = MutableStateFlow(TextFieldValue(""))

    val loginValidationSymbolsError: StateFlow<Boolean> = login.mapLatest {
        !it.text.matches(Regex("[A-z0-9\\-_]*"))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val loginValidationLengthError: StateFlow<Boolean> = login.mapLatest {
        it.text.isNotEmpty() && it.text.length < 6
    }.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val passwordValidationSymbolsError: StateFlow<Boolean> = password.mapLatest {
        !it.text.matches(Regex("[A-z0-9\\-_]*"))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val passwordValidationLengthError: StateFlow<Boolean> = password.mapLatest {
        it.text.isNotEmpty() && it.text.length < 6
    }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    private var _isProcessingRequest: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isProcessingRequest: StateFlow<Boolean> = _isProcessingRequest

    private var _isFormValid: StateFlow<Boolean> =
        combine(
            loginValidationSymbolsError,
            loginValidationLengthError,
            passwordValidationSymbolsError,
            passwordValidationLengthError
        ) { arr ->
            true !in arr
        }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    private var _isFormFilled: StateFlow<Boolean> =
        combine(login, password) { arr ->
            "" !in arr.map { el -> el.text }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val isSubmitButtonEnabled: StateFlow<Boolean> =
        combine(
            _isProcessingRequest,
            _isFormValid,
            _isFormFilled
        ) { isProcessingRequest, isFormValid, isFormFilled ->
            !isProcessingRequest && isFormValid && isFormFilled
        }.stateIn(viewModelScope, SharingStarted.Eagerly, true)


    fun updateLogin(changedValue: TextFieldValue) {
        if (changedValue.text.length > 32)
            return
        login.value = changedValue
    }

    fun updatePassword(changedValue: TextFieldValue) {
        if (changedValue.text.length > 32)
            return
        password.value = changedValue
    }

    fun onSubmitPressed() {
        _isProcessingRequest.value = true
        viewModelScope.launch {
            var errorCode = 1
            try {
                val response = UserApiObject.loginUser(login.value.text, password.value.text)
                errorCode = response.code()
                if (errorCode == 200) {
                    viewModelScope.launch {
                        _dataStore.saveSessionKey(response.body()?.sessionKey)
                        _dataStore.saveShortName(response.body()?.shortName)
                        _dataStore.saveIsAdmin(response.body()?.isAdmin)
                    }
                    navigateWithoutStack(
                        _navController, viewModelScope,
                        destination = "${Routes.MainPage}?success=${R.string.message_successful_signing_in}"
                    )
//                    _navController.navigate("${Routes.MainPage}?success=${R.string.message_successful_signing_in}")
                }
            } catch (_: Exception) {
            }
            displaySnackbarError.value = errorCode
            _isProcessingRequest.value = false

        }
    }
}