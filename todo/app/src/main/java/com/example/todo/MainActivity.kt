package com.example.todo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.todo.model.TodoItem
import com.example.todo.ui.components.TodoEditDialog
import com.example.todo.ui.components.TodoItemCard
import com.example.todo.ui.theme.TodoTheme
import com.example.todo.viewmodel.TodoViewModel

class MainActivity : ComponentActivity() {
    
    private val viewModel: TodoViewModel by viewModels()
    
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        setContent {
            TodoTheme {
                TodoScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(viewModel: TodoViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var editingTodoItem by remember { mutableStateOf<TodoItem?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deletingTodoItem by remember { mutableStateOf<TodoItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("待办事项提醒") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingTodoItem = null
                    showEditDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加待办事项")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.todoItems.isEmpty()) {
                Text(
                    text = "暂无待办事项\n点击 + 添加新的待办事项",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(uiState.todoItems) { todoItem ->
                        TodoItemCard(
                            todoItem = todoItem,
                            onEdit = {
                                editingTodoItem = todoItem
                                showEditDialog = true
                            },
                            onDelete = {
                                deletingTodoItem = todoItem
                                showDeleteDialog = true
                            },
                            onToggle = {
                                viewModel.toggleTodoItemEnabled(it)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        TodoEditDialog(
            todoItem = editingTodoItem,
            onDismiss = {
                showEditDialog = false
                editingTodoItem = null
            },
            onSave = { title, description, time, cycle, dayOfWeek, dayOfMonth ->
                if (editingTodoItem != null) {
                    viewModel.updateTodoItem(
                        editingTodoItem!!.copy(
                            title = title,
                            description = description,
                            reminderTime = time,
                            repeatCycle = cycle,
                            repeatDayOfWeek = dayOfWeek,
                            repeatDayOfMonth = dayOfMonth
                        )
                    )
                } else {
                    viewModel.addTodoItem(title, description, time, cycle, dayOfWeek, dayOfMonth)
                }
                showEditDialog = false
                editingTodoItem = null
            }
        )
    }

    if (showDeleteDialog && deletingTodoItem != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                deletingTodoItem = null
            },
            title = { Text("删除待办事项") },
            text = { Text("确定要删除\"${deletingTodoItem!!.title}\"吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTodoItem(deletingTodoItem!!)
                        showDeleteDialog = false
                        deletingTodoItem = null
                    }
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        deletingTodoItem = null
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }

    uiState.error?.let { error ->
        LaunchedEffect(error) {
            viewModel.clearError()
        }
    }
}
