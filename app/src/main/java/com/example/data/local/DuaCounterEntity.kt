package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dua_counters")
data class DuaCounterEntity(
    @PrimaryKey
    val duaId: String,
    val currentCount: Int = 0,
    val targetCount: Int = 1,
    val completedTimes: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
