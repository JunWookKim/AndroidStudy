package com.example.StateflowStudy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DBData(
    @PrimaryKey(autoGenerate = true) val id : Int,
    @ColumnInfo val text : String
)