package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey
    val duaId: String,
    val savedAt: Long = System.currentTimeMillis(),
    val userNote: String? = null
)
