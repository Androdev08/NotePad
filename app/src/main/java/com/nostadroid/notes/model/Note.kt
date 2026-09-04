package com.nostadroid.notes.model

data class Note(
  val title: String,
  val autoTitle: Boolean,
  val content: String,
  val timestamp: Long,
  val folderName: String,
  val folderId: Int,
  val displayTitle: String,
  val displayContent: String,
  val saveType: String
)