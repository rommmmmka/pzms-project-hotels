package com.kravets.hotels.booker.ui.screen.composable

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kravets.hotels.booker.R
import com.kravets.hotels.booker.ui.screen.view_model.OrdersPageViewModel
import com.kravets.hotels.booker.ui.shared.*
import com.kravets.hotels.booker.ui.theme.DarkRed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun OrdersPage(viewModel: OrdersPageViewModel, snackbarHostState: SnackbarHostState) {
    DisplaySnackbar(viewModel, snackbarHostState)
    HotelInfoDialog(viewModel)
    RoomInfoDialog(viewModel)

    DisplayContent(viewModel)

}

@ExperimentalCoroutinesApi
@Composable
fun DisplaySnackbar(viewModel: OrdersPageViewModel, snackbarHostState: SnackbarHostState) {
    val displaySnackbarMessage by viewModel.displaySnackbarMessage.collectAsState()

    if (displaySnackbarMessage != 0) {
        val snackbarMessage = stringResource(displaySnackbarMessage)
        LaunchedEffect(displaySnackbarMessage) {
            snackbarHostState.showSnackbar(snackbarMessage)
            viewModel.displaySnackbarMessage.value = 0
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun DisplayContent(viewModel: OrdersPageViewModel) {
    val ordersList by viewModel.ordersList.collectAsState()

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(Modifier.height(15.dp))
        }
        items(ordersList) {
            val dismissState = rememberDismissState(
                confirmStateChange = { dismissValue ->
                    if (dismissValue == DismissValue.DismissedToStart) {
                        viewModel.removeOrder(it)
                    } else {
                        false
                    }
                }
            )
            SwipeToDismiss(
                state = dismissState,
                directions = setOf(DismissDirection.EndToStart),
                background = {
                    if (dismissState.dismissDirection == DismissDirection.EndToStart) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 20.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(DarkRed)
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(end = 15.dp)
                                    .align(Alignment.CenterEnd),
                                imageVector = Icons.Default.Delete,
                                contentDescription = "",
                                tint = Color.White
                            )
                        }
                    }
                },
                dismissThresholds = {
                    FractionalThreshold(0.4F)
                }
            ) {
                CardComponent {
                    CardHeaderComponent(stringResource(R.string.order_n) + it.id)
                    Spacer(Modifier.height(5.dp))
                    Row {
                        CardTextBoldComponent(stringResource(R.string.check_in_date) + ": ")
                        CardTextComponent(it.checkInDate)
                    }
                    Row {
                        CardTextBoldComponent(stringResource(R.string.check_out_date) + ": ")
                        CardTextComponent(it.checkOutDate)
                    }
                    Spacer(Modifier.height(5.dp))
                    Row {
                        CardTextBoldComponent(stringResource(R.string.order_price))
                        CardTextComponent(it.cost.toString() + stringResource(R.string.roubles))
                    }
                    Spacer(Modifier.height(5.dp))
                    Row {
                        CardTextBoldComponent(stringResource(R.string.order_status))
                        CardTextBoldComponent(
                            it.status.name,
                            Color(("FF" + it.status.color.substring(1).uppercase()).toLong(16))
                        )
                    }
                    if (LocalDateTime.parse(
                            it.expireDateTime,
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        ).year != 999999999
                    ) {
                        CardTextBoldComponent(stringResource(R.string.order_autodelete))
                        CardTextComponent("   " + it.expireDateTime.replace("T", " "))
                    }
                    Spacer(Modifier.height(5.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ElevatedButton(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.onHotelInfoPressed(it.room.hotel)
                            }
                        ) {
                            Text(stringResource(R.string.hotel) + it.room.hotel.name)
                        }
                        ElevatedButton(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.onRoomInfoPressed(it.room)
                            }
                        ) {
                            Text(stringResource(R.string.room) + it.room.name)
                        }
                        if (it.status.id == 2L) {
                            Spacer(Modifier.height(8.dp))
                            ElevatedButton(
                                onClick = {
                                    focusManager.clearFocus()

                                    val baseLink = try {
                                        context.packageManager.getPackageInfo("org.telegram.messenger", 0)
                                        "tg://"
                                    } catch (_ : Exception) {
                                        "https://t.me/"
                                    }
                                    val urlIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("${baseLink}booker_payment_bot?start=${it.id}")
                                    )
                                    context.startActivity(urlIntent)
                                }
                            ) {
                                Text(stringResource(R.string.action_pay))
                            }
                        }
                    }
                }
            }
        }
    }
}


@ExperimentalCoroutinesApi
@Composable
fun HotelInfoDialog(viewModel: OrdersPageViewModel) {
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

@ExperimentalCoroutinesApi
@Composable
fun RoomInfoDialog(viewModel: OrdersPageViewModel) {
    val roomInfoDialogEntity by viewModel.roomInfoDialogEntity.collectAsState()
    val roomInfoDialogDisplay by viewModel.roomInfoDialogDisplay.collectAsState()

    if (roomInfoDialogDisplay) {
        AlertDialog(
            onDismissRequest = { viewModel.onHotelInfoClose() },
            text = {
                Column {
                    CardImageBoxComponent(
                        image = roomInfoDialogEntity!!.coverPhoto,
                        secondTitle = roomInfoDialogEntity!!.name,
                        paddingBottom = 15.dp
                    )
                    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                        if (roomInfoDialogEntity!!.description.isNotEmpty()) {
                            CardTextComponent(roomInfoDialogEntity!!.description)
                            Spacer(Modifier.height(5.dp))
                        }
                        CardTextBoldComponent(stringResource(R.string.limit_people_header))
                        CardTextComponent(stringResource(R.string.limit_adults) + roomInfoDialogEntity!!.adultsLimit)
                        CardTextComponent(stringResource(R.string.limit_children) + (roomInfoDialogEntity!!.guestsLimit - roomInfoDialogEntity!!.adultsLimit))
                        Spacer(Modifier.height(5.dp))
                        CardTextBoldComponent(stringResource(R.string.beds))
                        CardTextComponent(stringResource(R.string.beds_for_one) + roomInfoDialogEntity!!.bedsForOnePersonCount)
                        CardTextComponent(stringResource(R.string.beds_for_two) + roomInfoDialogEntity!!.bedsForTwoPersonsCount)
                        Spacer(Modifier.height(5.dp))
                        CardTextBoldComponent(
                            stringResource(
                                if (roomInfoDialogEntity!!.prepaymentRequired) R.string.prepayment_required
                                else R.string.prepayment_not_needed
                            )
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onRoomInfoClose() }
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }
}