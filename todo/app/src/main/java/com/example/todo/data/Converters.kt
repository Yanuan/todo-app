package com.example.todo.data

import androidx.room.TypeConverter
import com.example.todo.model.RepeatCycle
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun fromLocalTime(time: LocalTime): String {
        return time.toString()
    }

    @TypeConverter
    fun toLocalTime(timeString: String): LocalTime {
        return LocalTime.parse(timeString)
    }

    @TypeConverter
    fun fromRepeatCycle(cycle: RepeatCycle): String {
        return cycle.name
    }

    @TypeConverter
    fun toRepeatCycle(cycleString: String): RepeatCycle {
        return RepeatCycle.valueOf(cycleString)
    }
}
