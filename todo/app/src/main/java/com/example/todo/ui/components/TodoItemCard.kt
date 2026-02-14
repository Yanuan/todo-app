package com.example.todo.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todo.model.RepeatCycle
import com.example.todo.model.TodoItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoItemCard(
    todoItem: TodoItem,
    onEdit: (TodoItem) -> Unit,
    onDelete: (TodoItem) -> Unit,
    onToggle: (TodoItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = !todoItem.isEnabled,
                        onCheckedChange = { onToggle(todoItem) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = todoItem.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        todoItem.description?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                Row {
                    IconButton(onClick = { onEdit(todoItem) }) {
                        Icon(Icons.Default.Edit, contentDescription = "编辑")
                    }
                    IconButton(onClick = { onDelete(todoItem) }) {
                        Icon(Icons.Default.Delete, contentDescription = "删除")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = todoItem.reminderTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = getRepeatCycleText(todoItem),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            todoItem.nextReminderTimestamp?.let { timestamp ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "下次提醒: ${formatNextReminder(timestamp)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getRepeatCycleText(todoItem: TodoItem): String {
    return when (todoItem.repeatCycle) {
        RepeatCycle.NONE -> "仅一次"
        RepeatCycle.DAILY -> "每天"
        RepeatCycle.WEEKLY -> {
            val dayOfWeek = todoItem.repeatDayOfWeek ?: 1
            "每周${getDayOfWeekText(dayOfWeek)}"
        }
        RepeatCycle.MONTHLY -> {
            val dayOfMonth = todoItem.repeatDayOfMonth ?: 1
            "每月${dayOfMonth}日"
        }
    }
}

private fun getDayOfWeekText(dayOfWeek: Int): String {
    return when (dayOfWeek) {
        1 -> "一"
        2 -> "二"
        3 -> "三"
        4 -> "四"
        5 -> "五"
        6 -> "六"
        7 -> "日"
        else -> "一"
    }
}

private fun formatNextReminder(timestamp: Long): String {
    val dateTime = LocalDateTime.ofEpochSecond(timestamp / 1000, 0, java.time.ZoneOffset.UTC)
    val now = LocalDateTime.now()
    
    val formatter = when {
        dateTime.toLocalDate() == now.toLocalDate() -> "今天 ${dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
        dateTime.toLocalDate() == now.plusDays(1).toLocalDate() -> "明天 ${dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
        else -> dateTime.format(DateTimeFormatter.ofPattern("MM月dd日 HH:mm"))
    }
    
    return formatter
}
