package com.binissa.calendar.calendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel
class CalendarViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _stateFlow: MutableStateFlow<CalendarState> = MutableStateFlow(CalendarState(
        selectedDate = LocalDate.now(),
        tasks = getSampleTaskItems()
    ))

    val stateFlow: StateFlow<CalendarState> = _stateFlow.asStateFlow()

    fun selectDate(date: LocalDate) {
        _stateFlow.update { currentState ->
            currentState.copy(
                selectedDate = date,
                // In a real app, you would fetch tasks for this date from a repository
                tasks = getSampleTaskItems().filter {
                    LocalDate.now().dayOfMonth == date.dayOfMonth
                }
            )
        }
    }

    fun toggleTaskCompleted(task: TaskItem) {
        _stateFlow.update { currentState ->
            val updatedTasks = currentState.tasks.map {
                if (it.id == task.id) it.copy(isCompleted = !it.isCompleted) else it
            }
            currentState.copy(tasks = updatedTasks)
        }
    }

    fun showAddTask() {
        _stateFlow.update { it.copy(isAddingTask = true, newTaskTitle = "", editingTask = null) }
    }

    fun dismissAddTask() {
        _stateFlow.update { it.copy(isAddingTask = false, newTaskTitle = "", editingTask = null) }
    }

    fun updateNewTaskTitle(title: String) {
        _stateFlow.update { it.copy(newTaskTitle = title) }
    }

    fun saveNewTask(type: TaskType) {
        val currentState = _stateFlow.value

        if (currentState.newTaskTitle.isBlank()) return

        val newTask = TaskItem(
            id = UUID.randomUUID().toString(),
            title = currentState.newTaskTitle,
            type = type,
            date = currentState.selectedDate
        )

        _stateFlow.update { state ->
            state.copy(
                tasks = state.tasks + newTask,
                isAddingTask = false,
                newTaskTitle = ""
            )
        }
    }

    fun editTask(task: TaskItem) {
        _stateFlow.update { it.copy(editingTask = task, isAddingTask = true, newTaskTitle = task.title) }
    }

    fun deleteTask(task: TaskItem) {
        _stateFlow.update { currentState ->
            currentState.copy(tasks = currentState.tasks.filter { it.id != task.id })
        }
    }

    fun navigateToNextDay() {
        _stateFlow.update { currentState ->
            val nextDay = currentState.selectedDate.plusDays(1)
            currentState.copy(selectedDate = nextDay)
        }
    }

    fun navigateToPreviousDay() {
        _stateFlow.update { currentState ->
            val previousDay = currentState.selectedDate.minusDays(1)
            currentState.copy(selectedDate = previousDay)
        }
    }

    private fun getSampleTaskItems(): List<TaskItem> {
        return listOf(
            TaskItem(id = "1", title = "Daria's 20th Birthday", type = TaskType.BIRTHDAY, date = LocalDate.now()),
            TaskItem(id = "2", title = "Wake up", time = "09:00", type = TaskType.STAR, date = LocalDate.now()),
            TaskItem(id = "3", title = "Design Crit", time = "10:00", type = TaskType.CHECKBOX, date = LocalDate.now()),
            TaskItem(id = "4", title = "Haircut with Vincent", time = "13:00", type = TaskType.CHECKBOX, date = LocalDate.now()),
            TaskItem(id = "5", title = "Make pasta", type = TaskType.CHECKBOX, date = LocalDate.now()),
            TaskItem(id = "6", title = "Pushups x100", type = TaskType.CHECKBOX, isCompleted = true, date = LocalDate.now()),
            TaskItem(id = "7", title = "Wind down", time = "21:00", type = TaskType.MOON, date = LocalDate.now())
        )
    }
}