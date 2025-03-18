package com.binissa.calendar.calendar.components.morph

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * A composable that animates text transitions with a directional smearing/motion blur effect.
 * Characters that change will display a ghost trail effect in the specified direction.
 *
 * @param initialText The initial text state.
 * @param targetText The target text state to transition to.
 * @param isAnimating Whether the animation is currently active.
 * @param direction Direction of the animation.
 * @param config Configuration parameters for the animation effect.
 * @param modifier Modifier for the composable.
 * @param textStyle Text style for the characters.
 * @param color Color of the text.
 * @param onAnimationComplete Callback invoked when animation completes.
 */
@Composable
fun DirectionalTextTransition(
    initialText: String,
    targetText: String,
    isAnimating: Boolean,
    direction: TransitionDirection = TransitionDirection.FORWARD,
    config: DirectionalTransitionConfig = DirectionalTransitionConfig(),
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.displaySmall,
    color: Color = Color.White,
    onAnimationComplete: () -> Unit = {}
) {
    // Determine max length to ensure stable layout
    val maxLength = maxOf(initialText.length, targetText.length)

    // Create list of character transitions
    val charPairs = List(maxLength) { index ->
        val fromChar = if (index < initialText.length) initialText[index] else ' '
        val toChar = if (index < targetText.length) targetText[index] else ' '
        Pair(fromChar, toChar)
    }

    // Track overall animation completion
    val animationsInProgress = remember { mutableIntStateOf(0) }

    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            animationsInProgress.intValue = charPairs.count { it.first != it.second }
        }
    }

    // Overall animation completion tracking
    fun onCharAnimationComplete() {
        animationsInProgress.intValue = (animationsInProgress.intValue - 1).coerceAtLeast(0)
        if (animationsInProgress.intValue == 0) {
            onAnimationComplete()
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        charPairs.forEachIndexed { index, (fromChar, toChar) ->
            Box(
//                modifier = Modifier.wrapContentWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (fromChar == toChar) {
                    // If characters are the same, just show the character without animation
                    Text(
                        text = toChar.toString(),
                        style = textStyle,
                        color = color,
                        textAlign = TextAlign.Center
                    )
                } else {
                    // Calculate staggered delay for this character
                    val staggerDelay = (config.staggerDelay * index * config.staggerFactor).toLong()

                    // Animate transitioning characters
                    CharacterTransition(
                        fromChar = fromChar,
                        toChar = toChar,
                        isAnimating = isAnimating,
                        direction = direction,
                        config = config,
                        delayMillis = staggerDelay,
                        textStyle = textStyle,
                        color = color,
                        letterIndex = index,
                        onComplete = { onCharAnimationComplete() }
                    )
                }
            }
        }
    }
}
