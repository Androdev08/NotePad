package com.nostadroid.notes.model

import androidx.compose.ui.graphics.vector.ImageVector

data class FormatOption(
  val icon: ImageVector,
  val enabled: Boolean,
  val htmlRequired: Boolean,
  val onClick: (Boolean) -> Unit
)
