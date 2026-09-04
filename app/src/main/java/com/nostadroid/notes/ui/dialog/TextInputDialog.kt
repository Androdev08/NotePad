package com.nostadroid.notes.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.nostadroid.notes.R

@Composable
fun TextInputDialog(
  title: String,
  placeholder: String,
  errorText: String,
  onConfirm: (String) -> Unit,
  onDismiss: () -> Unit) {
  val text = rememberTextFieldState()
  val focusRequester = remember { FocusRequester() }
  var isError by rememberSaveable { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
      shape = RoundedCornerShape(28.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.headlineSmall,
          color = MaterialTheme.colorScheme.onSurface
        )
        OutlinedTextField(
          state = text,
          label = { Text(if(isError) errorText else placeholder) },
          lineLimits = TextFieldLineLimits.SingleLine,
          isError = isError,
          modifier = Modifier.focusRequester(focusRequester)
        )
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.align(Alignment.End)
        ) {
          TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
          TextButton(onClick = {
            isError = !validateString(text.text.toString())
            if(!isError) {
              onDismiss()
              onConfirm(text.text.toString())
            }
          }) { Text(stringResource(R.string.add)) }
        }
      }
    }
  }
  LaunchedEffect(Unit) {
    focusRequester.requestFocus()
  }
}

private fun validateString(inStr: String): Boolean {
  return inStr.isNotBlank()
}