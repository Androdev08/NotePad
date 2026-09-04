package com.nostadroid.notes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun FormatOptionButton(
  icon: ImageVector,
  enabled: Boolean,
  onClick: (Boolean) -> Unit
) {
  IconToggleButton(
    checked = enabled,
    onCheckedChange = onClick,
    modifier = Modifier
      .background(if(enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface, CircleShape)
    ) {
    Icon(icon,
      contentDescription = null,
      tint = if(enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    )
  }
}