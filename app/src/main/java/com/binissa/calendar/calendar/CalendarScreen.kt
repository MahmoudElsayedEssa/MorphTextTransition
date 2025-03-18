package com.binissa.calendar.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.binissa.calendar.calendar.components.AnimatedTaskItem
import com.binissa.calendar.calendar.components.SwipeToDeleteContainer
import com.binissa.calendar.calendar.components.TaskDialog
import com.binissa.calendar.calendar.components.WeekCalendar
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    state: CalendarState, actions: CalendarActions
) {
    val haptic = LocalHapticFeedback.current
    var dragOffset by remember { mutableFloatStateOf(0f) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { actions.onAddTaskClick() }, containerColor = Color(0xFFFF5252)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                    tint = Color.White
                )
            }
        }) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(onDragEnd = {
                        if (dragOffset > 100) {
                            actions.onSwipeToPreviousDay()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        } else if (dragOffset < -100) {
                            actions.onSwipeToNextDay()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        dragOffset = 0f
                    }, onDragCancel = { dragOffset = 0f }, onHorizontalDrag = { _, dragAmount ->
                        dragOffset += dragAmount
                    })
                }, color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    AnimatedCalendarHeader(
                        selectedDate = state.selectedDate,
                        onPreviousDayClick = actions.onSwipeToPreviousDay,
                        onNextDayClick = actions.onSwipeToNextDay
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    WeekCalendar(
                        selectedDate = state.selectedDate, onDateSelected = actions.onDateSelected
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (state.tasks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No tasks for this day",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                } else {
                    items(state.tasks) { taskItem ->
                        SwipeToDeleteContainer(
                            onDelete = { actions.onDeleteTask(taskItem) }) {
                            AnimatedTaskItem(
                                task = taskItem,
                                onClick = { actions.onTaskClick(taskItem) },
                                onCheckboxClick = { actions.onTaskCompleteToggle(taskItem) })
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Add/Edit Task Dialog
            if (state.isAddingTask) {
                TaskDialog(
                    taskTitle = state.newTaskTitle,
                    isEditing = state.editingTask != null,
                    onDismiss = actions.onDismissAddTask,
                    onTitleChange = actions.onNewTaskTitleChange,
                    onSave = actions.onSaveNewTask
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview(name = "Calendar")
private fun CalendarScreenPreview() {
    MaterialTheme {
        CalendarScreen(
            state = CalendarState(
                selectedDate = LocalDate.now(), tasks = listOf(
                    TaskItem(id = "1", title = "Daria's 20th Birthday", type = TaskType.BIRTHDAY),
                    TaskItem(id = "2", title = "Wake up", time = "09:00", type = TaskType.STAR),
                    TaskItem(
                        id = "3", title = "Design Crit", time = "10:00", type = TaskType.CHECKBOX
                    )
                )
            ), actions = CalendarActions()
        )
    }
}