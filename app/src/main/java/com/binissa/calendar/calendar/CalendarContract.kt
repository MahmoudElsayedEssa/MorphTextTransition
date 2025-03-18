package com.binissa.calendar.calendar

import java.time.LocalDate

/**
 * UI State that represents CalendarScreen
 **/
data class CalendarState(
    val selectedDate: LocalDate = LocalDate.now(),
    val tasks: List<TaskItem> = emptyList(),
    val isAddingTask: Boolean = false,
    val newTaskTitle: String = "",
    val editingTask: TaskItem? = null,
    val selectedDateEvents: Boolean = false
)

/**
 * Calendar Actions emitted from the UI Layer
 * passed to the coordinator to handle
 **/
data class CalendarActions(
    val onDateSelected: (LocalDate) -> Unit = {},
    val onTaskCompleteToggle: (TaskItem) -> Unit = {},
    val onAddTaskClick: () -> Unit = {},
    val onDismissAddTask: () -> Unit = {},
    val onNewTaskTitleChange: (String) -> Unit = {},
    val onSaveNewTask: (TaskType) -> Unit = {},
    val onTaskClick: (TaskItem) -> Unit = {},
    val onDeleteTask: (TaskItem) -> Unit = {},
    val onSwipeToNextDay: () -> Unit = {},
    val onSwipeToPreviousDay: () -> Unit = {}
)