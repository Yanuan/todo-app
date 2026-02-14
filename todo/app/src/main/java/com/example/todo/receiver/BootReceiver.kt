package com.example.todo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todo.scheduler.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            
            val scheduler = ReminderScheduler(context)
            
            CoroutineScope(Dispatchers.IO).launch {
                scheduler.rescheduleAllReminders()
            }
        }
    }
}
