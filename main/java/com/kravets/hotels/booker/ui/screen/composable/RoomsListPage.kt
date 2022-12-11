package com.kravets.hotels.booker.ui.screen.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kravets.hotels.booker.R
import com.kravets.hotels.booker.ui.screen.view_model.RoomsListViewModel
import com.kravets.hotels.booker.ui.shared.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@Composable
fun RoomsListPage(viewModel: RoomsListViewModel, snackbarHostState: SnackbarHostState) {
    val refreshing by viewModel.refreshing.collectAsState()

    val pullRefreshState = rememberPullRefreshState(refreshing, viewModel::refresh)

    DisplaySnackbar(viewModel, snackbarHostState)
    HotelInfoDialog(viewModel)

    Box(Modifier.pullRefresh(pullRefreshState)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FiltersPart(viewModel)
            ResultsPart(viewModel)
        }
        PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
    }
}

@ExperimentalCoroutinesApi
@Composable
fun DisplaySnackbar(viewModel: RoomsListViewModel, snackbarHostState: SnackbarHostState) {
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
@Composable
fun FiltersPart(viewModel: RoomsListViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.9F)
            .padding(vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FiltersPartHotelPicker(viewModel)
        FiltersPartCityPicker(viewModel)
        FiltersPartSortingPropertyPicker(viewModel)
        FiltersPartSortingOrderPicker(viewModel)
    }
}

@ExperimentalCoroutinesApi
@Composable
fun FiltersPartHotelPicker(viewModel: RoomsListViewModel) {
    val hotelsList by viewModel.hotelsList.collectAsState()

    val filterHotel by viewModel.filterHotel.collectAsState()

    PickerButtonComponent(
        descriptionStringId = R.string.hotel,
        onClick = {
            if (hotelsList.isNotEmpty()) {
                viewModel.isHotelDialogActive.value = true
            }
        }
    ) {
        var text = hotelsList.find { entity ->
            entity.id == filterHotel
        }?.name ?: stringResource(R.string.any)
        if (hotelsList.isEmpty()) {
            text = stringResource(R.string.loading)
        }

        Text(
            style = MaterialTheme.typography.labelMedium,
            text = text
        )
        FiltersPartHotelDialog(viewModel)
    }
}

