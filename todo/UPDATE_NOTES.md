# 功能更新说明 - 添加每周和每月的具体日期选择

## 🎯 更新内容

本次更新解决了"每月和每周的日期不能选择具体一天"的问题，现在用户可以：

- ✅ **每周重复**：选择具体的星期几（星期一到星期日）
- ✅ **每月重复**：选择具体的日期（1日到31日）

## 📝 详细修改

### 1. 数据模型更新

#### TodoItem.kt
添加了两个新字段：
```kotlin
data class TodoItem(
    // ... 其他字段
    val repeatDayOfWeek: Int? = null,      // 每周重复：1-7（星期一到星期日）
    val repeatDayOfMonth: Int? = null,     // 每月重复：1-31
    // ... 其他字段
)
```

**字段说明**：
- `repeatDayOfWeek`: 1=星期一, 2=星期二, ..., 7=星期日
- `repeatDayOfMonth`: 1-31，表示每月的几号

### 2. 数据库迁移

#### TodoDatabase.kt
- 数据库版本从 1 升级到 2
- 添加了 `MIGRATION_1_2` 迁移策略
- 自动添加新字段，保留现有数据

### 3. 提醒调度器更新

#### ReminderScheduler.kt
改进了 `calculateNextReminderTime()` 方法：

**每周重复**：
```kotlin
RepeatCycle.WEEKLY -> {
    val dayOfWeek = todoItem.repeatDayOfWeek ?: 1
    val currentDayOfWeek = now.dayOfWeek.value
    
    if (candidateTime.isAfter(now) && currentDayOfWeek == dayOfWeek) {
        candidateTime
    } else {
        // 计算到下一个指定星期的天数
        val daysToAdd = if (dayOfWeek > currentDayOfWeek) {
            dayOfWeek - currentDayOfWeek
        } else {
            7 - currentDayOfWeek + dayOfWeek
        }
        candidateTime.plusDays(daysToAdd.toLong())
    }
}
```

**每月重复**：
```kotlin
RepeatCycle.MONTHLY -> {
    val dayOfMonth = todoItem.repeatDayOfMonth ?: 1
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
```

### 4. UI 组件更新

#### TodoEditDialog.kt
添加了两个新的选择器：

**星期选择器**：
```kotlin
@Composable
fun DayOfWeekPickerDialog(
    selectedDay: Int,
    onDaySelected: (Int) -> Unit,
    onDismiss: () -> Unit
)
```
- 显示星期一到星期日的选项
- 使用 FilterChip 组件
- 单选模式

**日期选择器**：
```kotlin
@Composable
fun DayOfMonthPickerDialog(
    selectedDay: Int,
    onDaySelected: (Int) -> Unit,
    onDismiss: () -> Unit
)
```
- 显示1-31日的选项
- 网格布局（每行7个）
- 单选模式

**动态显示**：
- 选择"每周"时，显示星期选择器
- 选择"每月"时，显示日期选择器
- 其他周期时，隐藏这两个选择器

#### TodoItemCard.kt
更新了重复周期的显示文本：
```kotlin
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
```

显示示例：
- 每周一
- 每月15日

### 5. ViewModel 更新

#### TodoViewModel.kt
更新了 `addTodoItem()` 方法签名：
```kotlin
fun addTodoItem(
    title: String,
    description: String?,
    reminderTime: LocalTime,
    repeatCycle: RepeatCycle,
    repeatDayOfWeek: Int?,      // 新增
    repeatDayOfMonth: Int?      // 新增
)
```

### 6. MainActivity 更新

更新了 `TodoEditDialog` 的调用：
```kotlin
onSave = { title, description, time, cycle, dayOfWeek, dayOfMonth ->
    if (editingTodoItem != null) {
        viewModel.updateTodoItem(
            editingTodoItem!!.copy(
                title = title,
                description = description,
                reminderTime = time,
                repeatCycle = cycle,
                repeatDayOfWeek = dayOfWeek,      // 新增
                repeatDayOfMonth = dayOfMonth      // 新增
            )
        )
    } else {
        viewModel.addTodoItem(title, description, time, cycle, dayOfWeek, dayOfMonth)
    }
}
```

### 7. Web 演示更新

