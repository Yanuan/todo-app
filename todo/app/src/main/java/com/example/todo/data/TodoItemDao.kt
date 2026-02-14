package com.example.todo.data

import androidx.room.*
import com.example.todo.model.RepeatCycle
import com.example.todo.model.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao {
    @Query("SELECT * FROM todo_item ORDER BY nextReminderTimestamp ASC")
    fun getAllTodoItems(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_item WHERE id = :id")
    suspend fun getTodoItemById(id: Long): TodoItem?

    @Query("SELECT * FROM todo_item WHERE isEnabled = 1 ORDER BY nextReminderTimestamp ASC")
    fun getEnabledTodoItems(): Flow<List<TodoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodoItem(todoItem: TodoItem): Long

    @Update
    suspend fun updateTodoItem(todoItem: TodoItem)

    @Delete
    suspend fun deleteTodoItem(todoItem: TodoItem)

    @Query("DELETE FROM todo_item WHERE id = :id")
    suspend fun deleteTodoItemById(id: Long)

    @Query("UPDATE todo_item SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun updateTodoItemEnabled(id: Long, isEnabled: Boolean)
}
