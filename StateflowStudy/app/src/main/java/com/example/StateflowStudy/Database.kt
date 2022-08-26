package com.example.StateflowStudy

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DBData::class], version = 1)
abstract class Database : RoomDatabase(){
    abstract fun dataDao() : DBDao
}