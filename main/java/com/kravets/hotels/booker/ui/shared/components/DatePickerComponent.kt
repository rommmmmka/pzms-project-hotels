package com.kravets.hotels.booker.ui.shared.components

import android.text.format.DateFormat
import android.widget.CalendarView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kravets.hotels.booker.R
import com.kravets.hotels.booker.ui.theme.Typography
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

/**
 * A Jetpack Compose compatible Date Picker.
 * @author Arnau Mora, Joao Gavazzi
 * @param minDate The minimum date allowed to be picked.
 * @param maxDate The maximum date allowed to be picked.
 * @param onDateSelected Will get called when a date gets picked.
 * @param onDismissRequest Will get called when the user requests to close the dialog.
 */
@Composable
fun DatePickerComponent(
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    date: LocalDate? = null,
    onDateSelected: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    val selDate = remember { mutableStateOf(Calendar.getInstance().time) }
    val minDateLong = minDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    val maxDateLong = maxDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    val dateLong = date?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()

    Dialog(onDismissRequest = { onDismissRequest() }, properties = DialogProperties()) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(size = 16.dp)
                )
        ) {
            Column(
                Modifier
                    .defaultMinSize(minHeight = 72.dp)
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.pick_date),
                    style = Typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = DateFormat.format("MMM d, yyyy", selDate.value).toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.size(8.dp))
            }

            CustomCalendarView(
                minDateLong, maxDateLong, dateLong,
                onDateSelected = {
                    selDate.value = it
                }
            )

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 16.dp, end = 16.dp)
            ) {
                Button(
                    onClick = onDismissRequest,
                    colors = ButtonDefaults.textButtonColors(),
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }

                Button(
                    onClick = {
                        val newDate: LocalDate = Instant.ofEpochMilli(
                            maxOf(
                                minOf(maxDateLong ?: Long.MAX_VALUE, selDate.value.time),
                                minDateLong ?: Long.MIN_VALUE
                            )
                        ).atZone(ZoneId.systemDefault()).toLocalDate()
                        onDateSelected(newDate)
                    },
                    colors = ButtonDefaults.textButtonColors(),
                ) {
                    Text(text = stringResource(id = R.string.confirm))
                }

            }
        }
    }
}

/**
 * Used at [DatePicker] to create the calendar picker.
 * @author Arnau Mora, Joao Gavazzi
 * @param minDate The minimum date allowed to be picked.
 * @param maxDate The maximum date allowed to be picked.
 * @param onDateSelected Will get called when a date is selected.
 */
@Composable
private fun CustomCalendarView(
    minDate: Long? = null,
    maxDate: Long? = null,
    date: Long? = null,
    onDateSelected: (Date) -> Unit
) {
    // Adds view to Compose
    AndroidView(
        modifier = Modifier.wrapContentSize(),
        factory = { context ->
            CalendarView(context)
        }
    ) { view ->
        if (minDate != null)
            view.minDate = minDate
        if (maxDate != null)
            view.maxDate = maxDate
        if (date != null)
            view.date = date

        view.setOnDateChangeListener { _, year, month, dayOfMonth ->
            onDateSelected(
                Calendar
                    .getInstance()
                    .apply {
                        set(year, month, dayOfMonth)
                    }
                    .time
            )
        }
    }
}