package com.nostadroid.notes.ui.dialog

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nostadroid.notes.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialTimePicker(initialHour: Int, initialMinute: Int, onConfirm: (Int, Int) -> Unit, onDismiss: () -> Unit) {
  val timePickerState = rememberTimePickerState(
    initialHour = initialHour,
    initialMinute = initialMinute,
    is24Hour = true,
  )
  TimePickerDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      TextButton(onClick = {
        onConfirm(timePickerState.hour, timePickerState.minute)
        onDismiss()
      }) { Text(stringResource(R.string.confirm)) }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
    },
    title = { Text(stringResource(R.string.note_edit_time_picker_dialog_title)) }
  ) { TimePicker(state = timePickerState) }
}