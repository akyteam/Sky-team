package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val timeInMillis: Long,
    val isCompleted: Boolean = false,
    val category: String = "Reminder", // Reminder, Schedule, Call, Alarm
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "voice_command_logs")
data class VoiceCommandLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val commandText: String,
    val responseSummary: String,
    val intentType: String,
    val success: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
