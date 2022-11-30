package com.kravets.hotels.booker.ui.screen.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kravets.hotels.booker.R
import com.kravets.hotels.booker.misc.ErrorMessage
import com.kravets.hotels.booker.ui.screen.view_model.LoginPageViewModel
import com.kravets.hotels.booker.ui.shared.TextFieldPasswordComponent
import com.kravets.hotels.booker.ui.shared.TextFieldTextComponent
import com.kravets.hotels.booker.ui.shared.ValidationErrorComponent
import com.kravets.hotels.booker.ui.theme.Purple40
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalMaterial3Api
@ExperimentalCoroutinesApi
@Composable
fun LoginPage(viewModel: LoginPageViewModel, snackbarHostState: SnackbarHostState) {
    DisplaySnackbar(viewModel, snackbarHostState)
    DisplayContent(viewModel)
}

@ExperimentalCoroutinesApi
@Composable
fun DisplaySnackbar(viewModel: LoginPageViewModel, snackbarHostState: SnackbarHostState) {
    val displaySnackbarError by viewModel.displaySnackbarError.collectAsState()
    val displaySnackbarSuccess by viewModel.displaySnackbarSuccess.collectAsState()

    if (displaySnackbarError != 200) {
        val errorMessage = stringResource(ErrorMessage.getStringId(displaySnackbarError))
        LaunchedEffect(displaySnackbarError) {
            snackbarHostState.showSnackbar(errorMessage)
            viewModel.displaySnackbarError.value = 200
        }
    }

    if (displaySnackbarSuccess != 0) {
        val successMessage = stringResource(displaySnackbarSuccess)
        LaunchedEffect(0) {
            snackbarHostState.showSnackbar(successMessage)
            viewModel.displaySnackbarSuccess.value = 0
        }
    }
}

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@Composable
fun DisplayContent(viewModel: LoginPageViewModel) {
    val login by viewModel.login.collectAsState()
    val password by viewModel.password.collectAsState()

    val loginValidationSymbolsError by viewModel.loginValidationSymbolsError.collectAsState()
    val loginValidationLengthError by viewModel.loginValidationLengthError.collectAsState()
    val passwordValidationSymbolsError by viewModel.passwordValidationSymbolsError.collectAsState()
    val passwordValidationLengthError by viewModel.passwordValidationLengthError.collectAsState()

    val isProcessingRequest by viewModel.isProcessingRequest.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9F)
                .padding(vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextFieldTextComponent(
                descriptionStringId = R.string.login_mandatory,
                value = login,
                onValueChange = { viewModel.updateLogin(it) },
                isError = loginValidationLengthError || loginValidationSymbolsError
            )
            if (loginValidationLengthError) {
                ValidationErrorComponent(R.string.error_allowed_length_6_32)
            }
            if (loginValidationSymbolsError) {
                ValidationErrorComponent(R.string.error_allowed_symbols_log_pass)
            }

            TextFieldPasswordComponent(
                descriptionStringId = R.string.password_mandatory,
                value = password,
                onValueChange = { viewModel.updatePassword(it) },
                isError = passwordValidationLengthError || passwordValidationSymbolsError
            )
            if (passwordValidationLengthError) {
                ValidationErrorComponent(R.string.error_allowed_length_6_32)
            }
            if (passwordValidationSymbolsError) {
                ValidationErrorComponent(R.string.error_allowed_symbols_log_pass)
            }

            if (isProcessingRequest) {
                CircularProgressIndicator()
            } else {
                ButtonSubmit(viewModel)
            }
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
fun ButtonSubmit(viewModel: LoginPageViewModel) {
    val isSubmitButtonEnabled by viewModel.isSubmitButtonEnabled.collectAsState()

    val focusManager = LocalFocusManager.current

    OutlinedButton(
        border = BorderStroke(1.dp, Purple40),
        onClick = {
            focusManager.clearFocus()
            viewModel.onSubmitPressed()
        },
        enabled = isSubmitButtonEnabled
    ) {
        Text(stringResource(id = R.string.login_action))
    }
}