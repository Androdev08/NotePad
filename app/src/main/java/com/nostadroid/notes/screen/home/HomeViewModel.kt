package com.nostadroid.notes.screen.home

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nostadroid.notes.db.Folder
import com.nostadroid.notes.db.FolderDao
import com.nostadroid.notes.db.NoteDao
import com.nostadroid.notes.db.NoteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

data class HomeUiState(
  val searchQuery: String = "",
  val fabExpanded: Boolean = false,
  val editingNote: NoteEntity? = null,
  val noteTargetingDateChange: NoteEntity? = null,
  val isDatePickerShowing: Boolean = false,
  val isTimePickerShowing: Boolean = false,
  val temporarySelectedDateMillis: Long = 0L,
  val isFolderSheetShowing: Boolean = false,
  val fromTapAndHold: Boolean = false,
  val selectedChipId: Int = 0
)

class HomeViewModel(private val noteDao: NoteDao, private val folderDao: FolderDao) : ViewModel() {
  private val _uiState = MutableStateFlow(HomeUiState())
  val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
  val notes: StateFlow<List<NoteEntity>> = noteDao.getAll()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )
  val folders: StateFlow<List<Folder>> = folderDao.getAll()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  // Database actions
  fun insertNote(note: NoteEntity, onFinish: () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      noteDao.insertAll(note)
      withContext(Dispatchers.Main) {
        onFinish()
      }
    }
  }
  fun deleteNote(note: NoteEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      noteDao.delete(note)
    }
  }
  fun deleteMultipleNotes(vararg notes: NoteEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      for(note in notes) {
        noteDao.delete(note)
      }
    }
  }
  fun insertFolder(folder: Folder) {
    viewModelScope.launch(Dispatchers.IO) {
      folderDao.insertAll(folder)
    }
  }
  fun deleteFolder(folder: Folder?) {
    viewModelScope.launch(Dispatchers.IO) {
      folderDao.delete(folder!!)
    }
  }

  // Variable setters
  fun onSearchQueryChange(newQuery: String) {
    _uiState.update { it.copy(searchQuery = newQuery) }
  }
  fun onFabExpandedChange(expanded: Boolean) {
    _uiState.update { it.copy(fabExpanded = expanded) }
  }
  fun onEditNote(note: NoteEntity?) {
    _uiState.update { it.copy(editingNote = note) }
  }
  fun setIsDatePickerShowing(showing: Boolean, note: NoteEntity? = null) {
    _uiState.update {
      it.copy(
        isDatePickerShowing = showing,
        noteTargetingDateChange = note ?: it.noteTargetingDateChange
      )
    }
  }
  fun setIsTimePickerShowing(showing: Boolean, dateMillis: Long = 0L) {
    _uiState.update {
      it.copy(
        isTimePickerShowing = showing,
        temporarySelectedDateMillis = if (dateMillis != 0L) dateMillis else it.temporarySelectedDateMillis
      )
    }
  }
  fun setIsFolderSheetShowing(value: Boolean) {
    _uiState.update { it.copy(isFolderSheetShowing = value) }
  }
  fun setFromTapAndHold(value: Boolean) {
    _uiState.update { it.copy(fromTapAndHold = value) }
  }
  fun setSelectedChipId(value: Int) {
    _uiState.update { it.copy(selectedChipId = value) }
  }

  // Utility method to effectively perform a date change to a note
  fun completeDateTimeChange(hour: Int, minute: Int) {
    val state = _uiState.value
    val targetNoteEntity = state.noteTargetingDateChange ?: return
    val dateMillis = state.temporarySelectedDateMillis
    // Calculate time aligned to local system timezones properly
    val localDate = Instant.ofEpochMilli(dateMillis).atZone(ZoneId.of("UTC")).toLocalDate()
    val localZonedDateTime = ZonedDateTime.of(localDate, java.time.LocalTime.of(hour, minute), ZoneId.systemDefault())
    val finalTimestamp = localZonedDateTime.toInstant().toEpochMilli()
    // Perform the database update
    viewModelScope.launch(Dispatchers.IO) {
      val updatedNoteEntity = targetNoteEntity.copy(note = targetNoteEntity.note.copy(timestamp = finalTimestamp))
      noteDao.insertAll(updatedNoteEntity)
      // Clear tracking state
      _uiState.update {
        it.copy(
          isTimePickerShowing = false,
          noteTargetingDateChange = null,
          temporarySelectedDateMillis = 0L,
          editingNote = if (it.editingNote?.uid == targetNoteEntity.uid) updatedNoteEntity else it.editingNote
        )
      }
    }
  }

  fun writeNoteToUri(context: Context, uri: Uri?, noteEntity: NoteEntity) {
    if (uri == null) return

    viewModelScope.launch(Dispatchers.IO) {
      try {
        context.contentResolver.openFileDescriptor(uri, "w")?.use { pfd ->
          FileOutputStream(pfd.fileDescriptor).use { os ->
            val content = if(noteEntity.note.autoTitle) noteEntity.note.title + "\n" + noteEntity.note.content
            else noteEntity.note.content
            os.write(content.toByteArray(Charsets.UTF_8))
          }
        }
      } catch(e: Exception) {
        throw RuntimeException(e)
      }
    }
  }
}