package com.nostadroid.notes.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nostadroid.notes.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YesNoDialog(
  title: String,
  content: String,
  onYesClick: () -> Unit,
  onNoClick: () -> Unit,
  onDismissRequest: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismissRequest,
    confirmButton = { TextButton(onClick = onYesClick) { Text(stringResource(R.string.yes)) } },
    dismissButton = { TextButton(onClick = onNoClick) { Text(stringResource(R.string.no)) } },
    title = { Text(title) },
    text = { Text(content) },
  )
}