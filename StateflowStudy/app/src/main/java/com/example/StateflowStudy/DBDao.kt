package com.example.StateflowStudy

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DBDao {
    @Query("SELECT * FROM DBData")
    fun getAll(): List<Data>

    @Query("SELECT * FROM DBData")
    fun getAllFlow(): Flow<List<Data>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dbData: DBData)
}