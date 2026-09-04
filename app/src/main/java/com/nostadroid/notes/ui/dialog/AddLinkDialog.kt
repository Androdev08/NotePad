package com.nostadroid.notes.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.nostadroid.notes.R

@Composable
fun AddLinkDialog(
  initialText: String = "",
  onDismissRequest: () -> Unit,
  dialogCallback: (String, String) -> Unit
) {
  val text = rememberTextFieldState(initialText)
  val url = rememberTextFieldState()

  Dialog(
    onDismissRequest = onDismissRequest
  ) {
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
          text = stringResource(R.string.note_edit_add_link_dialog_title),
          style = MaterialTheme.typography.headlineSmall,
          color = MaterialTheme.colorScheme.onSurface
        )
        OutlinedTextField(
          state = text,
          label = { Text(stringResource(R.string.note_edit_add_link_dialog_text_placeholder)) },
          lineLimits = TextFieldLineLimits.SingleLine,
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        OutlinedTextField(
          state = url,
          label = { Text(stringResource(R.string.note_edit_add_link_dialog_url_placeholder)) },
          lineLimits = TextFieldLineLimits.SingleLine,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
        )
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.align(Alignment.End)
        ) {
          TextButton(onClick = { onDismissRequest() }) { Text(stringResource(R.string.cancel)) }
          TextButton(onClick = {
            onDismissRequest()
            dialogCallback(text.text.toString(), url.text.toString())
          }) { Text(stringResource(R.string.add)) }
        }
      }
    }
  }
}