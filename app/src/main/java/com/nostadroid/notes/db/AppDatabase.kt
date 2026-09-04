package com.nostadroid.notes.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NoteEntity::class, Folder::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun noteDao(): NoteDao
  abstract fun folderDao(): FolderDao
}