@ExperimentalCoroutinesApi
@Composable
fun FiltersPartHotelDialog(viewModel: RoomsListViewModel) {
    val hotelsList by viewModel.hotelsList.collectAsState()

    val isHotelDialogActive by viewModel.isHotelDialogActive.collectAsState()

    DropdownMenuComponent(
        isActive = isHotelDialogActive,
        onDismiss = { viewModel.isHotelDialogActive.value = false }
    ) {
        DropdownMenuItemComponent(text = stringResource(R.string.any)) {
            viewModel.filterHotel.value = 0
            viewModel.isHotelDialogActive.value = false
        }
        hotelsList.forEach {
            DropdownMenuItemComponent(text = it.name) {
                viewModel.filterHotel.value = it.id
                viewModel.filterCity.value = it.city.id
                viewModel.isHotelDialogActive.value = false
            }
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
fun FiltersPartCityPicker(viewModel: RoomsListViewModel) {
    val citiesList by viewModel.citiesList.collectAsState()

    val filterCity by viewModel.filterCity.collectAsState()

    PickerButtonComponent(
        descriptionStringId = R.string.city,
        onClick = {
            if (citiesList.isNotEmpty()) {
                viewModel.isCityDialogActive.value = true
            }
        }
    ) {
        var text = citiesList.find { entity ->
            entity.id == filterCity
        }?.name ?: stringResource(R.string.any)
        if (citiesList.isEmpty()) {
            text = stringResource(R.string.loading)
        }

        Text(
            style = MaterialTheme.typography.labelMedium,
            text = text
        )
        FiltersPartCityDialog(viewModel)
    }
}

@ExperimentalCoroutinesApi
@Composable
fun FiltersPartCityDialog(viewModel: RoomsListViewModel) {
    val citiesList by viewModel.citiesList.collectAsState()

    val isCityDialogActive by viewModel.isCityDialogActive.collectAsState()

    DropdownMenuComponent(
        isActive = isCityDialogActive,
        onDismiss = { viewModel.isCityDialogActive.value = false }
    ) {
        DropdownMenuItemComponent(text = stringResource(R.string.any)) {
            viewModel.filterCity.value = 0
            viewModel.filterHotel.value = 0
            viewModel.isCityDialogActive.value = false
        }
        citiesList.forEach {
            if (it.disabled) {
                DropdownMenuTextComponent(text = it.name)
            } else {
                DropdownMenuItemComponent(text = it.name) {
                    if (viewModel.filterCity.value != it.id) {
                        viewModel.filterCity.value = it.id
                        viewModel.filterHotel.value = 0
                    }
                    viewModel.isCityDialogActive.value = false
                }
            }
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
fun FiltersPartSortingPropertyPicker(viewModel: RoomsListViewModel) {
    val sortingProperty by viewModel.sortingProperty.collectAsState()

    PickerButtonComponent(
        descriptionStringId = R.string.filter,
        onClick = {
            viewModel.isSortingPropertyDialogActive.value = true
        }
    ) {
        Text(
            style = MaterialTheme.typography.labelMedium,
            text = when (sortingProperty) {
                "cost" -> stringResource(R.string.cost_filter)
                "roomsCount" -> stringResource(R.string.rooms_count_filter)
                else -> stringResource(R.string.creation_date_filter)
            }
        )
        FiltersPartSortingPropertyDialog(viewModel)
    }
}

@ExperimentalCoroutinesApi
@Composable
fun FiltersPartSortingPropertyDialog(viewModel: RoomsListViewModel) {
    val isSortingPropertyDialogActive by viewModel.isSortingPropertyDialogActive.collectAsState()

    DropdownMenuComponent(
        isActive = isSortingPropertyDialogActive,
        onDismiss = { viewModel.isSortingPropertyDialogActive.value = false }
    ) {
        DropdownMenuItemComponent(text = stringResource(R.string.creation_date_filter)) {
            viewModel.sortingProperty.value = "creationDate"
            viewModel.isSortingPropertyDialogActive.value = false
        }
        DropdownMenuItemComponent(text = stringResource(R.string.rooms_count_filter)) {
            viewModel.sortingProperty.value = "roomsCount"
            viewModel.isSortingPropertyDialogActive.value = false
        }
        DropdownMenuItemComponent(text = stringResource(R.string.cost_filter)) {
            viewModel.sortingProperty.value = "cost"
            viewModel.isSortingPropertyDialogActive.value = false
        }
    }
}


@ExperimentalCoroutinesApi
@Composable
fun FiltersPartSortingOrderPicker(viewModel: RoomsListViewModel) {
    val sortingDirection by viewModel.sortingDirection.collectAsState()

    PickerButtonComponent(
        descriptionStringId = R.string.sorting,
        onClick = {
            viewModel.isSortingDirectionDialogActive.value = true
        }
    ) {
        Text(
            style = MaterialTheme.typography.labelMedium,
            text = if (sortingDirection == -1) stringResource(R.string.descending)
            else stringResource(R.string.ascending)
        )
        FiltersPartSortingDirectionDialog(viewModel)
    }
}

@ExperimentalCoroutinesApi
@Composable
fun FiltersPartSortingDirectionDialog(viewModel: RoomsListViewModel) {
    val isSortingDirectionDialogActive by viewModel.isSortingDirectionDialogActive.collectAsState()

    DropdownMenuComponent(
        isActive = isSortingDirectionDialogActive,
        onDismiss = { viewModel.isSortingDirectionDialogActive.value = false }
    ) {
        DropdownMenuItemComponent(text = stringResource(R.string.descending)) {
            viewModel.sortingDirection.value = -1
            viewModel.isSortingDirectionDialogActive.value = false
        }
        DropdownMenuItemComponent(text = stringResource(R.string.ascending)) {
            viewModel.sortingDirection.value = 1
            viewModel.isSortingDirectionDialogActive.value = false
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
fun ResultsPart(viewModel: RoomsListViewModel) {
    val filteredRoomsList by viewModel.filteredRoomsList.collectAsState()

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(15.dp))
        filteredRoomsList.forEach {
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
                }
            }
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
fun HotelInfoDialog(viewModel: RoomsListViewModel) {
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
