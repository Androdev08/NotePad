package com.nostadroid.notes.ui.settingsitems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.nostadroid.notes.R

@Composable
fun SingleChoiceSettingsItem(
  title: String,
  currentValue: String,
  options: List<String>,
  isLastItem: Boolean,
  modifier: Modifier = Modifier,
  onValueSelected: (String) -> Unit
) {
  var showDialog by rememberSaveable { mutableStateOf(false) }
  Column {
    ListItem(
      modifier = modifier.clickable { showDialog = true },
      headlineContent = { Text(title) },
      colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
      supportingContent = {
        Text(currentValue)
      }
    )
    if (!isLastItem) HorizontalDivider()
  }

  // Dialog with choices
  if(showDialog) {
    AlertDialog(
      onDismissRequest = { showDialog = false },
      title = { Text(title) },
      confirmButton = {
        TextButton(onClick = { showDialog = false }) {
          Text(stringResource(R.string.cancel))
        }
      },
      text = {
        Column(modifier = Modifier.selectableGroup()) {
          options.forEach { optionText ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              modifier = Modifier
                .fillMaxWidth()
                .selectable(
                  selected = currentValue == optionText,
                  role = Role.RadioButton
                ) {
                  onValueSelected(optionText)
                  showDialog = false
                }
                .padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
              RadioButton(selected = optionText == currentValue, onClick = null)
              Text(
                text = optionText,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
              )
            }
          }
        }
      }
    )
  }
}