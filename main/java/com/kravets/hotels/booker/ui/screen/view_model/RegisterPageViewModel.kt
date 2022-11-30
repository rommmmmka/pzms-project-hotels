package com.kravets.hotels.booker.ui.screen.view_model

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.kravets.hotels.booker.R
import com.kravets.hotels.booker.misc.navigateWithoutStack
import com.kravets.hotels.booker.service.api_object.UserApiObject
import com.kravets.hotels.booker.ui.Routes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class RegisterPageViewModel(private val _navController: NavHostController) : ViewModel() {
    val displaySnackbarError: MutableStateFlow<Int> = MutableStateFlow(200)

    val lastname: MutableStateFlow<TextFieldValue> = MutableStateFlow(TextFieldValue(""))
    val firstname: MutableStateFlow<TextFieldValue> = MutableStateFlow(TextFieldValue(""))
    val patronymic: MutableStateFlow<TextFieldValue> = MutableStateFlow(TextFieldValue(""))
    val login: MutableStateFlow<TextFieldValue> = MutableStateFlow(TextFieldValue(""))
    val password: MutableStateFlow<TextFieldValue> = MutableStateFlow(TextFieldValue(""))
    val password2: MutableStateFlow<TextFieldValue> = MutableStateFlow(TextFieldValue(""))

    val lastnameValidationError: StateFlow<Boolean> = lastname.mapLatest {
        !it.text.matches(Regex("[A-zА-яЁёІіЎў'\\- ]*"))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val firstnameValidationError: StateFlow<Boolean> = firstname.mapLatest {
        !it.text.matches(Regex("[A-zА-яЁёІіЎў'\\- ]*"))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val patronymicValidationError: StateFlow<Boolean> = patronymic.mapLatest {
        !it.text.matches(Regex("[A-zА-яЁёІіЎў'\\- ]*"))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

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
    val password2ValidationError: StateFlow<Boolean> = combine(password, password2) { p1, p2 ->
        p1.text != p2.text
    }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    private var _isProcessingRequest: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isProcessingRequest: StateFlow<Boolean> = _isProcessingRequest

    private var _isFormValid: StateFlow<Boolean> =
        combine(
            lastnameValidationError,
            firstnameValidationError,
            patronymicValidationError,
            loginValidationSymbolsError,
            loginValidationLengthError,
            passwordValidationSymbolsError,
            passwordValidationLengthError,
            password2ValidationError
        ) { arr ->
            true !in arr
        }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    private var _isFormFilled: StateFlow<Boolean> =
        combine(
            lastname,
            firstname,
            login,
            password,
            password2
        ) { arr ->
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


    fun updateLastname(changedValue: TextFieldValue) {
        if (changedValue.text.length > 32)
            return
        lastname.value = changedValue
    }

    fun updateFirstname(changedValue: TextFieldValue) {
        if (changedValue.text.length > 32)
            return
        firstname.value = changedValue
    }

    fun updatePatronymic(changedValue: TextFieldValue) {
        if (changedValue.text.length > 32)
            return
        patronymic.value = changedValue
    }

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

    fun updatePassword2(changedValue: TextFieldValue) {
        if (changedValue.text.length > 32)
            return
        password2.value = changedValue
    }

    fun onSubmitPressed() {
        _isProcessingRequest.value = true
        viewModelScope.launch {
            val errorCode = try {
                UserApiObject.registerUser(
                    login.value.text,
                    password.value.text,
                    lastname.value.text,
                    firstname.value.text,
                    patronymic.value.text
                ).code()
            } catch (e: Exception) {
                1
            }
            displaySnackbarError.value = errorCode
            _isProcessingRequest.value = false
            if (errorCode == 200) {
                navigateWithoutStack(
                    _navController, viewModelScope,
                    destination = "${Routes.Login}?login=${login.value.text}&success=${R.string.message_successful_registration}"
                )
//                _navController.navigate("${Routes.Login}?login=${login.value.text}&success=${R.string.message_successful_registration}")
            }
        }
    }
}