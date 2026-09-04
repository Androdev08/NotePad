package com.nostadroid.notes.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nostadroid.notes.screen.home.HomeViewModel

@Composable
fun FolderFilterChip(
  id: Int,
  currentId: Int,
  viewModel: HomeViewModel,
  text: String
) {
  val selected = id == currentId

  FilterChip(
    selected = selected,
    label = { Text(text) },
    leadingIcon = { if(selected)
      Icon(Icons.Default.Check, contentDescription = text, modifier = Modifier.size(FilterChipDefaults.IconSize)) },
    onClick = { viewModel.setSelectedChipId(id) }
  )
}