package com.example.todo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.todo.model.RepeatCycle
import com.example.todo.model.TodoItem
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoEditDialog(
    todoItem: TodoItem? = null,
    onDismiss: () -> Unit,
    onSave: (String, String?, LocalTime, RepeatCycle, Int?, Int?) -> Unit
) {
    var title by remember { mutableStateOf(todoItem?.title ?: "") }
    var description by remember { mutableStateOf(todoItem?.description ?: "") }
    var selectedTime by remember { mutableStateOf(todoItem?.reminderTime ?: LocalTime.now()) }
    var selectedCycle by remember { mutableStateOf(todoItem?.repeatCycle ?: RepeatCycle.NONE) }
    var selectedDayOfWeek by remember { mutableStateOf(todoItem?.repeatDayOfWeek ?: 1) }
    var selectedDayOfMonth by remember { mutableStateOf(todoItem?.repeatDayOfMonth ?: 1) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showCycleDropdown by remember { mutableStateOf(false) }
    var showDayOfWeekPicker by remember { mutableStateOf(false) }
    var showDayOfMonthPicker by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (todoItem == null) "添加待办事项" else "编辑待办事项",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("标题") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("描述（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("提醒时间: ${selectedTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))}")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box {
                    OutlinedButton(
                        onClick = { showCycleDropdown = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("重复周期: ${getRepeatCycleText(selectedCycle)}")
                    }
                    
                    DropdownMenu(
                        expanded = showCycleDropdown,
                        onDismissRequest = { showCycleDropdown = false }
                    ) {
                        RepeatCycle.values().forEach { cycle ->
                            DropdownMenuItem(
                                text = { Text(getRepeatCycleText(cycle)) },
                                onClick = {
                                    selectedCycle = cycle
                                    showCycleDropdown = false
                                }
                            )
                        }
                    }
                }

                if (selectedCycle == RepeatCycle.WEEKLY) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { showDayOfWeekPicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("重复星期: ${getDayOfWeekText(selectedDayOfWeek)}")
                    }
                }

                if (selectedCycle == RepeatCycle.MONTHLY) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { showDayOfMonthPicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("重复日期: 每月${selectedDayOfMonth}日")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val dayOfWeek = if (selectedCycle == RepeatCycle.WEEKLY) selectedDayOfWeek else null
                                val dayOfMonth = if (selectedCycle == RepeatCycle.MONTHLY) selectedDayOfMonth else null
                                onSave(title, description.ifBlank { null }, selectedTime, selectedCycle, dayOfWeek, dayOfMonth)
                            }
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Text("保存")
                    }
                }
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            initialTime = selectedTime,
            onTimeSelected = {
                selectedTime = it
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }

    if (showDayOfWeekPicker) {
        DayOfWeekPickerDialog(
            selectedDay = selectedDayOfWeek,
            onDaySelected = {
                selectedDayOfWeek = it
                showDayOfWeekPicker = false
            },
            onDismiss = { showDayOfWeekPicker = false }
        )
    }

    if (showDayOfMonthPicker) {
        DayOfMonthPickerDialog(
            selectedDay = selectedDayOfMonth,
            onDaySelected = {
                selectedDayOfMonth = it
                showDayOfMonthPicker = false
            },
            onDismiss = { showDayOfMonthPicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(LocalTime.of(timePickerState.hour, timePickerState.minute))
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
        title = { Text("选择时间") },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}

private fun getRepeatCycleText(cycle: RepeatCycle): String {
    return when (cycle) {
        RepeatCycle.NONE -> "仅一次"
        RepeatCycle.DAILY -> "每天"
        RepeatCycle.WEEKLY -> "每周"
        RepeatCycle.MONTHLY -> "每月"
    }
}

private fun getDayOfWeekText(dayOfWeek: Int): String {
    return when (dayOfWeek) {
        1 -> "星期一"
        2 -> "星期二"
        3 -> "星期三"
        4 -> "星期四"
        5 -> "星期五"
        6 -> "星期六"
        7 -> "星期日"
        else -> "星期一"
    }
}

@Composable
fun DayOfWeekPickerDialog(
    selectedDay: Int,
    onDaySelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择星期") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..7).forEach { day ->
                    FilterChip(
                        selected = selectedDay == day,
                        onClick = { onDaySelected(day) },
                        label = { Text(getDayOfWeekText(day)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("确定")
            }
        }
    )
}

@Composable
fun DayOfMonthPickerDialog(
    selectedDay: Int,
    onDaySelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val days = (1..31).toList()
    var selectedDayOfMonth by remember { mutableStateOf(selectedDay) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择日期") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                days.chunked(7).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { day ->
                            FilterChip(
                                selected = selectedDayOfMonth == day,
                                onClick = { selectedDayOfMonth = day },
                                label = { Text(day.toString()) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDaySelected(selectedDayOfMonth)
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
