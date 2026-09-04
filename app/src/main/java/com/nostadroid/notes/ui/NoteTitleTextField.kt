package com.nostadroid.notes.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun NoteTitleTextField(
  state: TextFieldState,
  inViewMode: Boolean,
  focusRequester: FocusRequester,
  textStyle: TextStyle,
  placeholder: String,
  modifier: Modifier = Modifier
) {
  BasicTextField(
    state = state,
    readOnly = inViewMode,
    cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
    textStyle = textStyle.copy(MaterialTheme.colorScheme.onBackground),
    lineLimits = TextFieldLineLimits.SingleLine,
    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    decorator = { innerTextField ->
      Box {
        if (state.text.isEmpty()) {
          Text(
            text = placeholder,
            style = textStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        innerTextField()
      }
    },
    modifier = modifier
      .padding(vertical = 16.dp)
      .focusRequester(focusRequester)
  )
}