#### todo-demo.html
添加了完整的日期选择功能：

**星期选择器**：
```html
<div class="day-selector" id="dayOfWeekSelector">
    <div class="day-chip" data-day="1" onclick="selectDayOfWeek(1)">一</div>
    <div class="day-chip" data-day="2" onclick="selectDayOfWeek(2)">二</div>
    <!-- ... -->
</div>
```

**日期选择器**：
```javascript
function initDayOfMonthSelector() {
    const selector = document.getElementById('dayOfMonthSelector');
    selector.innerHTML = '';
    for (let i = 1; i <= 31; i++) {
        const chip = document.createElement('div');
        chip.className = 'day-chip';
        chip.dataset.day = i;
        chip.textContent = i;
        chip.onclick = () => selectDayOfMonth(i);
        selector.appendChild(chip);
    }
}
```

## 🎨 用户界面

### 添加/编辑待办事项界面

**重复周期选择**：
- 仅一次
- 每天
- 每周 → 显示星期选择器
- 每月 → 显示日期选择器

**星期选择器**：
```
┌─────────────────────────────┐
│ 重复星期                   │
├─────────────────────────────┤
│ [一] [二] [三] [四] [五] │
│ [六] [日]                 │
└─────────────────────────────┘
```

**日期选择器**：
```
┌─────────────────────────────┐
│ 重复日期                   │
├─────────────────────────────┤
│ [1] [2] [3] [4] [5] [6] [7] │
│ [8] [9] [10][11][12][13][14]│
│ [15][16][17][18][19][20][21]│
│ [22][23][24][25][26][27][28]│
│ [29][30][31]               │
└─────────────────────────────┘
```

### 待办事项列表显示

显示示例：
- 🕐 09:00  每周一
- 🕐 14:30  每月15日
- 🕐 17:00  每天

## 🔧 技术细节

### 数据库迁移

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE todo_item ADD COLUMN repeatDayOfWeek INTEGER"
        )
        database.execSQL(
            "ALTER TABLE todo_item ADD COLUMN repeatDayOfMonth INTEGER"
        )
    }
}
```

### 星期映射

```kotlin
1 -> 星期一 (Monday)
2 -> 星期二 (Tuesday)
3 -> 星期三 (Wednesday)
4 -> 星期四 (Thursday)
5 -> 星期五 (Friday)
6 -> 星期六 (Saturday)
7 -> 星期日 (Sunday)
```

### 日期处理

- 每月重复时，自动处理月末日期（如31日在30天的月份会自动调整）
- 每周重复时，智能计算下一个指定星期
- 支持跨月、跨年的提醒计算

## 📊 使用示例

### 示例 1：每周一提醒
```
标题: 团队会议
时间: 09:00
重复周期: 每周
重复星期: 星期一
```
结果：每个星期一的 09:00 提醒

### 示例 2：每月15日提醒
```
标题: 提交月度报告
时间: 17:00
重复周期: 每月
重复日期: 15日
```
结果：每月15日的 17:00 提醒

### 示例 3：每周五提醒
```
标题: 周总结
时间: 18:00
重复周期: 每周
重复星期: 星期五
```
结果：每个星期五的 18:00 提醒

## ✅ 兼容性

- ✅ 向后兼容：现有数据自动迁移
- ✅ 新字段可选：旧数据不会受影响
- ✅ UI 适配：根据重复周期动态显示
- ✅ 数据库安全：使用 Room 迁移机制

## 🚀 后续优化建议

1. **多选支持**：允许选择多个星期或日期
2. **自定义周期**：支持自定义重复间隔（如每3天）
3. **工作日/周末**：快速选择工作日或周末
4. **农历支持**：支持农历日期选择
5. **智能建议**：根据使用习惯推荐重复周期

## 📝 总结

本次更新完善了待办事项提醒的重复功能，现在用户可以：

1. **精确控制**：每周和每月都可以选择具体日期
2. **灵活设置**：支持各种重复周期组合
3. **智能提醒**：自动计算下次提醒时间
4. **友好界面**：直观的选择器设计
5. **数据安全**：平滑的数据库迁移

所有功能都已实现并测试通过，包括 Android 原生应用和 Web 演示版本。
