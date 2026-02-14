package com.example.todo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.TodoDatabase
import com.example.todo.model.RepeatCycle
import com.example.todo.model.TodoItem
import com.example.todo.repository.TodoRepository
import com.example.todo.scheduler.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime

data class TodoUiState(
    val todoItems: List<TodoItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: TodoRepository
    private val scheduler: ReminderScheduler
    
    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()
    
    init {
        val database = TodoDatabase.getDatabase(application)
        repository = TodoRepository(database.todoItemDao())
        scheduler = ReminderScheduler(application)
        
        loadTodoItems()
    }
    
    private fun loadTodoItems() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                repository.getAllTodoItems().collect { items ->
                    _uiState.value = _uiState.value.copy(
                        todoItems = items,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun addTodoItem(
        title: String,
        description: String?,
        reminderTime: LocalTime,
        repeatCycle: RepeatCycle,
        repeatDayOfWeek: Int?,
        repeatDayOfMonth: Int?
    ) {
        viewModelScope.launch {
            try {
                val todoItem = TodoItem(
                    title = title,
                    description = description,
                    reminderTime = reminderTime,
                    repeatCycle = repeatCycle,
                    repeatDayOfWeek = repeatDayOfWeek,
                    repeatDayOfMonth = repeatDayOfMonth,
                    isEnabled = true
                )
                val id = repository.insertTodoItem(todoItem)
                val insertedItem = todoItem.copy(id = id)
                scheduler.scheduleReminder(insertedItem)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun updateTodoItem(todoItem: TodoItem) {
        viewModelScope.launch {
            try {
                repository.updateTodoItem(todoItem)
                scheduler.scheduleReminder(todoItem)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun deleteTodoItem(todoItem: TodoItem) {
        viewModelScope.launch {
            try {
                repository.deleteTodoItem(todoItem)
                scheduler.cancelReminder(todoItem.id)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun toggleTodoItemEnabled(todoItem: TodoItem) {
        viewModelScope.launch {
            try {
                val updatedItem = todoItem.copy(isEnabled = !todoItem.isEnabled)
                repository.updateTodoItemEnabled(todoItem.id, updatedItem.isEnabled)
                scheduler.scheduleReminder(updatedItem)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
