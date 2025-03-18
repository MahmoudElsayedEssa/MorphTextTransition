package com.binissa.calendar.calendar.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.binissa.calendar.calendar.TaskItem
import com.binissa.calendar.calendar.TaskType

@Composable
fun AnimatedTaskItem(
    task: TaskItem,
    onClick: () -> Unit,
    onCheckboxClick: () -> Unit,
    isVisible: Boolean = true,
    index: Int = 0
) {
    val animatedElevation by animateFloatAsState(
        targetValue = if (task.isCompleted) 0f else 4f, label = "cardElevation"
    )

    // Animation state
    val revealConfig = BlurTextRevealConfig(
        direction = RevealDirection.START_TO_END,
        initialBlur = 6f,
        initialOffset = 24f,
        blurDuration = 700,
        alphaDuration = 500
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) Color.LightGray.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon based on task type with animated transitions
            when (task.type) {
                TaskType.BIRTHDAY -> {
                    Text(
                        text = "✹",
                        color = Color(0xFFFF5252),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }

                TaskType.STAR -> {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Star",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }

                TaskType.CHECKBOX -> {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onCheckboxClick
                            )
                            .padding(end = 16.dp)
                    ) {
                        if (task.isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.Gray
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = "Not completed",
                                tint = Color.Gray
                            )
                        }
                    }
                }

                TaskType.MOON -> {
                    Text(
                        text = "🌙",
                        fontSize = 20.sp,
                        color = Color(0xFF7E57C2),
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            }

            // Task title with blur reveal animation
            BlurTextReveal(
                text = task.title,
                isTriggered = isVisible,
                color = if (task.isCompleted) Color.Gray else Color.Black,
                config = revealConfig,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (task.isHighlighted) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 16.sp
                ),
                staggerDelay = 15L,
                modifier = Modifier.weight(1f)
            )

            // Time if available
            if (task.time != null) {
                BlurTextReveal(
                    text = task.time,
                    isTriggered = isVisible,
                    color = Color.Gray,
                    config = revealConfig.copy(
                        direction = RevealDirection.TOP_TO_BOTTOM, blurDuration = 600
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    staggerDelay = 20L
                )
            }
        }
    }
}