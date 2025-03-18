package com.binissa.calendar.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.binissa.calendar.calendar.components.morph.AnimatedDayName
import com.binissa.calendar.calendar.components.morph.DirectionalTransitionConfig
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * A Material 3 styled calendar header with smooth transitioning animations.
 *
 * @param selectedDate The currently selected date
 * @param onPreviousDayClick Callback when navigating to previous day
 * @param onNextDayClick Callback when navigating to next day
 * @param accentColor The accent color for the current day indicator (defaults to primary)
 * @param modifier Modifier for the composable
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnimatedCalendarHeader(
    selectedDate: LocalDate,
    onPreviousDayClick: () -> Unit,
    onNextDayClick: () -> Unit,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    // Animation state to coordinate date transitions
    val transitionConfig = remember {
        DirectionalTransitionConfig(
            transitionDuration = 650,
            staggerDelay = 80,
            staggerFactor = 1.4f,
            trailCount = 7,
            maxVerticalOffset = 10.dp,
            maxHorizontalOffset = 2.dp,
            maxBlur = 4.dp,
            rotationAmount = 4f,
            scaleRange = Pair(0.2f, 1.1f),
            bunching = 2.5f
        )
    }

    Row(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onPreviousDayClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                    contentDescription = "Previous Day",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AnimatedDayName(
                    date = selectedDate,
                    config = transitionConfig,
                    textStyle = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(onClick = onNextDayClick) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowRight,
                    contentDescription = "Next Day",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Year with subtle animation
        Text(
            text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy")),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
