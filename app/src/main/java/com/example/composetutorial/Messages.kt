package com.example.composetutorial

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Messages(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val author: String,
    val body: String
)
