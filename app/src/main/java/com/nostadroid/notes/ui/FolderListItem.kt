package com.nostadroid.notes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nostadroid.notes.db.Folder

@Composable
fun FolderListItem(
  folder: Folder,
  inViewMode: Boolean,
  onDeleteClick: () -> Unit,
  onClick: () -> Unit
) {
  Surface(
    color = MaterialTheme.colorScheme.surfaceContainerHigh,
    modifier = Modifier
      .fillMaxWidth()
      .defaultMinSize(minHeight = 48.dp)
      .clip(RoundedCornerShape(12.dp))
      .let {
        if (!inViewMode) it.clickable(onClick = onClick)
        else it
      },
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.padding(8.dp)
    ) {
      Text(folder.name,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.weight(1f)
      )
      Text(folder.notesCount.toString())
      if(inViewMode && folder.uid >= 0) Icon(
        Icons.Outlined.Delete,
        contentDescription = null,
        modifier = Modifier
          .padding(4.dp)
          .background(MaterialTheme.colorScheme.surfaceContainerHigh)
          .clickable(onClick = onDeleteClick)
      )
    }
  }
}

@Preview
@Composable
fun FolderListItemPreview() {
  FolderListItem(Folder(0, "Work", 5), false, {}) {}
}