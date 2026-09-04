package com.nostadroid.notes.ui.dialog.folders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nostadroid.notes.R
import com.nostadroid.notes.db.Folder
import com.nostadroid.notes.db.NoteEntity
import com.nostadroid.notes.screen.home.HomeViewModel
import com.nostadroid.notes.ui.FolderListItem
import com.nostadroid.notes.ui.dialog.TextInputDialog
import com.nostadroid.notes.ui.dialog.YesNoDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoldersSheet(
  inViewMode: Boolean,
  noteEntity: NoteEntity?,
  homeViewModel: HomeViewModel,
  onDismiss: () -> Unit
) {
  val folderList by homeViewModel.folders.collectAsStateWithLifecycle(initialValue = emptyList())
  val allNotes by homeViewModel.notes.collectAsStateWithLifecycle(initialValue = emptyList())

  val viewModel: FoldersSheetViewModel = viewModel()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  ModalBottomSheet(onDismissRequest = onDismiss) {
    Column(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.padding(16.dp)
    ) {
      OutlinedButton(
        onClick = { viewModel.setIsNewFolderDialogShowing(true) },
        modifier = Modifier.fillMaxWidth()
      ) { Text(stringResource(R.string.folders_dialog_new_folder)) }
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // "Uncategorized" option, i.e. no folder and only shows up in "All"
        folderList.forEach { folder ->
          if(folder.name != "") {
            val noteCount = allNotes.count { it.note.folderId == folder.uid }
            val folderWithNoteCount = folder.copy(notesCount = noteCount)
            FolderListItem(
              folder = folderWithNoteCount,
              inViewMode = inViewMode,
              onDeleteClick = {
                viewModel.setFolderToDelete(folderWithNoteCount)
                if(noteCount > 0) viewModel.setIsDeleteConfirmDialogShowing(true)
                else {
                  // Empty folder, delete with no dialog
                  homeViewModel.deleteFolder(folderWithNoteCount)
                  homeViewModel.deleteMultipleNotes(*allNotes.filter { it.note.folderId == folderWithNoteCount.uid }.toTypedArray())
                  homeViewModel.onEditNote(null)
                }
              }
            ) {
              onDismiss() // Close the sheet
              // Insert the note into the database
              val updatedNote = noteEntity!!.note.copy(folderName = folder.name, folderId = folder.uid)
              val updatedNoteEntity = noteEntity.copy(note = updatedNote)
              homeViewModel.insertNote(updatedNoteEntity) { if(inViewMode) homeViewModel.onEditNote(null) }
            }
          } else {
            // "Uncategorized" option, i.e. no folder and only shows up in "All"
            val uncategorizedCount = allNotes.count { it.note.folderId == folder.uid || it.note.folderId <= 0 }
            FolderListItem(
              folder = Folder(
                uid = folder.uid,
                name = stringResource(R.string.folder_sheet_uncategorized),
                notesCount = uncategorizedCount,
              ),
              onDeleteClick = {},
              inViewMode = inViewMode
            ) {
              onDismiss()
              // Move note back to default Uncategorized state
              val updatedNote = noteEntity!!.note.copy(folderName = "", folderId = folder.uid)
              homeViewModel.insertNote(noteEntity.copy(note = updatedNote)) { if(inViewMode) homeViewModel.onEditNote(null) }
            }
          }
        }
      }
    }
  }

  // Dialogs
  if(uiState.isNewFolderDialogShowing)
    TextInputDialog(
      title = stringResource(R.string.folders_dialog_new_folder),
      placeholder = stringResource(R.string.new_folder_dialog_name_placeholder),
      errorText = stringResource(R.string.new_folder_dialog_name_error),
      onConfirm = { name ->
        homeViewModel.insertFolder(Folder(0, name, 0))
      }
    ) { viewModel.setIsNewFolderDialogShowing(false) }
  if(uiState.isDeleteConfirmDialogShowing)
    YesNoDialog( // Delete a folder; show a confirmation dialog first
      title = stringResource(R.string.folder_sheet_delete_folder_dialog_title),
      content = stringResource(
        if(uiState.folderToDelete!!.notesCount == 1) R.string.folder_sheet_delete_folder_dialog_content_one_item
        else R.string.folder_sheet_delete_folder_dialog_content,
        uiState.folderToDelete!!.name, uiState.folderToDelete!!.notesCount),
      onYesClick = {
        homeViewModel.deleteFolder(uiState.folderToDelete)
        homeViewModel.deleteMultipleNotes(*allNotes.filter { it.note.folderId == uiState.folderToDelete!!.uid }.toTypedArray())
        homeViewModel.onEditNote(null)
        viewModel.setIsDeleteConfirmDialogShowing(false)
      },
      onNoClick = {
        viewModel.setIsDeleteConfirmDialogShowing(false)
        viewModel.setFolderToDelete(null)
      }
    ) {
      viewModel.setIsDeleteConfirmDialogShowing(false)
      viewModel.setFolderToDelete(null)
    }
}