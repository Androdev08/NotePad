package com.nostadroid.notes.ui

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nostadroid.notes.R
import com.nostadroid.notes.db.NoteEntity
import com.nostadroid.notes.screen.home.HomeViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(
  currentNote: NoteEntity,
  viewModel: HomeViewModel,
  onExportClick: (NoteEntity) -> Unit,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  var showContextMenu by rememberSaveable { mutableStateOf(false) }
  val note = currentNote.note

  val timestampInstant = Instant.ofEpochMilli(note.timestamp)
  val zonedTime = ZonedDateTime.ofInstant(timestampInstant, ZoneId.systemDefault())
  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

  Card(
    modifier = modifier
      .fillMaxSize()
      .defaultMinSize(minHeight = 64.dp)
      .padding(4.dp)
      .combinedClickable(
        onClick = onClick,
        onLongClick = {
          showContextMenu = true
          viewModel.setFromTapAndHold(true)
          viewModel.onEditNote(currentNote)
        }
      ),
    elevation = CardDefaults.cardElevation()
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(4.dp),
      modifier = Modifier.padding(4.dp),
    ) {
      Text(note.displayTitle,
        style = MaterialTheme.typography.titleLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Text(note.displayContent,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 4,
        overflow = TextOverflow.Ellipsis
      )
      Text(zonedTime.format(dateFormatter),
        style = MaterialTheme.typography.labelSmall
      )
    }
  }

  // Context menu for each note
  if(showContextMenu) {
    ModalBottomSheet(onDismissRequest = {
      viewModel.onEditNote(null)
      showContextMenu = false
    }) {
      Column {
        // Option to move note to folder
        ContextMenuItem(Icons.Outlined.Folder, stringResource(R.string.note_context_menu_move_to_folder)) {
          showContextMenu = false
          viewModel.setFromTapAndHold(true)
          viewModel.setIsFolderSheetShowing(true)
        }
        // Option to export the note to .md/.html file
        ContextMenuItem(Icons.Outlined.FileDownload, stringResource(R.string.note_context_menu_export)) {
          showContextMenu = false
          onExportClick(currentNote)
        }
        // Option to change date of the note
        ContextMenuItem(Icons.Outlined.EditCalendar, stringResource(R.string.note_context_menu_change_date)) {
          showContextMenu = false
          viewModel.setIsDatePickerShowing(true, note = currentNote)
        }
        // Option to delete the note
        ContextMenuItem(Icons.Outlined.Delete, stringResource(R.string.note_context_menu_delete)) {
          showContextMenu = false
          viewModel.deleteNote(currentNote)
        }
      }
    }
  }
}