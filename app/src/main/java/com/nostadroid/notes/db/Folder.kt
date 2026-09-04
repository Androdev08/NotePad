package com.nostadroid.notes.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Folder(
  @PrimaryKey(autoGenerate = true) val uid: Int,
  val name: String,
  val notesCount: Int
)
