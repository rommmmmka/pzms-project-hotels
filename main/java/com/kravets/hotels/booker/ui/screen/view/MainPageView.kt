package com.kravets.hotels.booker.ui.screen.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kravets.hotels.booker.R
import com.kravets.hotels.booker.ui.screen.view_model.MainPageViewModel
import com.kravets.hotels.booker.ui.shared.components.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.format.DateTimeFormatter

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@Composable
fun MainPageView(viewModel: MainPageViewModel, navController: NavController) {
    val snackbarHostState by viewModel.snackbarHostState.collectAsState()
    val displaySnackbarError by viewModel.displaySnackbarError.collectAsState()
    val snackbarMessage = stringResource(id = R.string.error_connecting_to_server)

    ScaffoldComponent(
        topBarStringId = R.string.app_name,
        snackbarHostState = snackbarHostState,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SearchPart(viewModel, navController)
                ResultsPart()
            }
        }
    )

    if (displaySnackbarError) {
        LaunchedEffect(displaySnackbarError) {
            snackbarHostState.showSnackbar(snackbarMessage)
            viewModel.displaySnackbarError.value = false
        }
    }
}

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@Composable
fun SearchPart(viewModel: MainPageViewModel, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.9F)
            .padding(vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchPartCityPicker(viewModel)
        SearchPartDatePickers(viewModel)
        SearchPartNumberInputs(viewModel)
        SearchPartButtonSubmit(viewModel)
    }
}

@ExperimentalCoroutinesApi
@Composable
fun SearchPartCityPicker(viewModel: MainPageViewModel) {
    val cityId by viewModel.cityId.collectAsState()
    val citiesList by viewModel.citiesList.collectAsState()

    val isCitiesListLoaded by viewModel.isCitiesListLoaded.collectAsState()

    PickerButtonComponent(
        descriptionStringId = R.string.city,
        onClick = {
            if (isCitiesListLoaded) {
                viewModel.changeCityDialogStatus(true)
            }
        }
    ) {
        Text(
            style = MaterialTheme.typography.labelMedium,
            text = citiesList.find { entity ->
                entity.id == cityId
            }?.name ?: stringResource(id = R.string.loading)
        )
        SearchPartCityDialog(viewModel)
    }
}

@ExperimentalCoroutinesApi
@Composable
fun SearchPartCityDialog(viewModel: MainPageViewModel) {
    val citiesList by viewModel.citiesList.collectAsState()

    val isCityDialogActive by viewModel.isCityDialogActive.collectAsState()

    DropdownMenuComponent(
        isActive = isCityDialogActive,
        onDismiss = { viewModel.isCityDialogActive.value = false }
    ) {
        citiesList.forEach {
            if (it.disabled) {
                DropdownMenuTextComponent(text = it.name)
            } else {
                DropdownMenuItemComponent(text = it.name) {
                    viewModel.cityId.value = it.id
                    viewModel.isCityDialogActive.value = false
                }
            }
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
fun SearchPartDatePickers(viewModel: MainPageViewModel) {
    val currentServerDate by viewModel.currentServerDate.collectAsState()
    val checkInDate by viewModel.checkInDate.collectAsState()
    val checkOutDate by viewModel.checkOutDate.collectAsState()

    val isServerDateLoaded by viewModel.isServerDateLoaded.collectAsState()

    val isCheckInDatePickerDialogActive by viewModel.isCheckInDatePickerDialogActive.collectAsState()
    val isCheckOutDatePickerDialogActive by viewModel.isCheckOutDatePickerDialogActive.collectAsState()

    PickerButtonComponent(
        descriptionStringId = R.string.check_in_date,
        onClick = {
            if (isServerDateLoaded) {
                viewModel.isCheckInDatePickerDialogActive.value = true
            }
        }
    ) {
        Text(
            style = MaterialTheme.typography.labelMedium,
            text = if (isServerDateLoaded) checkInDate!!.format(DateTimeFormatter.ISO_DATE)
            else stringResource(id = R.string.loading)
        )
    }
    if (isCheckInDatePickerDialogActive) {
        DatePickerComponent(
            minDate = currentServerDate,
            maxDate = currentServerDate?.plusDays(179),
            date = checkInDate,
            onDateSelected = {
                viewModel.updateCheckInDate(it)
                if (it >= checkOutDate) {
                    viewModel.updateCheckOutDate(it.plusDays(1))
                }
                viewModel.isCheckInDatePickerDialogActive.value = false
            },
            onDismissRequest = {
                viewModel.isCheckInDatePickerDialogActive.value = false
            }
        )
    }

    PickerButtonComponent(
        descriptionStringId = R.string.check_out_date,
        onClick = {
            if (isServerDateLoaded) {
                viewModel.isCheckOutDatePickerDialogActive.value = true
            }
        }
    ) {
        Text(
            style = MaterialTheme.typography.labelMedium,
            text = if (isServerDateLoaded) checkOutDate!!.format(DateTimeFormatter.ISO_DATE)
            else stringResource(id = R.string.loading)
        )
    }
    if (isCheckOutDatePickerDialogActive) {
        DatePickerComponent(
            minDate = checkInDate!!.plusDays(1),
            maxDate = currentServerDate!!.plusDays(180),
            date = checkOutDate,
            onDateSelected = {
                viewModel.updateCheckOutDate(it)
                viewModel.isCheckOutDatePickerDialogActive.value = false
            },
            onDismissRequest = {
                viewModel.isCheckOutDatePickerDialogActive.value = false
            }
        )
    }
}

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@Composable
fun SearchPartNumberInputs(viewModel: MainPageViewModel) {
    val adultsCount by viewModel.adultsCount.collectAsState()
    val childrenCount by viewModel.childrenCount.collectAsState()

    val adultsCountValidationError by viewModel.adultsCountValidationError.collectAsState()
    val childrenCountValidationError by viewModel.childrenCountValidationError.collectAsState()

    TextFieldDecimalComponent(
        descriptionStringId = R.string.adults_count,
        value = adultsCount,
        onValueChange = { viewModel.updateAdultsCount(it) },
        isError = adultsCountValidationError
    )
    if (adultsCountValidationError)
        ValidationErrorComponent(errorMessageId = R.string.error_adults_count)

    TextFieldDecimalComponent(
        descriptionStringId = R.string.children_count,
        value = childrenCount,
        onValueChange = { viewModel.updateChildrenCount(it) },
        isError = childrenCountValidationError
    )
    if (childrenCountValidationError)
        ValidationErrorComponent(errorMessageId = R.string.error_children_count)
}

@ExperimentalCoroutinesApi
@Composable
fun SearchPartButtonSubmit(viewModel: MainPageViewModel) {
    val isSearchButtonEnabled by viewModel.isSearchButtonEnabled.collectAsState()

    val focusManager = LocalFocusManager.current

    OutlinedButton(
        onClick = {
            focusManager.clearFocus()
            viewModel.onLoginPressed()
        },
        enabled = isSearchButtonEnabled,
    ) {
        Text(stringResource(id = R.string.search))
    }
}

@Composable
fun ResultsPart() {
    Text(text = "hi")
}
