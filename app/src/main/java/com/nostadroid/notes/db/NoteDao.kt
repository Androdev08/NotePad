package com.nostadroid.notes.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
  @Query("SELECT * FROM noteentity ORDER BY timestamp DESC")
  fun getAll(): Flow<List<NoteEntity>>

  @Query("SELECT * FROM noteentity WHERE uid IN (:noteId)")
  fun loadById(noteId: Int): NoteEntity

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(vararg notes: NoteEntity)

  @Delete
  fun delete(note: NoteEntity)
}