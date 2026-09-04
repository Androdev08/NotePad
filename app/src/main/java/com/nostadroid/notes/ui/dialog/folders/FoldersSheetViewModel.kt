package com.nostadroid.notes.ui.dialog.folders

import androidx.lifecycle.ViewModel
import com.nostadroid.notes.db.Folder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FolderSheetUiState(
  val isNewFolderDialogShowing: Boolean = false,
  val isDeleteConfirmDialogShowing: Boolean = false,
  val folderToDelete: Folder? = null
)

class FoldersSheetViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(FolderSheetUiState())
  val uiState: StateFlow<FolderSheetUiState> = _uiState.asStateFlow()

  // Variable setters
  fun setFolderToDelete(folder: Folder?) {
    _uiState.value = _uiState.value.copy(folderToDelete = folder)
  }
  fun setIsNewFolderDialogShowing(value: Boolean) {
    _uiState.value = _uiState.value.copy(isNewFolderDialogShowing = value)
  }
  fun setIsDeleteConfirmDialogShowing(value: Boolean) {
    _uiState.value = _uiState.value.copy(isDeleteConfirmDialogShowing = value)
  }
}