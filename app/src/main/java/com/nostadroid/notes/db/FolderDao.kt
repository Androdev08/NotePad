package com.nostadroid.notes.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
  @Query("SELECT * FROM folder ORDER BY name")
  fun getAll(): Flow<List<Folder>>

  @Query("SELECT * FROM folder WHERE uid IN (:folderId)")
  fun loadById(folderId: Int): Folder

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(vararg folders: Folder)

  @Delete
  fun delete(folder: Folder)
}