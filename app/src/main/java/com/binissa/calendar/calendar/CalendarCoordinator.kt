package com.binissa.calendar.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDate

/**
 * Screen's coordinator which is responsible for handling actions from the UI layer
 * and one-shot actions based on the new UI state
 */
class CalendarCoordinator(
    val viewModel: CalendarViewModel
) {
    val screenStateFlow = viewModel.stateFlow

    fun onDateSelected(date: LocalDate) {
        viewModel.selectDate(date)
    }

    fun onTaskCompleteToggle(task: TaskItem) {
        viewModel.toggleTaskCompleted(task)
    }

    fun onAddTaskClick() {
        viewModel.showAddTask()
    }

    fun onDismissAddTask() {
        viewModel.dismissAddTask()
    }

    fun onNewTaskTitleChange(title: String) {
        viewModel.updateNewTaskTitle(title)
    }

    fun onSaveNewTask(type: TaskType) {
        viewModel.saveNewTask(type)
    }

    fun onTaskClick(task: TaskItem) {
        viewModel.editTask(task)
    }

    fun onDeleteTask(task: TaskItem) {
        viewModel.deleteTask(task)
    }

    fun onSwipeToNextDay() {
        viewModel.navigateToNextDay()
    }

    fun onSwipeToPreviousDay() {
        viewModel.navigateToPreviousDay()
    }
}

@Composable
fun rememberCalendarCoordinator(
    viewModel: CalendarViewModel = hiltViewModel()
): CalendarCoordinator {
    return remember(viewModel) {
        CalendarCoordinator(
            viewModel = viewModel
        )
    }
}