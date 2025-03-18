package com.binissa.calendar.calendar.components.morph

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sin

/**
 * Configuration for the DirectionalTextTransition effect.
 * 
 * @param transitionDuration Duration of the transition animation in milliseconds.
 * @param staggerDelay Base delay between character animations in milliseconds.
 * @param staggerFactor Multiplier for stagger delay between subsequent characters.
 * @param trailCount Number of ghost trail instances for each character.
 * @param maxVerticalOffset Maximum vertical movement distance during animation.
 * @param maxHorizontalOffset Maximum horizontal sway during animation.
 * @param maxBlur Maximum blur amount applied to trailing instances.
 * @param rotationAmount Maximum rotation angle in degrees.
 * @param scaleRange Pair of (minimum, maximum) scale values during transition.
 * @param charSpacing Horizontal spacing between characters.
 * @param bunching Power factor for trail bunching (higher = more bunched).
 * @param easingCurve Easing curve for the animation.
 */
data class DirectionalTransitionConfig(
    val transitionDuration: Int = 750,
    val staggerDelay: Long = 120,
    val staggerFactor: Float = 1.3f,
    val trailCount: Int = 8,
    val maxVerticalOffset: Dp = 48.dp,
    val maxHorizontalOffset: Dp = 3.dp,
    val maxBlur: Dp = 10.dp,
    val rotationAmount: Float = 6f,
    val scaleRange: Pair<Float, Float> = Pair(0.6f, 1.15f),
    val charSpacing: Dp = 2.dp,
    val bunching: Float = 2.7f,
    val easingCurve: Easing = FastOutSlowInEasing
)

/**
 * Direction of the transition animation.
 */
enum class TransitionDirection {
    FORWARD, BACKWARD
}
