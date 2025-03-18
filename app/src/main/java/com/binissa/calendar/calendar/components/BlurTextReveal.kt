package com.binissa.calendar.calendar.components


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class RevealDirection {
    BOTTOM_TO_TOP, TOP_TO_BOTTOM, START_TO_END, END_TO_START
}

@Stable
data class BlurTextRevealConfig(
    val initialBlur: Float = 8f,
    val initialOffset: Float = 4f,
    val blurDuration: Int = 600,
    val alphaDuration: Int = 400,
    val glowBlur: Float = 16f,
    val glowAlpha: Float = 0.3f,
    val direction: RevealDirection = RevealDirection.BOTTOM_TO_TOP
)


@Composable
private fun BlurCharReveal(
    char: Char,
    color: Color,
    delayMillis: Long,
    isTriggered: Boolean,
    config: BlurTextRevealConfig = BlurTextRevealConfig(),
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
) {
    val blur = remember { Animatable(config.initialBlur) }
    val alpha = remember { Animatable(0f) }
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    // Reset animations when trigger is false
    LaunchedEffect(isTriggered) {
        if (!isTriggered) {
            blur.snapTo(config.initialBlur)
            alpha.snapTo(0f)
            when (config.direction) {
                RevealDirection.BOTTOM_TO_TOP -> offsetY.snapTo(config.initialOffset)
                RevealDirection.TOP_TO_BOTTOM -> offsetY.snapTo(-config.initialOffset)
                RevealDirection.START_TO_END -> offsetX.snapTo(-config.initialOffset)
                RevealDirection.END_TO_START -> offsetX.snapTo(config.initialOffset)
            }
        }
    }

    // Start animations when triggered
    LaunchedEffect(isTriggered) {
        if (isTriggered) {
            delay(delayMillis)

            launch {
                alpha.animateTo(
                    targetValue = 1f, animationSpec = tween(
                        durationMillis = config.alphaDuration, easing = FastOutSlowInEasing
                    )
                )
            }

            launch {
                blur.animateTo(
                    targetValue = 0f, animationSpec = tween(
                        durationMillis = config.blurDuration, easing = FastOutSlowInEasing
                    )
                )
            }

            launch {
                offsetX.animateTo(
                    targetValue = 0f, animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow
                    )
                )
            }

            launch {
                offsetY.animateTo(
                    targetValue = 0f, animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow
                    )
                )
            }
        }
    }

    Box {
        // Glow effect
        Text(text = char.toString(),
            style = textStyle,
            color = color.copy(alpha = config.glowAlpha),
            modifier = modifier
                .graphicsLayer {
                    this.alpha = alpha.value
                }
                .offset(x = offsetX.value.dp, y = offsetY.value.dp)
                .blur(
                    config.glowBlur.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded
                ))

        // Main character
        Text(text = char.toString(),
            style = textStyle,
            color = color,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            modifier = modifier
                .graphicsLayer {
                    this.alpha = alpha.value
                }
                .offset(x = offsetX.value.dp, y = offsetY.value.dp)
                .blur(
                    blur.value.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded
                ))
    }
}

@Composable
fun BlurTextReveal(
    text: String,
    color: Color = Color.Unspecified,
    isTriggered: Boolean,
    modifier: Modifier = Modifier,
    config: BlurTextRevealConfig = BlurTextRevealConfig(),
    staggerDelay: Long = 50L,
    textStyle: TextStyle = MaterialTheme.typography.displayLarge,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        text.forEachIndexed { index, char ->
            key(index) {
                BlurCharReveal(
                    char = char,
                    color = color,
                    delayMillis = index * staggerDelay,
                    isTriggered = isTriggered,
                    config = config,
                    textStyle = textStyle,
                    fontWeight = fontWeight,
                    fontFamily = fontFamily
                )
            }
        }
    }
}