package com.nostadroid.notes.model

import androidx.compose.ui.graphics.vector.ImageVector

data class FormatCategory(
  val type: String,
  val icon: ImageVector,
  val options: List<FormatOption>,
  val htmlOnly: Boolean = false
)
