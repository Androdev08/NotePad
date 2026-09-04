package com.nostadroid.notes.screen.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.room.Room
import com.nostadroid.notes.R
import com.nostadroid.notes.db.AppDatabase
import com.nostadroid.notes.db.Folder
import com.nostadroid.notes.db.NoteEntity
import com.nostadroid.notes.screen.noteedit.NoteEditScreen
import com.nostadroid.notes.ui.ExpandableFAB
import com.nostadroid.notes.ui.FolderFilterChip
import com.nostadroid.notes.ui.NoteCard
import com.nostadroid.notes.ui.dialog.DialTimePicker
import com.nostadroid.notes.ui.dialog.ModalDatePicker
import com.nostadroid.notes.ui.dialog.folders.FoldersSheet
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
  val context = LocalContext.current

  val db = remember { Room.databaseBuilder(context, AppDatabase::class.java, "notes.db").build() }
  val noteDao = remember { db.noteDao() }
  val folderDao = remember { db.folderDao() }
  val viewModel: HomeViewModel = viewModel(
    factory = viewModelFactory {
      initializer {
        HomeViewModel(noteDao, folderDao)
      }
    }
  )
  var notePendingExport by remember { mutableStateOf<NoteEntity?>(null) }
  val filePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.CreateDocument("*/*")
  ) { uri ->
    if(uri != null && notePendingExport != null) {
      viewModel.writeNoteToUri(context, uri, notePendingExport!!)
    }
    notePendingExport = null
  }
  fun onExportClick(noteToExport: NoteEntity) {
    notePendingExport = noteToExport
    val isHtml = noteToExport.note.saveType == "HTML"
    val extension = if (isHtml) ".html" else ".md"
    val defaultFileName = "${noteToExport.note.displayTitle}${extension}"
      .replace(Regex("[\\\\/:*?\"<>|]"), "_")
    filePickerLauncher.launch(defaultFileName)
  }

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val fabExpanded = uiState.fabExpanded
  val searchQuery = uiState.searchQuery
  val allNotes by viewModel.notes.collectAsState()
  val currentFolders by viewModel.folders.collectAsState()
  val editingNote = uiState.editingNote
  val fromTapAndHold = uiState.fromTapAndHold
  val selectedChipId = uiState.selectedChipId
  val filteredNotes = remember(allNotes, selectedChipId, searchQuery) {
    allNotes.filter { noteEntity ->
      val matchesFolder =
        if (selectedChipId == 0) true else noteEntity.note.folderId == selectedChipId
      val matchesSearch = if (searchQuery.isBlank()) true else {
        noteEntity.note.title.contains(searchQuery, ignoreCase = true) ||
            noteEntity.note.content.contains(searchQuery, ignoreCase = true)
      }
      matchesFolder && matchesSearch
    }
  }

  // Create "Uncategorized" folder on app first launch
  LaunchedEffect(Unit) {
    if (currentFolders.isEmpty()) {
      viewModel.insertFolder(Folder(-1, "", 0))
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      topBar = {
        SearchBar(
          inputField = {
            SearchBarDefaults.InputField(
              query = searchQuery,
              onQueryChange = { viewModel.onSearchQueryChange(it) },
              expanded = false,
              onExpandedChange = { },
              onSearch = { _ -> },
              trailingIcon = {
                Row {
                  // Icon to show and manage folders
                  IconButton(
                    onClick = {
                      viewModel.setFromTapAndHold(false)
                      viewModel.setIsFolderSheetShowing(true)
                    }
                  ) { Icon(Icons.Outlined.Folder, contentDescription = null) }
                  // Icon to navigate to settings
                  IconButton(
                    onClick = { navController.navigate("settings_home") }
                  ) { Icon(Icons.Outlined.Settings, contentDescription = null)}
                }
              },
              placeholder = { Text(stringResource(R.string.search_notes_placeholder)) },
              modifier = Modifier,
            )
          },
          expanded = false,
          onExpandedChange = { },
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        ) { }
      }
    ) { innerPadding ->
      Surface(
        modifier = Modifier.padding(innerPadding)
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
        ) {
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 4.dp)
          ) {
            // Option to show notes in all folders
            item {
              FolderFilterChip(
                id = 0,
                currentId = selectedChipId,
                viewModel = viewModel,
                text = stringResource(R.string.all)
              )
            }
            // Options to filter based on folder
            items(currentFolders) { folder ->
              FolderFilterChip(
                id = folder.uid,
                currentId = selectedChipId,
                viewModel = viewModel,
                text = if (folder.name == "") stringResource(R.string.folder_sheet_uncategorized) else folder.name
              )
            }
          }
          Box(
            modifier = Modifier
              .fillMaxSize()
              .weight(1f)
          ) {
            if (allNotes.isEmpty() && searchQuery.isEmpty()) {
              Text(
                text = stringResource(R.string.home_screen_no_notes),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center)
              )
            } else {
              LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(128.dp),
              ) {
                items(
                  items = filteredNotes,
                  key = { note -> note.uid }
                ) { note ->
                  NoteCard(
                    currentNote = note,
                    viewModel = viewModel,
                    onExportClick = { onExportClick(note) },
                    modifier = Modifier.animateItem()
                  ) {
                    viewModel.setFromTapAndHold(false)
                    viewModel.onEditNote(note)
                  }
                }
              }
            }
            // Text that shows up if a search query is entered and no results are found
            if (searchQuery.isNotBlank() && filteredNotes.isEmpty()) {
              Text(
                text = stringResource(R.string.home_screen_no_results_found_search),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center)
              )
            }
          }
        }
      }
    }

    ExpandableFAB(
      fabExpanded, { viewModel.onFabExpandedChange(true) },
      onExpandedChange = { viewModel.onFabExpandedChange(it) },
      viewModel = viewModel,
      modifier = Modifier.align(Alignment.BottomEnd)
    )
    // Show the note editing screen if any note is selected
    AnimatedContent(
      targetState = editingNote,
      modifier = Modifier.fillMaxSize(),
      contentKey = { it?.uid }
    ) { noteToEdit ->
      if (noteToEdit != null && !fromTapAndHold) {
        NoteEditScreen(
          _inViewMode = true,
          note = noteToEdit,
          isShowing = { isShowing -> if (!isShowing) viewModel.onEditNote(null) },
          homeViewModel = viewModel,
          onExportClick = { onExportClick(noteToEdit) }
        )
      }
    }
  }

  // Dialogs
  if (uiState.isDatePickerShowing) {
    val noteTimestamp =
      uiState.noteTargetingDateChange?.note?.timestamp ?: System.currentTimeMillis()
    ModalDatePicker(
      noteDate = noteTimestamp,
      onDateSelected = { dateMillis ->
        viewModel.setIsTimePickerShowing(true, dateMillis = dateMillis)
        viewModel.setIsDatePickerShowing(false)
      },
      onDismiss = { viewModel.setIsDatePickerShowing(false) }
    )
  }
  if (uiState.isTimePickerShowing) {
    val targetTimestamp =
      uiState.noteTargetingDateChange?.note?.timestamp ?: System.currentTimeMillis()
    val zonedTime =
      ZonedDateTime.ofInstant(Instant.ofEpochMilli(targetTimestamp), ZoneId.systemDefault())
    DialTimePicker(
      initialHour = zonedTime.hour,
      initialMinute = zonedTime.minute,
      onConfirm = { hour, minute -> viewModel.completeDateTimeChange(hour, minute) },
      onDismiss = { viewModel.setIsTimePickerShowing(false) }
    )
  }
  if (uiState.isFolderSheetShowing) {
    val sheetInViewMode = !uiState.fromTapAndHold && uiState.editingNote == null
    FoldersSheet(
      sheetInViewMode,
      uiState.editingNote,
      viewModel
    ) {
      viewModel.setIsFolderSheetShowing(false)
      if (uiState.fromTapAndHold) {
        viewModel.onEditNote(null)
        viewModel.setFromTapAndHold(false)
      }
    }
  }
}