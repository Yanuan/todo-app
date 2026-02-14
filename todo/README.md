# 待办事项提醒应用

一个使用 Kotlin 和 Jetpack Compose 开发的 Android 待办事项提醒应用，类似于小米手机闹钟应用。

## 功能特性

- ✅ 创建、编辑、查看和删除待办事项
- ⏰ 自定义提醒时间
- 🔄 支持多种重复周期（每天、每周、每月、仅一次）
- 🔔 精确的系统通知提醒
- 📱 设备重启后自动恢复所有闹钟
- 🎨 现代化的 Material Design 3 UI

## 项目结构

```
app/src/main/java/com/example/todo/
├── data/
│   ├── Converters.kt          # Room 类型转换器
│   ├── TodoDatabase.kt        # Room 数据库
│   └── TodoItemDao.kt         # 数据访问对象
├── model/
│   └── TodoItem.kt            # 数据模型
├── receiver/
│   ├── BootReceiver.kt        # 开机广播接收器
│   └── ReminderReceiver.kt    # 提醒广播接收器
├── repository/
│   └── TodoRepository.kt      # 仓库层
├── scheduler/
│   └── ReminderScheduler.kt  # 提醒调度器
├── ui/
│   ├── components/
│   │   ├── TodoEditDialog.kt  # 编辑对话框
│   │   └── TodoItemCard.kt    # 待办事项卡片
│   └── theme/
│       ├── Color.kt           # 颜色主题
│       ├── Theme.kt           # 主题配置
│       └── Type.kt            # 字体样式
├── viewmodel/
│   └── TodoViewModel.kt       # 视图模型
└── MainActivity.kt           # 主活动
```

## 技术栈

- **语言**: Kotlin
- **UI 框架**: Jetpack Compose
- **架构**: MVVM
- **数据库**: Room
- **依赖注入**: 手动实现
- **通知**: AlarmManager + BroadcastReceiver
- **主题**: Material Design 3

## 核心组件说明

### 1. 数据模型 (TodoItem.kt)
定义了待办事项的数据结构，包含：
- `id`: 主键
- `title`: 标题
- `description`: 描述（可选）
- `reminderTime`: 提醒时间
- `repeatCycle`: 重复周期（枚举）
- `isEnabled`: 是否启用
- `nextReminderTimestamp`: 下次提醒时间戳

### 2. 数据库层 (Room)
- **TodoDatabase**: Room 数据库实例
- **TodoItemDao**: 数据访问接口，提供 CRUD 操作
- **Converters**: 类型转换器，处理 LocalTime 和 RepeatCycle

### 3. 提醒调度器 (ReminderScheduler.kt)
使用 `AlarmManager.setExactAndAllowWhileIdle()` 实现精确提醒：
- 计算下次提醒时间
- 设置系统闹钟
- 处理重复周期逻辑
- 支持取消提醒

### 4. 广播接收器
- **ReminderReceiver**: 接收闹钟触发，显示通知
- **BootReceiver**: 监听设备启动，恢复所有闹钟

### 5. UI 组件 (Jetpack Compose)
- **TodoScreen**: 主界面，显示待办列表
- **TodoItemCard**: 待办事项卡片
- **TodoEditDialog**: 添加/编辑对话框
- **TimePickerDialog**: 时间选择器

## 权限说明

应用需要以下权限：
- `SCHEDULE_EXACT_ALARM`: 设置精确闹钟
- `RECEIVE_BOOT_COMPLETED`: 接收开机广播
- `POST_NOTIFICATIONS`: 发送通知（Android 13+）
- `VIBRATE`: 震动提醒

## 使用方法

1. 点击右下角的 `+` 按钮添加新的待办事项
2. 填写标题和描述（可选）
3. 选择提醒时间和重复周期
4. 点击保存
5. 到达设定时间后会收到通知提醒

## 构建要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 34
- Kotlin 1.9.10
- Gradle 8.1.2

## 依赖版本

- Compose BOM: 2023.10.01
- Room: 2.6.0
- Lifecycle: 2.6.2
- Material 3: 2023.10.01

## 注意事项

1. Android 12+ 需要请求 `SCHEDULE_EXACT_ALARM` 权限
2. Android 13+ 需要请求 `POST_NOTIFICATIONS` 权限
3. 设备重启后会自动恢复所有启用的闹钟
4. 使用 `setExactAndAllowWhileIdle()` 确保在 Doze 模式下也能触发

## 后续优化建议

- 添加分类功能
- 支持自定义铃声
- 添加统计功能
- 支持导入/导出数据
- 添加桌面小组件
- 支持多语言
