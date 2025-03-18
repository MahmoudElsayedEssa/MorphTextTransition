package com.binissa.calendar.calendar.components.morph

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.util.Locale
import java.time.format.TextStyle as JavaTextStyle

/**
 * A specialized implementation of [CharacterMorph] for transitioning between day names.
 * This component handles extracting day names from dates and animating the character transitions.
 *
 * @param currentDate The current selected date
 * @param previousDate The previous date (for determining the transition)
 * @param isAnimating Whether the animation is currently running
 * @param direction The direction of the animation (FORWARD or BACKWARD)
 * @param config Configuration options for the animation
 * @param textStyle Text style to apply to the day name
 * @param color Text color (defaults to LocalContentColor)
 * @param modifier Modifier for the composable
 * @param onAnimationComplete Callback when animation finishes
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayNameDirectionalTransition(
    currentDate: LocalDate,
    previousDate: LocalDate?,
    isAnimating: Boolean,
    direction: TransitionDirection = TransitionDirection.FORWARD,
    config: DirectionalTransitionConfig = DirectionalTransitionConfig(),
    textStyle: TextStyle = MaterialTheme.typography.displaySmall.copy(
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold
    ),
    color: Color = LocalContentColor.current,
    modifier: Modifier = Modifier,
    onAnimationComplete: () -> Unit = {}
) {
    // Get day names
    val currentDayName =
        currentDate.dayOfWeek.getDisplayName(JavaTextStyle.SHORT, Locale.getDefault())
    val previousDayName = previousDate?.dayOfWeek?.getDisplayName(
        JavaTextStyle.SHORT,
        Locale.getDefault()
    ) ?: currentDayName

    // Use the character morph animation
    DirectionalTextTransition(
        initialText = previousDayName,
        targetText = currentDayName,
        isAnimating = isAnimating,
        direction = direction,
        config = config,
        textStyle = textStyle,
        color = color,
        modifier = modifier,
        onAnimationComplete = onAnimationComplete,
    )
}

/**
 * A higher-level component that handles date transitions and applies the day name morphing effect.
 * This component automatically determines the animation direction and manages animation state.
 *
 * @param date The current date to display
 * @param onDateChanged Callback when date changes (useful for updating other UI elements)
 * @param config Configuration options for the animation
 * @param textStyle Text style to apply to the day name
 * @param color Text color
 * @param modifier Modifier for the composable
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnimatedDayName(
    date: LocalDate,
    onDateChanged: (LocalDate, LocalDate) -> Unit = { _, _ -> },
    config: DirectionalTransitionConfig = DirectionalTransitionConfig(),
    textStyle: TextStyle = MaterialTheme.typography.displaySmall.copy(
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold
    ),
    color: Color = LocalContentColor.current,
    modifier: Modifier = Modifier
) {
    // Track previous date and animation state
    var previousDate by remember { mutableStateOf<LocalDate?>(null) }
    var isAnimating by remember { mutableStateOf(false) }
    var morphDirection by remember { mutableStateOf(TransitionDirection.FORWARD) }

    // When date changes, trigger animation
    LaunchedEffect(date) {
        if (previousDate != null && previousDate != date) {
            // Determine animation direction based on days of week
            morphDirection = determineTransitionDirection(date, previousDate!!)

            // Notify about date change
            onDateChanged(date, previousDate!!)

            // Start animation
            isAnimating = true
        } else if (previousDate == null) {
            previousDate = date
        }
    }

    DayNameDirectionalTransition(
        currentDate = date,
        previousDate = previousDate,
        isAnimating = isAnimating,
        direction = morphDirection,
        config = config,
        textStyle = textStyle,
        color = color,
        modifier = modifier,
        onAnimationComplete = {
            // Update previous date after animation
            previousDate = date
            isAnimating = false
        }
    )
}

/**
 * Determines the correct animation direction when transitioning between dates.
 * This considers the calendar flow (forward in time = bottom to top, backward = top to bottom).
 */
@RequiresApi(Build.VERSION_CODES.O)
fun determineTransitionDirection(newDate: LocalDate, oldDate: LocalDate): TransitionDirection {
    // Handle special case of week wrap-around
    if (newDate.dayOfWeek.value == 1 && oldDate.dayOfWeek.value == 7) {
        return TransitionDirection.FORWARD  // Sunday to Monday is moving forward
    }

    if (newDate.dayOfWeek.value == 7 && oldDate.dayOfWeek.value == 1) {
        return TransitionDirection.BACKWARD  // Monday to Sunday is moving backward
    }

    // Regular weekday progression
    return if (newDate.dayOfWeek.value > oldDate.dayOfWeek.value) {
        TransitionDirection.FORWARD
    } else {
        TransitionDirection.BACKWARD
    }
}