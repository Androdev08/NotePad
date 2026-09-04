package com.nostadroid.notes.screen.noteedit

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import com.mohamedrejeb.richeditor.model.RichTextState
import com.nostadroid.notes.db.NoteEntity
import com.nostadroid.notes.model.Note
import com.nostadroid.notes.screen.home.HomeViewModel
import com.nostadroid.notes.util.stripFormatting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class NoteEditState(
  val inViewMode: Boolean = false,
  val isHeadingMenuShowing: Boolean = false,
  val isLinkDialogShowing: Boolean = false,
  val isDropdownMenuShowing: Boolean = false,
  val currentTimestamp: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
class NoteEditViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(NoteEditState())
  val uiState: StateFlow<NoteEditState> = _uiState.asStateFlow()

  val titleTextFieldState = TextFieldState("")
  val bodyRichTextState = RichTextState()

  private var loadedNoteId: Int? = -1

  fun initializeForNote(noteId: Int?, initialInViewMode: Boolean, title: String,
                        content: String, contentType: String, initialTimestamp: Long) {
    _uiState.update {
      it.copy(
        inViewMode = initialInViewMode,
        isHeadingMenuShowing = false,
        isLinkDialogShowing = false,
        isDropdownMenuShowing = false,
        currentTimestamp = initialTimestamp
      )
    }
    // If the note target has changed (or it's the first run), reload entirely
    if (loadedNoteId != noteId|| noteId == 0) {
      loadedNoteId = noteId
      // Safely swap out text buffers completely
      titleTextFieldState.edit { replace(0, length, title) }
      if(contentType == "Markdown") bodyRichTextState.setMarkdown(content)
      else bodyRichTextState.setHtml(content)
      bodyRichTextState.history.clear() // Clean history undo/redo stack for the new note
      _uiState.update { it.copy(currentTimestamp = initialTimestamp) }
    }
  }
  fun setInViewMode(value: Boolean) {
    _uiState.update { it.copy(inViewMode = value) }
  }
  fun setIsHeadingMenuShowing(value: Boolean) {
    _uiState.update { it.copy(isHeadingMenuShowing = value) }
  }
  fun setIsLinkDialogShowing(value: Boolean) {
    _uiState.update { it.copy(isLinkDialogShowing = value) }
  }
  fun setIsDropdownMenuShowing(value: Boolean) {
    _uiState.update { it.copy(isDropdownMenuShowing = value) }
  }
  fun updateTimestampDirectly(newTimestamp: Long) {
    _uiState.update { it.copy(currentTimestamp = newTimestamp) }
  }
  fun clearState() {
    loadedNoteId = -1
    titleTextFieldState.edit { replace(0, length, "") }
    bodyRichTextState.setMarkdown("")
    bodyRichTextState.history.clear()
  }

  fun formatUrl(url: String): String {
    val trimmed = url.trim()
    return if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
      "https://$trimmed"
    } else {
      trimmed
    }
  }

  fun saveOrUpdateNote(
    uid: Int,
    currentTitleText: String,
    currentBody: String,
    initialTitle: String,
    initialBodyMarkdown: String,
    activeSaveType: String,
    folderName: String,
    inViewMode: Boolean,
    note: NoteEntity?,
    homeViewModel: HomeViewModel,
    onComplete: () -> Unit
  ) {
    val isHTML = activeSaveType == "HTML"
    val autoTitle = currentTitleText.isEmpty()

    // Check if anything actually changed
    if (currentTitleText == initialTitle && currentBody == initialBodyMarkdown) {
      clearState()
      onComplete()
      return
    }

    // Format unstyled lines for previews
    val unstyledLines = if (isHTML) {
      stripFormatting(currentBody.replace(Regex("(?i)</p>|</div>|<br\\s*/?>"), "\n"))
        .split("\n")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
    } else {
      currentBody.split("\n").map { stripFormatting(it) }
    }

    var title = currentTitleText
    var bodyContent = currentBody

    // Handle auto-title generation
    if (autoTitle && currentBody.isNotBlank()) {
      val lines = currentBody.split("\n")
      title = lines[0]
      bodyContent = if (lines.size > 1) lines.drop(1).joinToString("\n") else ""
    }

    // Strip formatting for card display
    val strippedTitle: String
    val strippedBodyContent: String

    if (autoTitle) {
      strippedTitle = stripFormatting(title)
      strippedBodyContent = unstyledLines.drop(1).take(4).joinToString("\n") { stripFormatting(it) }
    } else {
      strippedTitle = title
      strippedBodyContent = unstyledLines.take(4).joinToString("\n") { stripFormatting(it) }
    }

    // Save, delete, or ignore based on content validity
    if (strippedTitle.isNotBlank() || strippedBodyContent.isNotBlank()) {
      homeViewModel.insertNote(
        NoteEntity(
          uid,
          Note(
            title, autoTitle, bodyContent, System.currentTimeMillis(), folderName, -1,
            strippedTitle, strippedBodyContent, activeSaveType
          )
        )
      ) {
        clearState()
        onComplete()
      }
    } else {
      if (inViewMode && note != null) {
        homeViewModel.deleteNote(note)
      }
      clearState()
      onComplete()
    }
  }
}