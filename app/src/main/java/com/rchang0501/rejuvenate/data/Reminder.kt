package com.rchang0501.rejuvenate.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//represents a database entity in the app --> basically each unique row
@Entity(tableName = "reminder")
data class Reminder (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "dueDate")
    val dueDate: String,
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    @ColumnInfo(name = "isComplete")
    var isComplete: Boolean = false
)