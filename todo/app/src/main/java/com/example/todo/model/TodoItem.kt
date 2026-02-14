package com.example.todo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

enum class RepeatCycle {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY
}

@Entity(tableName = "todo_item")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val reminderTime: LocalTime,
    val repeatCycle: RepeatCycle,
    val repeatDayOfWeek: Int? = null,
    val repeatDayOfMonth: Int? = null,
    val isEnabled: Boolean = true,
    val nextReminderTimestamp: Long? = null
)
