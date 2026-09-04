package com.nostadroid.notes.screen.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.nostadroid.notes.R
import com.nostadroid.notes.datastore.SettingsManager
import com.nostadroid.notes.ui.settingsitems.SingleChoiceSettingsItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsHomeScreen(navController: NavHostController) {
  val viewModel: SettingsHomeViewModel = viewModel(
    factory = viewModelFactory {
      initializer {
        SettingsHomeViewModel(SettingsManager(navController.context))
      }
    }
  )

  // Get settings
  val currentSaveType by viewModel.saveTypeState.collectAsStateWithLifecycle()

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(stringResource(R.string.settings_page_header)) },
        navigationIcon = {
          IconButton(onClick = { navController.navigateUp() }) { Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null) }
        }
      )
    }
  ) { innerPadding ->
    Box(modifier = Modifier.padding(innerPadding + PaddingValues(8.dp))) {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column {
          SingleChoiceSettingsItem(
            title = stringResource(R.string.settings_default_note_save_type),
            currentValue = currentSaveType,
            options = listOf(stringResource(R.string.settings_default_note_save_type_markdown),
            stringResource(R.string.settings_default_note_save_type_html)),
            isLastItem = true
          ) { viewModel.updateSaveType(it) }
        }
      }
    }
  }
}