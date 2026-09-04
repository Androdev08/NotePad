package com.nostadroid.notes.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.nostadroid.notes.model.FormatOption

@Composable
fun FormatOptionsCollapsible(
  icon: ImageVector,
  options: List<FormatOption>,
  withHTMLOptions: Boolean,
  isExpanded: Boolean,
  onToggle: (Boolean) -> Unit
) {
  if (isExpanded) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(
          space = 4.dp,
          alignment = Alignment.CenterHorizontally
        ),
        modifier = Modifier
          .weight(1f)
          .horizontalScroll(rememberScrollState())
      ) {
        for (option in options) {
          if (!withHTMLOptions && option.htmlRequired) continue
          FormatOptionButton(icon = option.icon, enabled = option.enabled, onClick = option.onClick)
        }
      }
      IconButton(
        onClick = { onToggle(false) },
        modifier = Modifier.padding(end = 24.dp)
      ) {
        Icon(Icons.Default.Close, contentDescription = null)
      }
    }
  } else {
    FormatOptionButton(icon = icon, enabled = false) { onToggle(true) }
  }
}