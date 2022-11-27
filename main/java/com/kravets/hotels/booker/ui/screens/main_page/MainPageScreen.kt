package com.kravets.hotels.booker.ui.screens.main_page

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import com.kravets.hotels.booker.R
import com.kravets.hotels.booker.ui.shared.components.PickerButtonComponent
import com.kravets.hotels.booker.ui.shared.components.ScaffoldComponent
import com.kravets.hotels.booker.ui.shared.components.TextFieldDecimalComponent
import com.kravets.hotels.booker.ui.shared.components.ValidationErrorComponent
import com.kravets.hotels.booker.ui.theme.DarkRed
import com.kravets.hotels.booker.ui.theme.Purple40
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@Composable
fun MainPageScreen(viewModel: MainPageViewModel, navController: NavController) {
    ScaffoldComponent(
        topBarStringId = R.string.app_name,
        content = {
            Column {
                SearchPart(viewModel, navController)
                ResultsPart()
            }
        }
    )
}

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@Composable
fun SearchPart(viewModel: MainPageViewModel, navController: NavController) {
    Column(
        modifier = Modifier
            .padding(40.dp, 15.dp)
            .fillMaxWidth(),
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
    PickerButtonComponent(
        descriptionStringId = R.string.city,
        isError = false,
        onClick = { /*TODO*/ }
    ) {
        Text(text = "Minsk")
        println("ggg")
    }
}

@ExperimentalCoroutinesApi
@Composable
fun SearchPartDatePickers(viewModel: MainPageViewModel) {
    val checkInDate by viewModel.checkInDate.collectAsState()
    val checkOutDate by viewModel.checkOutDate.collectAsState()

    val dateValidationError1 by viewModel.dateValidationError1.collectAsState()
    val dateValidationError2 by viewModel.dateValidationError2.collectAsState()

    val checkInDatePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            viewModel.updateCheckInDate(LocalDate.of(year, month + 1, day))
        },
        checkInDate.year, checkInDate.monthValue - 1, checkInDate.dayOfMonth
    )
    val checkOutDatePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            println("$year, $month, $day")
            viewModel.updateCheckOutDate(LocalDate.of(year, month + 1, day))
        },
        checkOutDate.year, checkOutDate.monthValue - 1, checkOutDate.dayOfMonth
    )

    PickerButtonComponent(
        descriptionStringId = R.string.check_in_date,
        isError = dateValidationError1 || dateValidationError2,
        onClick = {
            checkInDatePickerDialog.show()
        }
    ) {
        Text(text = checkInDate.format(DateTimeFormatter.ISO_DATE))
    }
    PickerButtonComponent(
        descriptionStringId = R.string.check_out_date,
        isError = dateValidationError1 || dateValidationError2,
        onClick = {
            checkOutDatePickerDialog.show()
        }
    ) {
        Text(text = checkOutDate.format(DateTimeFormatter.ISO_DATE))
    }
    if (dateValidationError1)
        ValidationErrorComponent(errorMessageId = R.string.error_check_in_date_after_check_out)
    if (dateValidationError2)
        ValidationErrorComponent(errorMessageId = R.string.error_check_in_date_before_current_date)
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
