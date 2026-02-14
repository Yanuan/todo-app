# 核心功能代码示例

## 1. 数据模型示例

### TodoItem 数据类
```kotlin
// 文件: model/TodoItem.kt
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val reminderTime: LocalTime,
    val repeatCycle: RepeatCycle,
    val isEnabled: Boolean = true,
    val nextReminderTimestamp: Long? = null
)

enum class RepeatCycle {
    NONE,      // 仅一次
    DAILY,     // 每天
    WEEKLY,    // 每周
    MONTHLY    // 每月
}
```

## 2. 数据库操作示例

### TodoItemDao 接口
```kotlin
// 文件: data/TodoItemDao.kt
@Dao
interface TodoItemDao {
    @Query("SELECT * FROM todo_item ORDER BY nextReminderTimestamp ASC")
    fun getAllTodoItems(): Flow<List<TodoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodoItem(todoItem: TodoItem): Long

    @Update
    suspend fun updateTodoItem(todoItem: TodoItem)

    @Delete
    suspend fun deleteTodoItem(todoItem: TodoItem)

    @Query("UPDATE todo_item SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun updateTodoItemEnabled(id: Long, isEnabled: Boolean)
}
```

### 使用示例
```kotlin
// 在 ViewModel 中使用
class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val database = TodoDatabase.getDatabase(application)
    private val dao = database.todoItemDao()

    fun addTodoItem(title: String, description: String?, 
                     time: LocalTime, cycle: RepeatCycle) {
        viewModelScope.launch {
            val todoItem = TodoItem(
                title = title,
                description = description,
                reminderTime = time,
                repeatCycle = cycle
            )
            dao.insertTodoItem(todoItem)
        }
    }
}
```

## 3. 提醒调度器示例

### ReminderScheduler 核心实现
```kotlin
// 文件: scheduler/ReminderScheduler.kt
class ReminderScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val database = TodoDatabase.getDatabase(context)

    suspend fun scheduleReminder(todoItem: TodoItem) {
        if (!todoItem.isEnabled) {
            cancelReminder(todoItem.id)
            return
        }

        val nextReminderTime = calculateNextReminderTime(todoItem)
        val timestamp = nextReminderTime.atZone(ZoneId.systemDefault())
            .toInstant().toEpochMilli()

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

    private fun calculateNextReminderTime(todoItem: TodoItem): LocalDateTime {
        val now = LocalDateTime.now()
        val reminderTime = todoItem.reminderTime
        val today = LocalDate.now()
        val candidateTime = LocalDateTime.of(today, reminderTime)

        return when (todoItem.repeatCycle) {
            RepeatCycle.NONE -> {
                if (candidateTime.isAfter(now)) candidateTime
                else candidateTime.plusDays(1)
            }
            RepeatCycle.DAILY -> {
                if (candidateTime.isAfter(now)) candidateTime
                else candidateTime.plusDays(1)
            }
            RepeatCycle.WEEKLY -> {
                if (candidateTime.isAfter(now)) candidateTime
                else candidateTime.plusWeeks(1)
            }
            RepeatCycle.MONTHLY -> {
                if (candidateTime.isAfter(now)) candidateTime
                else candidateTime.plusMonths(1)
            }
        }
    }
}
```

## 4. 广播接收器示例

### ReminderReceiver - 显示通知
```kotlin
// 文件: receiver/ReminderReceiver.kt
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TODO_TITLE) ?: "待办事项"
        val description = intent.getStringExtra(EXTRA_TODO_DESCRIPTION)

        createNotificationChannel(context)
        showNotification(context, title, description)
    }

    private fun showNotification(context: Context, title: String, description: String?) {
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(description ?: "是时候完成这个待办事项了！")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
}
```

### BootReceiver - 设备重启恢复
```kotlin
// 文件: receiver/BootReceiver.kt
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val scheduler = ReminderScheduler(context)
            CoroutineScope(Dispatchers.IO).launch {
                scheduler.rescheduleAllReminders()
            }
        }
    }
}
```

## 5. UI 组件示例

### TodoScreen - 主界面
```kotlin
// 文件: MainActivity.kt
@Composable
fun TodoScreen(viewModel: TodoViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var editingTodoItem by remember { mutableStateOf<TodoItem?>(null) }

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(uiState.todoItems) { todoItem ->
                TodoItemCard(
                    todoItem = todoItem,
                    onEdit = { 
                        editingTodoItem = todoItem
                        showEditDialog = true 
                    },
                    onDelete = { viewModel.deleteTodoItem(it) },
                    onToggle = { viewModel.toggleTodoItemEnabled(it) }
                )
            }
        }
    }

    if (showEditDialog) {
        TodoEditDialog(
            todoItem = editingTodoItem,
            onDismiss = { showEditDialog = false },
            onSave = { title, description, time, cycle ->
                if (editingTodoItem != null) {
                    viewModel.updateTodoItem(
                        editingTodoItem!!.copy(
                            title = title,
                            description = description,
                            reminderTime = time,
                            repeatCycle = cycle
                        )
                    )
                } else {
                    viewModel.addTodoItem(title, description, time, cycle)
                }
                showEditDialog = false
            }
        )
    }
}
```

