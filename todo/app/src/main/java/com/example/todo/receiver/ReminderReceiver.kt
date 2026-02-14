package com.example.todo.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.todo.MainActivity
import com.example.todo.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ReminderReceiver : BroadcastReceiver() {
    
    companion object {
        const val CHANNEL_ID = "todo_reminder_channel"
        const val NOTIFICATION_ID = 1001
        const val EXTRA_TODO_ID = "todo_id"
        const val EXTRA_TODO_TITLE = "todo_title"
        const val EXTRA_TODO_DESCRIPTION = "todo_description"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getLongExtra(EXTRA_TODO_ID, -1L)
        val title = intent.getStringExtra(EXTRA_TODO_TITLE) ?: "待办事项"
        val description = intent.getStringExtra(EXTRA_TODO_DESCRIPTION)

        createNotificationChannel(context)
        showNotification(context, todoId, title, description)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "待办事项提醒",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "待办事项提醒通知"
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(
        context: Context,
        todoId: Long,
        title: String,
        description: String?
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_TODO_ID, todoId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            todoId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(description ?: "是时候完成这个待办事项了！")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(description ?: "是时候完成这个待办事项了！")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setShowWhen(true)
            .setWhen(System.currentTimeMillis())

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID + todoId.toInt(), notificationBuilder.build())
    }
}
