package com.example.todo.repository

import com.example.todo.data.TodoItemDao
import com.example.todo.model.TodoItem
import kotlinx.coroutines.flow.Flow

class TodoRepository(private val todoItemDao: TodoItemDao) {
    
    fun getAllTodoItems(): Flow<List<TodoItem>> = todoItemDao.getAllTodoItems()
    
    fun getEnabledTodoItems(): Flow<List<TodoItem>> = todoItemDao.getEnabledTodoItems()
    
    suspend fun getTodoItemById(id: Long): TodoItem? = todoItemDao.getTodoItemById(id)
    
    suspend fun insertTodoItem(todoItem: TodoItem): Long = todoItemDao.insertTodoItem(todoItem)
    
    suspend fun updateTodoItem(todoItem: TodoItem) = todoItemDao.updateTodoItem(todoItem)
    
    suspend fun deleteTodoItem(todoItem: TodoItem) = todoItemDao.deleteTodoItem(todoItem)
    
    suspend fun deleteTodoItemById(id: Long) = todoItemDao.deleteTodoItemById(id)
    
    suspend fun updateTodoItemEnabled(id: Long, isEnabled: Boolean) = 
        todoItemDao.updateTodoItemEnabled(id, isEnabled)
}
