package com.kravets.hotels.booker.ui.screen.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.kravets.hotels.booker.ui.screen.view_model.HotelsListViewModel
import com.kravets.hotels.booker.ui.shared.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun HotelsListPage(viewModel: HotelsListViewModel, snackbarHostState: SnackbarHostState) {
    val refreshing by viewModel.refreshing.collectAsState()

    val pullRefreshState = rememberPullRefreshState(refreshing, viewModel::refresh)

    DisplaySnackbar(viewModel, snackbarHostState)

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
fun DisplaySnackbar(viewModel: HotelsListViewModel, snackbarHostState: SnackbarHostState) {
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
fun FiltersPart(viewModel: HotelsListViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.9F)
            .padding(vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FiltersPartCityPicker(viewModel)
        FiltersPartSortingPropertyPicker(viewModel)
        FiltersPartSortingOrderPicker(viewModel)
    }
}


@ExperimentalCoroutinesApi
@Composable
fun FiltersPartCityPicker(viewModel: HotelsListViewModel) {
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
fun FiltersPartCityDialog(viewModel: HotelsListViewModel) {
    val citiesList by viewModel.citiesList.collectAsState()

    val isCityDialogActive by viewModel.isCityDialogActive.collectAsState()

    DropdownMenuComponent(
        isActive = isCityDialogActive,
        onDismiss = { viewModel.isCityDialogActive.value = false }
    ) {
        DropdownMenuItemComponent(text = stringResource(R.string.any)) {
            viewModel.filterCity.value = 0
            viewModel.isCityDialogActive.value = false
        }
        citiesList.forEach {
            if (it.disabled) {
                DropdownMenuTextComponent(text = it.name)
            } else {
                DropdownMenuItemComponent(text = it.name) {
                    viewModel.filterCity.value = it.id
                    viewModel.isCityDialogActive.value = false
                }
            }
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
fun FiltersPartSortingPropertyPicker(viewModel: HotelsListViewModel) {
    val sortingProperty by viewModel.sortingProperty.collectAsState()

    PickerButtonComponent(
        descriptionStringId = R.string.filter,
        onClick = {
            viewModel.isSortingPropertyDialogActive.value = true
        }
    ) {
        Text(
            style = MaterialTheme.typography.labelMedium,
            text = if (sortingProperty == "creationDate") stringResource(R.string.creation_date_filter)
            else stringResource(R.string.rooms_count_filter)
        )
        FiltersPartSortingPropertyDialog(viewModel)
    }
}

@ExperimentalCoroutinesApi
@Composable
fun FiltersPartSortingPropertyDialog(viewModel: HotelsListViewModel) {
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
    }
}


@ExperimentalCoroutinesApi
@Composable
fun FiltersPartSortingOrderPicker(viewModel: HotelsListViewModel) {
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
fun FiltersPartSortingDirectionDialog(viewModel: HotelsListViewModel) {
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
fun ResultsPart(viewModel: HotelsListViewModel) {
    val filteredHotelsList by viewModel.filteredHotelsList.collectAsState()

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(15.dp))
        filteredHotelsList.forEach {
            CardComponent(
                image = it.coverPhoto,
                title = it.name,
                secondTitle = LocalContext.current.resources.getQuantityString(
                    R.plurals.rooms_in_hotel,
                    it.roomsCount.toInt(),
                    it.roomsCount
                ),
                thirdTitle = it.city.name
            ) {
                if (it.description != "") {
                    CardTextComponent(it.description)
                    Spacer(Modifier.height(5.dp))
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ElevatedButton(
                        onClick = {
                            focusManager.clearFocus()
//                            viewModel.onHotelInfoPressed(it.hotel)
                        }
                    ) {
                        Text(stringResource(R.string.see_rooms))
                    }
                    Spacer(Modifier.height(8.dp))
                    ElevatedButton(
                        onClick = {
                            focusManager.clearFocus()
//                            viewModel.onOrderPressed(it.id)
                        },
                    ) {
                        Text(stringResource(R.string.order_room_for_hotel))
                    }
                }
            }
        }
    }
}