package com.nostadroid.notes.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nostadroid.notes.datastore.SettingsManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsHomeViewModel(private val settingsManager: SettingsManager) : ViewModel() {
  val saveTypeState: StateFlow<String> = settingsManager.saveTypeFlow
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = "Markdown"
    )

  fun updateSaveType(saveType: String) {
    viewModelScope.launch {
      settingsManager.storeSaveType(saveType)
    }
  }
}