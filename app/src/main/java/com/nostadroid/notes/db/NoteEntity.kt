package com.nostadroid.notes.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nostadroid.notes.model.Note

@Entity
data class NoteEntity(
  @PrimaryKey(autoGenerate = true) val uid: Int,
  @Embedded val note: Note,
)
