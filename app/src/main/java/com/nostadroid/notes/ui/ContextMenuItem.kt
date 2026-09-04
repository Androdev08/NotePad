package com.nostadroid.notes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ContextMenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    modifier = Modifier
      .padding(horizontal = 16.dp)
      .clickable(onClick = onClick)
  ) {
    Icon(icon, contentDescription = null, modifier = Modifier.padding(vertical = 16.dp))
    Text(label, style = MaterialTheme.typography.titleMedium, modifier = Modifier
      .padding(8.dp)
      .weight(1f)
    )
  }
}