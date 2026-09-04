package com.nostadroid.notes.screen.noteedit

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.HeadingStyle
import com.mohamedrejeb.richeditor.ui.BasicRichText
import com.mohamedrejeb.richeditor.ui.BasicRichTextEditor
import com.nostadroid.notes.R
import com.nostadroid.notes.datastore.SettingsManager
import com.nostadroid.notes.db.NoteEntity
import com.nostadroid.notes.screen.home.HomeViewModel
import com.nostadroid.notes.screen.settings.SettingsHomeViewModel
import com.nostadroid.notes.ui.FormatOptionButton
import com.nostadroid.notes.ui.FormatOptionsCollapsible
import com.nostadroid.notes.ui.HeadingMenuItem
import com.nostadroid.notes.ui.NoteTitleTextField
import com.nostadroid.notes.ui.dialog.AddLinkDialog
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class, ExperimentalRichTextApi::class)
@Composable
fun NoteEditScreen(
  _inViewMode: Boolean,
  note: NoteEntity?,
  homeViewModel: HomeViewModel,
  onExportClick: (NoteEntity?) -> Unit,
  isShowing: (Boolean) -> Unit
) {
  val context = LocalContext.current
  val noteScrollState = rememberScrollState()

  val viewModel = viewModel(NoteEditViewModel::class) // The current view model
  val settingsViewModel: SettingsHomeViewModel = viewModel(
    factory = viewModelFactory {
      initializer {
        SettingsHomeViewModel(SettingsManager(context))
      }
    }
  )

  // Get settings
  val defaultSaveType by settingsViewModel.saveTypeState.collectAsStateWithLifecycle()

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  val inViewMode = uiState.inViewMode
  val isHeadingMenuShowing = uiState.isHeadingMenuShowing
  val isLinkDialogShowing = uiState.isLinkDialogShowing
  val isDropdownMenuShowing = uiState.isDropdownMenuShowing

  // Info about the note
  val currentNote = note?.note
  val withAutoTitle = currentNote?.autoTitle ?: false
  val folderName = currentNote?.folderName ?: ""

  // Get the timestamp
  val activeTimestamp = uiState.currentTimestamp
  val timestampInstant = Instant.ofEpochMilli(
    if (activeTimestamp == 0L) System.currentTimeMillis() else activeTimestamp
  )
  val zonedTime = ZonedDateTime.ofInstant(timestampInstant, ZoneId.systemDefault())
  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

  val initialTitle =
    if (_inViewMode && !withAutoTitle) currentNote?.title.orEmpty() else "" // If a title was automatically generated, don't show it in the text field, otherwise show the title given by the user
  val initialBody =
    if (_inViewMode) { // If a title was automatically generated, compose the body content from the auto title and the body, otherwise show the body given by the user
      if (withAutoTitle) "${currentNote.title}\n${currentNote.content}".trim()
      else currentNote?.content.orEmpty()
    } else "" // Brand-new note starts completely empty
  val initialBodyMarkdown = remember(initialBody) { initialBody }

  val titleTextFieldState = viewModel.titleTextFieldState
  val bodyRichTextState = viewModel.bodyRichTextState

  // Determine save type
  val activeSaveType = if (_inViewMode && currentNote != null) currentNote.saveType else defaultSaveType

  val isHTML = activeSaveType == "HTML"

  // Create the categories list
  val categories = remember(bodyRichTextState.currentSpanStyle, bodyRichTextState.currentParagraphStyle, isHTML) {
    FormatOptionsBuilder.buildCategories(bodyRichTextState, viewModel, isHTML)
  }

  LaunchedEffect(note?.uid, _inViewMode, activeSaveType) {
    viewModel.initializeForNote(
      noteId = note?.uid,
      initialInViewMode = _inViewMode,
      title = initialTitle,
      content = initialBody,
      contentType = activeSaveType,
      initialTimestamp = currentNote?.timestamp ?: System.currentTimeMillis()
    )
  }
  LaunchedEffect(note?.note?.timestamp) {
    note?.note?.timestamp?.let { freshTimestamp ->
      viewModel.updateTimestampDirectly(freshTimestamp)
    }
  }

  // Determine the actions to do when exiting the screen
  fun onExit() {
    val currentTitleText = titleTextFieldState.text.toString()
    val currentBody = if (activeSaveType == "Markdown") {
      bodyRichTextState.toMarkdown()
    } else {
      bodyRichTextState.toHtml()
    }

    viewModel.saveOrUpdateNote(
      uid = note?.uid ?: 0,
      currentTitleText = currentTitleText,
      currentBody = currentBody,
      initialTitle = initialTitle,
      initialBodyMarkdown = initialBodyMarkdown,
      activeSaveType = activeSaveType,
      folderName = folderName,
      inViewMode = _inViewMode,
      note = note,
      homeViewModel = homeViewModel,
      onComplete = { isShowing(false) }
    )
  }

  Surface(
    modifier = Modifier
      .fillMaxSize()
      .safeDrawingPadding()
  ) {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(stringResource(if (_inViewMode) R.string.edit_note_screen_title else R.string.new_note_screen_title)) },
          navigationIcon = {
            IconButton(onClick = { onExit() }) {
              Icon(Icons.Default.Close, contentDescription = null)
            }
          },
          actions = {
            if (inViewMode) {
              // An edit button
              IconButton(onClick = { viewModel.setInViewMode(false) }) {
                Icon(
                  Icons.Outlined.Edit,
                  contentDescription = null
                )
              }
            } else {
              // Undo and redo buttons
              IconButton(
                onClick = { bodyRichTextState.history.undo() },
                enabled = bodyRichTextState.history.canUndo
              ) { Icon(Icons.AutoMirrored.Default.Undo, contentDescription = null) }
              IconButton(
                onClick = { bodyRichTextState.history.redo() },
                enabled = bodyRichTextState.history.canRedo
              ) { Icon(Icons.AutoMirrored.Default.Redo, contentDescription = null) }
            }
            // Three dot menu, shows up only in saved notes and not in new ones
            if (_inViewMode) {
              Box {
                IconButton(
                  onClick = { viewModel.setIsDropdownMenuShowing(!isDropdownMenuShowing) }
                ) { Icon(Icons.Default.MoreVert, contentDescription = null) }
                DropdownMenu(
                  expanded = isDropdownMenuShowing,
                  onDismissRequest = { viewModel.setIsDropdownMenuShowing(false) }) {
                  DropdownMenuItem(
                    text = { Text(stringResource(R.string.note_context_menu_move_to_folder)) },
                    onClick = {
                      viewModel.setIsDropdownMenuShowing(false)
                      homeViewModel.setIsFolderSheetShowing(true)
                    }
                  )
                  DropdownMenuItem(
                    text = { Text(stringResource(R.string.note_context_menu_export)) },
                    onClick = {
                      viewModel.setIsDropdownMenuShowing(false)
                      onExportClick(note)
                    }
                  )
                  DropdownMenuItem(
                    text = { Text(stringResource(R.string.note_context_menu_change_date)) },
                    onClick = {
                      viewModel.setIsDropdownMenuShowing(false)
                      homeViewModel.setIsDatePickerShowing(true, note = note)
                    }
                  )
                  DropdownMenuItem(
                    text = { Text(stringResource(R.string.note_context_menu_delete)) },
                    onClick = {
                      homeViewModel.deleteNote(note!!)
                      isShowing(false)
                    }
                  )
                }
              }
            }
          }
        )
      }
    ) { innerPadding ->
      val titleFocusRequester = remember { FocusRequester() }
      val bodyFocusRequester = remember { FocusRequester() }

      Column(
        modifier = Modifier.padding(innerPadding)
      ) {
        Column(
          modifier = Modifier
            .padding(horizontal = 16.dp)
            .weight(1f)
        ) {
          // Title text field
          NoteTitleTextField(
            state = titleTextFieldState,
            inViewMode = inViewMode,
            focusRequester = titleFocusRequester,
            textStyle = MaterialTheme.typography.titleLarge,
            placeholder = stringResource(R.string.note_edit_title_textbox_placeholder),
            modifier = Modifier.fillMaxWidth()
          )
          // Timestamp label
          Text(zonedTime.format(dateFormatter), style = MaterialTheme.typography.labelSmall)
          Spacer(Modifier.height(16.dp))
          // Body text field
          if (!inViewMode) BasicRichTextEditor(
            state = bodyRichTextState,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
            textStyle = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.onBackground),
            decorationBox = { innerTextField ->
              Box {
                if (bodyRichTextState.annotatedString.text.isEmpty()) {
                  Text(
                    text = stringResource(R.string.note_edit_body_textbox_placeholder),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
                innerTextField()
              }
            },
            modifier = Modifier
              .fillMaxSize()
              .focusRequester(bodyFocusRequester)
              .verticalScroll(noteScrollState)
          ) else BasicRichText(
            state = bodyRichTextState,
            style = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
              .fillMaxSize()
              .verticalScroll(noteScrollState)
          )
        }
        // A button row that will hold buttons for formatting the text
        var activeCategory by rememberSaveable { mutableStateOf<String?>(null) }
        AnimatedContent(activeCategory) { currentActiveCategory ->
          Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            modifier = Modifier
              .fillMaxWidth()
              .padding(8.dp)
          ) {
            if (currentActiveCategory != null) {
              val activeMenu = categories.find { it.type == currentActiveCategory }
              if (activeMenu != null) {
                FormatOptionsCollapsible(
                  icon = activeMenu.icon,
                  options = activeMenu.options,
                  withHTMLOptions = isHTML,
                  isExpanded = true,
                  onToggle = { isExpanded -> if (!isExpanded) activeCategory = null }
                )
              }
            } else {
              categories.forEach { category ->
                FormatOptionsCollapsible(
                  icon = category.icon,
                  options = category.options,
                  withHTMLOptions = isHTML,
                  isExpanded = false,
                  onToggle = { isExpanding -> if (isExpanding) activeCategory = category.type }
                )
              }

              FormatOptionButton(Icons.Default.Link, bodyRichTextState.isLink) { enabled ->
                if (enabled) viewModel.setIsLinkDialogShowing(true) else bodyRichTextState.removeLink()
              }
            }
          }
        }
        LaunchedEffect(inViewMode) {
          delay(100.milliseconds) // Delay to avoid any problems with the soft keyboard pushing the content off-screen
          if (!inViewMode) bodyFocusRequester.requestFocus()
        }
      }
    }
    // Dialogs
    // Headings
    if (isHeadingMenuShowing) HeadingMenu(onDismissRequest = {
      viewModel.setIsHeadingMenuShowing(false)
    }) {
      bodyRichTextState.setHeadingStyle(HeadingStyle.fromLevel(it))
    }
    // Add link
    if (isLinkDialogShowing) AddLinkDialog(
      initialText = if (bodyRichTextState.selection.collapsed) "" else
        bodyRichTextState.toText().substring(bodyRichTextState.selection),
      onDismissRequest = { viewModel.setIsLinkDialogShowing(false) }
    ) { text, url ->
      val finalUrl = viewModel.formatUrl(url)

      if (bodyRichTextState.selection.collapsed) {
        bodyRichTextState.addLink(text, finalUrl)
      } else {
        bodyRichTextState.addLinkToTextRange(finalUrl, bodyRichTextState.selection)
      }
      viewModel.setIsLinkDialogShowing(false)
    }
    // Exit the screen when back is pressed
    BackHandler { onExit() }

    // Monitor selection and text indices to clear out residual line-break styles safely
    LaunchedEffect(bodyRichTextState.selection) {
      val text = bodyRichTextState.toText()
      val selectionStart = bodyRichTextState.selection.start

      if (selectionStart > 0 && selectionStart <= text.length) {
        // If the character immediately preceding the cursor is a newline, it means the user just pressed Enter or soft wrap committed a break block.
        if (text[selectionStart - 1] == '\n') {
          if (bodyRichTextState.currentHeadingStyle != HeadingStyle.Normal) {
            bodyRichTextState.setHeadingStyle(HeadingStyle.Normal)
          }
          // Instantly clear trailing parent bold weights out of the active cursor configuration
          if (bodyRichTextState.currentSpanStyle.fontWeight == FontWeight.Bold) {
            bodyRichTextState.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
          }
          // Clear the inherited font size from the typing cursor context
          val inheritedFontSize = bodyRichTextState.currentSpanStyle.fontSize
          if (inheritedFontSize != TextUnit.Unspecified) {
            bodyRichTextState.toggleSpanStyle(SpanStyle(fontSize = inheritedFontSize))
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeadingMenu(onDismissRequest: () -> Unit, menuCallback: (Int) -> Unit) {
  ModalBottomSheet(onDismissRequest = onDismissRequest) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      for (level in 0..6) {
        HeadingMenuItem(level) {
          menuCallback(level)
          onDismissRequest()
        }
      }
    }
  }
}