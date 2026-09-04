package com.nostadroid.notes.ui.dialog

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nostadroid.notes.R
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDatePicker(noteDate: Long, onDateSelected: (Long) -> Unit, onDismiss: () -> Unit) {
  val datePickerState = rememberDatePickerState(initialSelectedDate = Instant
    .ofEpochMilli(noteDate)
    .atZone(ZoneId.systemDefault())
    .toLocalDate()
  )
  DatePickerDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      TextButton(onClick = {
        onDateSelected(datePickerState.selectedDateMillis!!)
        onDismiss()
      }) { Text(stringResource(R.string.confirm)) }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
    }
  ) { DatePicker(state = datePickerState) }
}