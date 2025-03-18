package com.binissa.calendar.calendar.components.morph

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sin

/**
 * Animates a single character transition with ghost trail effect.
 */
@Composable
fun CharacterTransition(
    fromChar: Char,
    toChar: Char,
    isAnimating: Boolean,
    direction: TransitionDirection,
    config: DirectionalTransitionConfig,
    delayMillis: Long = 0,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
    ),
    color: Color = Color.White,
    letterIndex: Int = 0,
    onComplete: () -> Unit = {}
) {
    // Animation state
    val progress = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // Direction multiplier for vertical movement
    val directionMultiplier = if (direction == TransitionDirection.FORWARD) 1f else -1f

    // Subtle horizontal movement (slightly different for each character)
    val horizontalSway = remember { (letterIndex % 2) * 2 - 1 } // Alternates between -1 and 1

    // Convert Dp values to pixels for calculations
    val maxVerticalOffsetPx = with(LocalDensity.current) { config.maxVerticalOffset.toPx() }
    val maxHorizontalOffsetPx = with(LocalDensity.current) {
        (config.maxHorizontalOffset.toPx() + (letterIndex % 3))
    }
    val maxBlurPx = with(LocalDensity.current) { config.maxBlur.toPx() }

    // Reset and trigger animations when isAnimating changes
    LaunchedEffect(isAnimating, fromChar, toChar) {
        if (isAnimating && fromChar != toChar) {
            progress.snapTo(0f)
            delay(delayMillis)
            scope.launch {
                progress.animateTo(
                    targetValue = 1f, animationSpec = tween(
                        durationMillis = config.transitionDuration, easing = config.easingCurve
                    )
                )
                delay(100) // Small delay before signaling completion for visual polish
                onComplete()
            }
        }
    }

    // Enhanced easing curves for more dramatic animation
    val incomingEasing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f) // Slight overshoot
    val outgoingEasing = CubicBezierEasing(0.36f, 0f, 0.66f, -0.56f) // Dramatic exit

    // Derived progress values
    val outgoingProgress = outgoingEasing.transform(progress.value)
    val incomingProgress = incomingEasing.transform(progress.value)

    Box(
        contentAlignment = Alignment.Center,
    ) {
        val (minScale, maxScale) = config.scaleRange
        val toCharScale = when {
            incomingProgress < 0.7f -> minScale + (incomingProgress * 1.3f * (1f - minScale))
            else -> maxScale - ((incomingProgress - 0.7f) * (maxScale - 1f) / 0.3f)
        }

        // Position with directional movement
        val toCharVerticalOffset =
            ((1f - incomingProgress) * maxVerticalOffsetPx * -directionMultiplier).dp
        val toCharHorizontalOffset =
            (sin(incomingProgress * Math.PI) * maxHorizontalOffsetPx * horizontalSway).dp
        val toCharRotation = (1f - incomingProgress) * directionMultiplier * -config.rotationAmount

        // Alpha with quick fade-in
        val toCharAlpha = when {
            incomingProgress < 0.3f -> incomingProgress * 3.33f
            else -> 1f
        }

        // Blur effect that clears quickly
        val toCharBlur = (maxBlurPx * (1f - incomingProgress).pow(2f)).dp

        // Render incoming character
        Text(
            text = toChar.toString(),
            style = textStyle,
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .offset(x = toCharHorizontalOffset, y = toCharVerticalOffset)
                .alpha(toCharAlpha)
                .blur(toCharBlur, BlurredEdgeTreatment.Unbounded)
                .scale(toCharScale)
                .rotate(toCharRotation)
        )

        // OUTGOING CHARACTER ANIMATION (MAIN)
        // -------------------------------
        val fromCharMainAlpha = (1f - outgoingProgress.pow(0.7f)).coerceIn(0f, 1f)
        val fromCharMainOffset =
            (outgoingProgress.pow(1.2f) * maxVerticalOffsetPx * directionMultiplier).dp
        val fromCharMainHOffset =
            (sin(outgoingProgress * Math.PI) * -maxHorizontalOffsetPx * horizontalSway).dp
        val fromCharMainBlur = (outgoingProgress.pow(0.8f) * maxBlurPx * 0.6f).dp

        // Scaling effect
        val fromCharMainScale = when {
            outgoingProgress < 0.2f -> 1f + (outgoingProgress * 0.1f)
            else -> 1.02f - ((outgoingProgress - 0.2f) * 0.3f)
        }

        // Rotation effect
        val fromCharMainRotation = outgoingProgress * directionMultiplier * config.rotationAmount

        // Render main outgoing character if visible
        if (fromCharMainAlpha > 0.01f) {
            Text(
                text = fromChar.toString(),
                style = textStyle,
                color = color,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .offset(x = fromCharMainHOffset, y = fromCharMainOffset)
                    .alpha(fromCharMainAlpha)
                    .blur(fromCharMainBlur, BlurredEdgeTreatment.Unbounded)
                    .scale(fromCharMainScale)
                    .rotate(fromCharMainRotation)
            )
        }

        // GHOST TRAIL EFFECT
        // -----------------
        for (i in 1..config.trailCount) {
            // Calculate trail position
            val trailPosition = i.toFloat() / config.trailCount

            // Apply non-linear distribution for bunching effect
            val trailCurve = 1f - (1f - trailPosition).pow(config.bunching)

            // Staggered appearance threshold
            val trailThreshold = trailPosition * (0.6f + letterIndex * 0.05f)

            if (outgoingProgress >= trailThreshold) {
                // Calculate ghost trail parameters
                val trailProgress = (outgoingProgress - trailThreshold) / (1f - trailThreshold)
                val trailOffsetBase = outgoingProgress * maxVerticalOffsetPx * directionMultiplier
                val trailOffsetVariation =
                    sin(trailCurve * Math.PI * 0.5f) * 3f * directionMultiplier
                val trailOffset =
                    (trailOffsetBase * (0.85f + trailCurve * 0.15f) + trailOffsetVariation).dp

                // Horizontal movement with slight variation
                val trailHorizontalOffset =
                    (sin(outgoingProgress * Math.PI) * -maxHorizontalOffsetPx * horizontalSway * (1f - trailCurve * 0.3f)).dp

                // Opacity calculation
                val trailFadeMultiplier = (1f - trailCurve).pow(1.7f)
                val trailAlpha = fromCharMainAlpha * trailFadeMultiplier * 0.85f

                // Blur calculation - increases with trail position
                val trailBlur = (maxBlurPx * (0.6f + trailCurve * 0.6f)).dp

                // Scale and rotation effects
                val trailScale = fromCharMainScale * (1f - 0.15f * trailCurve)
                val trailRotation = fromCharMainRotation * (1f + trailCurve * 0.4f)

                // Only render visible trail instances
                if (trailAlpha > 0.01f) {
                    Text(
                        text = fromChar.toString(),
                        style = textStyle,
                        color = color,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .offset(x = trailHorizontalOffset, y = trailOffset)
                            .alpha(trailAlpha)
                            .blur(trailBlur, BlurredEdgeTreatment.Unbounded)
                            .scale(trailScale)
                            .rotate(trailRotation)
                    )
                }
            }
        }
    }
}