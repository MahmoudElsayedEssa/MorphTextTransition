package com.binissa.calendar.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember


@Composable
fun CalendarRoute(
    coordinator: CalendarCoordinator = rememberCalendarCoordinator()
) {
    // State observing and declarations
    val uiState by coordinator.screenStateFlow.collectAsState(CalendarState())

    // UI Actions
    val actions = rememberCalendarActions(coordinator)

    // UI Rendering
    CalendarScreen(uiState, actions)
}


@Composable
fun rememberCalendarActions(coordinator: CalendarCoordinator): CalendarActions {
    return remember(coordinator) {
        CalendarActions(
            onDateSelected = coordinator::onDateSelected,
            onTaskCompleteToggle = coordinator::onTaskCompleteToggle,
            onAddTaskClick = coordinator::onAddTaskClick,
            onDismissAddTask = coordinator::onDismissAddTask,
            onNewTaskTitleChange = coordinator::onNewTaskTitleChange,
            onSaveNewTask = coordinator::onSaveNewTask,
            onTaskClick = coordinator::onTaskClick,
            onDeleteTask = coordinator::onDeleteTask,
            onSwipeToNextDay = coordinator::onSwipeToNextDay,
            onSwipeToPreviousDay = coordinator::onSwipeToPreviousDay
        )
    }
}