package com.example.todo.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.todo.data.TodoDatabase
import com.example.todo.model.RepeatCycle
import com.example.todo.model.TodoItem
import com.example.todo.receiver.ReminderReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class ReminderScheduler(private val context: Context) {
    
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val database = TodoDatabase.getDatabase(context)
    
    suspend fun scheduleReminder(todoItem: TodoItem) = withContext(Dispatchers.IO) {
        if (!todoItem.isEnabled) {
            cancelReminder(todoItem.id)
            return@withContext
        }

        val nextReminderTime = calculateNextReminderTime(todoItem)
        val timestamp = nextReminderTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        database.todoItemDao().updateTodoItem(
            todoItem.copy(nextReminderTimestamp = timestamp)
        )

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_TODO_ID, todoItem.id)
            putExtra(ReminderReceiver.EXTRA_TODO_TITLE, todoItem.title)
            putExtra(ReminderReceiver.EXTRA_TODO_DESCRIPTION, todoItem.description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todoItem.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timestamp,
                pendingIntent
            )
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timestamp,
                pendingIntent
            )
        }
    }

    suspend fun cancelReminder(todoId: Long) = withContext(Dispatchers.IO) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todoId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
    }

    suspend fun rescheduleAllReminders() = withContext(Dispatchers.IO) {
        val todoItems = database.todoItemDao().getEnabledTodoItems()
        todoItems.collect { items ->
            items.forEach { item ->
                scheduleReminder(item)
            }
        }
    }

    private fun calculateNextReminderTime(todoItem: TodoItem): LocalDateTime {
        val now = LocalDateTime.now()
        val reminderTime = todoItem.reminderTime
        
        return when (todoItem.repeatCycle) {
            RepeatCycle.NONE -> {
                val today = LocalDate.now()
                val candidateTime = LocalDateTime.of(today, reminderTime)
                if (candidateTime.isAfter(now)) {
                    candidateTime
                } else {
                    candidateTime.plusDays(1)
                }
            }
            RepeatCycle.DAILY -> {
                val today = LocalDate.now()
                val candidateTime = LocalDateTime.of(today, reminderTime)
                if (candidateTime.isAfter(now)) {
                    candidateTime
                } else {
                    candidateTime.plusDays(1)
                }
            }
            RepeatCycle.WEEKLY -> {
                val dayOfWeek = todoItem.repeatDayOfWeek ?: 1
                val currentDayOfWeek = now.dayOfWeek.value
                val today = LocalDate.now()
                val candidateTime = LocalDateTime.of(today, reminderTime)
                
                if (candidateTime.isAfter(now) && currentDayOfWeek == dayOfWeek) {
                    candidateTime
                } else {
                    val daysToAdd = if (dayOfWeek > currentDayOfWeek) {
                        dayOfWeek - currentDayOfWeek
                    } else {
                        7 - currentDayOfWeek + dayOfWeek
                    }
                    candidateTime.plusDays(daysToAdd.toLong())
                }
            }
            RepeatCycle.MONTHLY -> {
                val dayOfMonth = todoItem.repeatDayOfMonth ?: 1
                val today = LocalDate.now()
                val candidateTime = LocalDateTime.of(
                    today.withDayOfMonth(dayOfMonth.coerceIn(1, today.lengthOfMonth())),
                    reminderTime
                )
                
                if (candidateTime.isAfter(now)) {
                    candidateTime
                } else {
                    candidateTime.plusMonths(1)
                }
            }
        }
    }
}
