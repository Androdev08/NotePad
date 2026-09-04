package com.nostadroid.notes.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("settings")

class SettingsManager(private val context: Context) {
  companion object {
    val DEFAULT_SAVE_TYPE_KEY = stringPreferencesKey("save_type")
  }

  // Functions to store data
  suspend fun storeSaveType(saveType: String) {
    context.dataStore.edit {
      it[DEFAULT_SAVE_TYPE_KEY] = saveType
    }
  }

  // Flows to retrieve data
  val saveTypeFlow: Flow<String> = context.dataStore.data.map { it[DEFAULT_SAVE_TYPE_KEY] ?: "Markdown"}
}