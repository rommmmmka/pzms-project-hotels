package com.kravets.hotels.booker.ui.screen.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kravets.hotels.booker.R
import com.kravets.hotels.booker.ui.screen.view_model.MainPageViewModel
import com.kravets.hotels.booker.ui.shared.*
import com.kravets.hotels.booker.ui.theme.Purple40
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@Composable
fun MainPage(viewModel: MainPageViewModel, snackbarHostState: SnackbarHostState) {
    DisplaySnackbar(viewModel, snackbarHostState)
    HotelInfoDialog(viewModel)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchPart(viewModel)
        ResultsPart(viewModel)
    }


}

@ExperimentalCoroutinesApi
@Composable
fun DisplaySnackbar(viewModel: MainPageViewModel, snackbarHostState: SnackbarHostState) {
    val displaySnackbarMessage by viewModel.displaySnackbarMessage.collectAsState()

    if (displaySnackbarMessage != 0) {
        val snackbarMessage = stringResource(displaySnackbarMessage)
        LaunchedEffect(displaySnackbarMessage) {
            snackbarHostState.showSnackbar(snackbarMessage)
            viewModel.displaySnackbarMessage.value = 0
        }
    }
}

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@Composable
fun SearchPart(viewModel: MainPageViewModel) {
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
                viewModel.isCityDialogActive.value = true
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
                viewModel.checkInDate.value = it
                if (it >= checkOutDate) {
                    viewModel.checkOutDate.value = it.plusDays(1)
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
                viewModel.checkOutDate.value = it
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
        border = BorderStroke(1.dp, Purple40),
        onClick = {
            focusManager.clearFocus()
            viewModel.onSearchPressed()
        },
        enabled = isSearchButtonEnabled
    ) {
        Text(stringResource(id = R.string.search))
    }
}

@ExperimentalCoroutinesApi
@Composable
fun ResultsPart(viewModel: MainPageViewModel) {
    val isProcessingSearchRequest by viewModel.isProcessingSearchRequest.collectAsState()

    if (isProcessingSearchRequest) {
        CircularProgressIndicator()
    } else {
        SearchResults(viewModel)
    }

}

@ExperimentalCoroutinesApi
@Composable
fun SearchResults(viewModel: MainPageViewModel) {
    val searchResults by viewModel.searchResults.collectAsState()
    val isProcessingOrderRequest by viewModel.isProcessingOrderRequest.collectAsState()

    val focusManager = LocalFocusManager.current

    searchResults.forEach {
        CardComponent(
            image = it.coverPhoto,
            title = it.name,
            secondTitle = LocalContext.current.resources.getQuantityString(
                R.plurals.free_spaces,
                it.freeRoomsLeft,
                it.freeRoomsLeft
            ),
            thirdTitle = "${it.costPerNight} ${stringResource(id = R.string.rub_per_night)}"
        ) {
            if (it.description != "") {
                CardTextComponent(it.description)
                Spacer(Modifier.height(5.dp))
            }
            CardTextBoldComponent(stringResource(R.string.limit_people_header))
            CardTextComponent(stringResource(R.string.limit_adults) + it.adultsLimit)
            CardTextComponent(stringResource(R.string.limit_children) + (it.guestsLimit - it.adultsLimit))
            Spacer(Modifier.height(5.dp))
            CardTextBoldComponent(stringResource(R.string.beds))
            CardTextComponent(stringResource(R.string.beds_for_one) + it.bedsForOnePersonCount)
            CardTextComponent(stringResource(R.string.beds_for_two) + it.bedsForTwoPersonsCount)
            Spacer(Modifier.height(5.dp))
            CardTextBoldComponent(
                stringResource(
                    if (it.prepaymentRequired) R.string.prepayment_required
                    else R.string.prepayment_not_needed
                )
            )
            Spacer(Modifier.height(5.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ElevatedButton(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.onHotelInfoPressed(it.hotel)
                    }
                ) {
                    Text(stringResource(R.string.hotel) + it.hotel.name)
                }
                Spacer(Modifier.height(8.dp))
                ElevatedButton(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.onOrderPressed(it.id)
                    },
                    enabled = !isProcessingOrderRequest
                ) {
                    Text(stringResource(R.string.action_order))
                }
            }
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
fun HotelInfoDialog(viewModel: MainPageViewModel) {
    val hotelInfoDialogEntity by viewModel.hotelInfoDialogEntity.collectAsState()
    val hotelInfoDialogDisplay by viewModel.hotelInfoDialogDisplay.collectAsState()

    if (hotelInfoDialogDisplay) {
        AlertDialog(
            onDismissRequest = { viewModel.onHotelInfoClose() },
            text = {
                Column {
                    CardImageBoxComponent(
                        image = hotelInfoDialogEntity!!.coverPhoto,
                        secondTitle = hotelInfoDialogEntity!!.name,
                        paddingBottom = 15.dp
                    )
                    Box(modifier = Modifier.padding(horizontal = 10.dp)) {
                        CardTextComponent(hotelInfoDialogEntity!!.description)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onHotelInfoClose() }
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }
}
