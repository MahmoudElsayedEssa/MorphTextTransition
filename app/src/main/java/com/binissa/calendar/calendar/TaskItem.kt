package com.binissa.calendar.calendar

import java.time.LocalDate

data class TaskItem(
    val id: String,
    val title: String,
    val time: String? = null,
    val type: TaskType = TaskType.CHECKBOX,
    val isCompleted: Boolean = false,
    val isHighlighted: Boolean = false,
    val date: LocalDate = LocalDate.now()
)

enum class TaskType {
    BIRTHDAY, STAR, CHECKBOX, MOON
}