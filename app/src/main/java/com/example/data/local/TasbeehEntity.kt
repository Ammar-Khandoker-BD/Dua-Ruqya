package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasbeeh_records")
data class TasbeehEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dhikrTitleBn: String,
    val dhikrTitleEn: String,
    val arabicText: String,
    val currentCount: Int = 0,
    val targetGoal: Int = 33,
    val totalRounds: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