### TodoItemCard - 待办事项卡片
```kotlin
// 文件: ui/components/TodoItemCard.kt
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
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = !todoItem.isEnabled,
                        onCheckedChange = { onToggle(todoItem) }
                    )
                    Column {
                        Text(
                            text = todoItem.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        todoItem.description?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                Text(
                    text = "🕐 ${todoItem.reminderTime.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = when (todoItem.repeatCycle) {
                        RepeatCycle.NONE -> "仅一次"
                        RepeatCycle.DAILY -> "每天"
                        RepeatCycle.WEEKLY -> "每周"
                        RepeatCycle.MONTHLY -> "每月"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
```

### TodoEditDialog - 添加/编辑对话框
```kotlin
// 文件: ui/components/TodoEditDialog.kt
@Composable
fun TodoEditDialog(
    todoItem: TodoItem? = null,
    onDismiss: () -> Unit,
    onSave: (String, String?, LocalTime, RepeatCycle) -> Unit
) {
    var title by remember { mutableStateOf(todoItem?.title ?: "") }
    var description by remember { mutableStateOf(todoItem?.description ?: "") }
    var selectedTime by remember { mutableStateOf(todoItem?.reminderTime ?: LocalTime.now()) }
    var selectedCycle by remember { mutableStateOf(todoItem?.repeatCycle ?: RepeatCycle.NONE) }
    var showTimePicker by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (todoItem == null) "添加待办事项" else "编辑待办事项",
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("标题") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("描述（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("提醒时间: ${selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))}")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("取消") }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(title, description.ifBlank { null }, selectedTime, selectedCycle)
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
}
```

## 6. ViewModel 示例

### TodoViewModel - 状态管理
```kotlin
// 文件: viewmodel/TodoViewModel.kt
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

    fun addTodoItem(title: String, description: String?, 
                     reminderTime: LocalTime, repeatCycle: RepeatCycle) {
        viewModelScope.launch {
            val todoItem = TodoItem(
                title = title,
                description = description,
                reminderTime = reminderTime,
                repeatCycle = repeatCycle,
                isEnabled = true
            )
            val id = repository.insertTodoItem(todoItem)
            val insertedItem = todoItem.copy(id = id)
            scheduler.scheduleReminder(insertedItem)
        }
    }

    fun updateTodoItem(todoItem: TodoItem) {
        viewModelScope.launch {
            repository.updateTodoItem(todoItem)
            scheduler.scheduleReminder(todoItem)
        }
    }

    fun deleteTodoItem(todoItem: TodoItem) {
        viewModelScope.launch {
            repository.deleteTodoItem(todoItem)
            scheduler.cancelReminder(todoItem.id)
        }
    }

    fun toggleTodoItemEnabled(todoItem: TodoItem) {
        viewModelScope.launch {
            val updatedItem = todoItem.copy(isEnabled = !todoItem.isEnabled)
            repository.updateTodoItemEnabled(todoItem.id, updatedItem.isEnabled)
            scheduler.scheduleReminder(updatedItem)
        }
    }
}
```

## 7. 使用示例

### 添加一个每天提醒的待办事项
```kotlin
viewModel.addTodoItem(
    title = "晨会",
    description = "每天早上9点的团队会议",
    reminderTime = LocalTime.of(9, 0),
    repeatCycle = RepeatCycle.DAILY
)
```

### 添加一个每周提醒的待办事项
```kotlin
viewModel.addTodoItem(
    title = "周报",
    description = "提交本周工作总结",
    reminderTime = LocalTime.of(17, 30),
    repeatCycle = RepeatCycle.WEEKLY
)
```

### 添加一个仅一次的提醒
```kotlin
viewModel.addTodoItem(
    title = "提交项目报告",
    description = "需要在今天下午3点前提交",
    reminderTime = LocalTime.of(15, 0),
    repeatCycle = RepeatCycle.NONE
)
```

### 编辑待办事项
```kotlin
val updatedItem = todoItem.copy(
    title = "新的标题",
    reminderTime = LocalTime.of(10, 0),
    repeatCycle = RepeatCycle.WEEKLY
)
viewModel.updateTodoItem(updatedItem)
```

### 删除待办事项
```kotlin
viewModel.deleteTodoItem(todoItem)
```

### 启用/禁用待办事项
```kotlin
viewModel.toggleTodoItemEnabled(todoItem)
```

## 8. 权限请求示例

### MainActivity 中请求权限
```kotlin
class MainActivity : ComponentActivity() {
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
```

## 9. AndroidManifest.xml 配置

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.VIBRATE" />

    <application>
        <activity android:name=".MainActivity" />
        
        <receiver android:name=".receiver.ReminderReceiver" />
        
        <receiver android:name=".receiver.BootReceiver">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>
    </application>
</manifest>
```

## 10. 完整工作流程示例

### 用户操作流程
1. **用户打开应用** → MainActivity 启动
2. **查看待办列表** → ViewModel 加载数据 → UI 显示列表
3. **点击 + 按钮** → 显示添加对话框
4. **填写信息并保存** → ViewModel 调用 addTodoItem
5. **数据保存到数据库** → Repository 插入数据
6. **设置闹钟** → ReminderScheduler 调度提醒
7. **到达提醒时间** → ReminderReceiver 接收广播
8. **显示通知** → 用户看到提醒通知
9. **点击通知** → 打开应用查看详情

### 设备重启流程
1. **设备启动** → 系统发送 BOOT_COMPLETED 广播
2. **BootReceiver 接收** → 启动恢复流程
3. **加载所有启用的待办事项** → 从数据库读取
4. **重新设置闹钟** → ReminderScheduler 重新调度
5. **恢复完成** → 所有提醒恢复正常

这些代码示例展示了应用的核心功能实现，包括数据管理、提醒调度、UI 交互等关键部分。
