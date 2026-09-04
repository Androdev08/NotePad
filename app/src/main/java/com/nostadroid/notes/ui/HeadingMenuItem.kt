package com.nostadroid.notes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nostadroid.notes.R

@Composable
fun HeadingMenuItem(level: Int, onClick: () -> Unit) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .padding(horizontal = 8.dp)
      .clickable(onClick = onClick)
      .fillMaxWidth(),
  ) {
    Text(
      text = if(level == 0) stringResource(R.string.note_edit_heading_menu_item_normal) else stringResource(R.string.note_edit_heading_menu_item_label, level),
      fontWeight = if(level != 0) FontWeight.Bold else FontWeight.Normal,
      style = MaterialTheme.typography.bodyMedium,
      fontSize = when(level) {
        1 -> 32.051.sp
        2 -> 24.038.sp
        3 -> 18.75.sp
        4 -> 17.949.sp
        5 -> 13.301.sp
        6 -> 12.019.sp
        else -> MaterialTheme.typography.bodyMedium.fontSize // When it's 0
      },
      modifier = Modifier
        .padding(vertical = 8.dp)
        .defaultMinSize(minHeight = 24.dp)
    )
  }
}