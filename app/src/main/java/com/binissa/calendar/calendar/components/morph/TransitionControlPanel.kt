package com.binissa.calendar.calendar.components.morph


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.binissa.calendar.calendar.components.morph.AnimatedDayName
import com.binissa.calendar.calendar.components.morph.DirectionalTransitionConfig
import com.binissa.calendar.calendar.components.morph.TransitionDirection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransitionControlPanel(
    modifier: Modifier = Modifier,
    initialConfig: DirectionalTransitionConfig = DirectionalTransitionConfig(),
    onConfigChanged: (DirectionalTransitionConfig) -> Unit
) {
    var config by remember { mutableStateOf(initialConfig) }
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var expandedSection by remember { mutableStateOf("none") }
    val scope = rememberCoroutineScope()

    // Define presets with visual indicators
    val presets = remember {
        listOf(
            TransitionPreset(
                name = "Subtle",
                description = "Gentle motion with minimal blur",
                visualTags = listOf("Clean", "Simple"),
                config = DirectionalTransitionConfig(
                    transitionDuration = 400,
                    staggerDelay = 70,
                    trailCount = 2,
                    maxBlur = 4.dp,
                    rotationAmount = 0f,
                    maxVerticalOffset = 12.dp,
                    maxHorizontalOffset = 1.dp,
                    bunching = 1.2f
                )
            ),
            TransitionPreset(
                name = "Classic Fade",
                description = "Traditional text crossfade with motion",
                visualTags = listOf("Smooth", "Professional"),
                config = DirectionalTransitionConfig(
                    transitionDuration = 500,
                    staggerDelay = 80,
                    trailCount = 3,
                    maxBlur = 8.dp,
                    rotationAmount = 0f,
                    maxVerticalOffset = 20.dp,
                    maxHorizontalOffset = 0.dp,
                    bunching = 1.5f
                )
            ),
            TransitionPreset(
                name = "Smear",
                description = "Dramatic motion blur effect",
                visualTags = listOf("Dynamic", "Bold"),
                config = DirectionalTransitionConfig(
                    transitionDuration = 650,
                    staggerDelay = 60,
                    trailCount = 7,
                    maxBlur = 16.dp,
                    rotationAmount = 3f,
                    maxVerticalOffset = 30.dp,
                    maxHorizontalOffset = 2.dp,
                    bunching = 2.2f
                )
            ),
            TransitionPreset(
                name = "Ghostly",
                description = "Extended blur trails with transparency",
                visualTags = listOf("Ethereal", "Artistic"),
                config = DirectionalTransitionConfig(
                    transitionDuration = 800,
                    staggerDelay = 90,
                    trailCount = 9,
                    maxBlur = 20.dp,
                    rotationAmount = 0f,
                    maxVerticalOffset = 35.dp,
                    maxHorizontalOffset = 0.dp,
                    bunching = 3.0f
                )
            ),
            TransitionPreset(
                name = "Playful",
                description = "Bouncy motion with rotation",
                visualTags = listOf("Fun", "Energetic"),
                config = DirectionalTransitionConfig(
                    transitionDuration = 700,
                    staggerDelay = 100,
                    trailCount = 5,
                    maxBlur = 10.dp,
                    rotationAmount = 10f,
                    maxVerticalOffset = 25.dp,
                    maxHorizontalOffset = 5.dp,
                    bunching = 1.8f,
                    scaleRange = Pair(0.7f, 1.2f)
                )
            ),
            TransitionPreset(
                name = "Techno",
                description = "Sharp, digital appearance",
                visualTags = listOf("Modern", "Precise"),
                config = DirectionalTransitionConfig(
                    transitionDuration = 550,
                    staggerDelay = 50,
                    trailCount = 4,
                    maxBlur = 6.dp,
                    rotationAmount = 2f,
                    maxVerticalOffset = 28.dp,
                    maxHorizontalOffset = 1.dp,
                    bunching = 0.8f,
                    scaleRange = Pair(0.9f, 1.1f)
                )
            )
        )
    }

    var selectedPreset by remember { mutableStateOf(presets.first()) }
    var isCustom by remember { mutableStateOf(false) }

    LaunchedEffect(config) {
        onConfigChanged(config)

        // Check if config matches a preset
        val matchingPreset = presets.firstOrNull { it.config == config }
        if (matchingPreset != null) {
            selectedPreset = matchingPreset
            isCustom = false
        } else {
            isCustom = true
        }
    }

    // Animate the date change for preview
    var isAnimating by remember { mutableStateOf(false) }
    fun animatePreview(newDate: LocalDate) {
        scope.launch {
            isAnimating = true
            currentDate = newDate
            delay(config.transitionDuration.toLong() + 200)
            isAnimating = false
        }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Preview section with elegant card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Preview",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Animation preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedDayName(
                        date = currentDate,
                        config = config,
                        textStyle = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Day selection buttons in an elegant row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Previous/Next buttons
                    FilledTonalIconButton(
                        onClick = { animatePreview(currentDate.minusDays(1)) },
                        enabled = !isAnimating
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Previous Day"
                        )
                    }

                    // Days of week buttons
                    DayOfWeek.values().take(3).forEach { day ->
                        DayButton(
                            day = day,
                            isSelected = currentDate.dayOfWeek == day,
                            onClick = { animatePreview(LocalDate.now().with(day)) },
                            enabled = !isAnimating
                        )
                    }

                    FilledTonalIconButton(
                        onClick = { animatePreview(currentDate.plusDays(1)) },
                        enabled = !isAnimating
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Next Day"
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    DayOfWeek.values().drop(3).forEach { day ->
                        DayButton(
                            day = day,
                            isSelected = currentDate.dayOfWeek == day,
                            onClick = { animatePreview(LocalDate.now().with(day)) },
                            enabled = !isAnimating
                        )
                    }
                }
            }
        }

        // Presets selector with visual indicators
        SectionHeader(
            title = "Effect Style",
            icon = Icons.Rounded.Star,
            expanded = expandedSection == "presets",
            onExpandToggle = { expandedSection = if (expandedSection == "presets") "none" else "presets" }
        )

        AnimatedVisibility(
            visible = expandedSection == "presets",
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            PresetSelector(
                presets = presets,
                selectedPreset = if (isCustom) null else selectedPreset,
                onPresetSelected = { preset ->
                    config = preset.config
                    selectedPreset = preset
                    isCustom = false
                }
            )
        }

        // Quick controls for most common adjustments
        SectionHeader(
            title = "Quick Adjustments",
            icon = Icons.Rounded.Edit,
            expanded = expandedSection == "quick",
            onExpandToggle = { expandedSection = if (expandedSection == "quick") "none" else "quick" }
        )

        AnimatedVisibility(
            visible = expandedSection == "quick",
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            QuickAdjustments(
                config = config,
                onConfigChanged = { newConfig ->
                    config = newConfig
                    isCustom = true
                }
            )
        }

        // Advanced controls
        SectionHeader(
            title = "Advanced Settings",
            icon = Icons.Rounded.Edit,
            expanded = expandedSection == "advanced",
            onExpandToggle = { expandedSection = if (expandedSection == "advanced") "none" else "advanced" }
        )

        AnimatedVisibility(
            visible = expandedSection == "advanced",
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            AdvancedControls(
                config = config,
                onConfigChanged = { newConfig ->
                    config = newConfig
                    isCustom = true
                }
            )
        }

        // Reset/Save button row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = {
                    config = DirectionalTransitionConfig()
                    isCustom = false
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = "Reset",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reset")
            }

            if (isCustom) {
                Button(
                    onClick = {
                        // Save as custom preset (in a real app)
                    }
                ) {
                    Text("Save Custom Preset")
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector,
    expanded: Boolean,
    onExpandToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onExpandToggle)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Icon(
            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = if (expanded) "Collapse" else "Expand",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Divider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun PresetSelector(
    presets: List<TransitionPreset>,
    selectedPreset: TransitionPreset?,
    onPresetSelected: (TransitionPreset) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        presets.chunked(2).forEach { rowPresets ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowPresets.forEach { preset ->
                    val isSelected = preset == selectedPreset

                    PresetCard(
                        preset = preset,
                        isSelected = isSelected,
                        onClick = { onPresetSelected(preset) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Add empty space if odd number
                if (rowPresets.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PresetCard(
    preset: TransitionPreset,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    }

    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.outlinedCardColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = preset.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = preset.description,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Visual tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                preset.visualTags.forEach { tag ->
                    PresetTag(tag = tag, isSelected = isSelected)
                }
            }
        }
    }
}

@Composable
private fun PresetTag(tag: String, isSelected: Boolean) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
    }

    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = tag,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Composable
private fun QuickAdjustments(
    config: DirectionalTransitionConfig,
    onConfigChanged: (DirectionalTransitionConfig) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        // Intensity slider
        var intensity by remember { mutableFloatStateOf(calculateIntensity(config)) }

        LabeledSlider(
            label = "Effect Intensity",
            value = intensity,
            range = 0f..1f,
            steps = 10,
            valueDisplay = { "%.0f%%".format(it * 100) },
            onValueChange = {
                intensity = it
                onConfigChanged(applyIntensity(config, intensity))
            }
        )

        // Blur amount
        LabeledSlider(
            label = "Blur Amount",
            value = config.maxBlur.value,
            range = 0f..20f,
            valueDisplay = { "%.1f".format(it) },
            onValueChange = { onConfigChanged(config.copy(maxBlur = it.dp)) }
        )

        // Trail count with tooltip
        SliderWithHelp(
            label = "Trail Count",
            value = config.trailCount.toFloat(),
            range = 1f..12f,
            steps = 11,
            valueDisplay = { "${it.toInt()}" },
            helpText = "Number of trailing ghost instances. Higher values create more pronounced motion blur effect.",
            onValueChange = { onConfigChanged(config.copy(trailCount = it.toInt())) }
        )

        // Duration slider
        LabeledSlider(
            label = "Duration (ms)",
            value = config.transitionDuration.toFloat(),
            range = 200f..1000f,
            steps = 16,
            valueDisplay = { "${it.toInt()}" },
            onValueChange = { onConfigChanged(config.copy(transitionDuration = it.toInt())) }
        )
    }
}

@Composable
private fun AdvancedControls(
    config: DirectionalTransitionConfig,
    onConfigChanged: (DirectionalTransitionConfig) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        // Group sliders into categories
        SectionSubheader("Animation Timing")

        LabeledSlider(
            label = "Stagger Delay (ms)",
            value = config.staggerDelay.toFloat(),
            range = 0f..200f,
            steps = 20,
            valueDisplay = { "${it.toInt()}" },
            onValueChange = { onConfigChanged(config.copy(staggerDelay = it.toLong())) }
        )

        LabeledSlider(
            label = "Stagger Factor",
            value = config.staggerFactor,
            range = 0.5f..3f,
            valueDisplay = { "%.1f".format(it) },
            onValueChange = { onConfigChanged(config.copy(staggerFactor = it)) }
        )

        SectionSubheader("Visual Effects")

        SliderWithHelp(
            label = "Bunching Factor",
            value = config.bunching,
            range = 0.5f..5f,
            valueDisplay = { "%.1f".format(it) },
            helpText = "Controls how trails bunch together. Higher values create a more concentrated smearing effect at the end of the trail.",
            onValueChange = { onConfigChanged(config.copy(bunching = it)) }
        )

        LabeledSlider(
            label = "Rotation (°)",
            value = config.rotationAmount,
            range = 0f..20f,
            valueDisplay = { "%.1f°".format(it) },
            onValueChange = { onConfigChanged(config.copy(rotationAmount = it)) }
        )

        SectionSubheader("Movement")

        LabeledSlider(
            label = "Vertical Offset",
            value = config.maxVerticalOffset.value,
            range = 0f..60f,
            valueDisplay = { "%.0f".format(it) },
            onValueChange = { onConfigChanged(config.copy(maxVerticalOffset = it.dp)) }
        )

        LabeledSlider(
            label = "Horizontal Offset",
            value = config.maxHorizontalOffset.value,
            range = 0f..10f,
            valueDisplay = { "%.1f".format(it) },
            onValueChange = { onConfigChanged(config.copy(maxHorizontalOffset = it.dp)) }
        )

        SectionSubheader("Scale Effects")

        LabeledSlider(
            label = "Min Scale",
            value = config.scaleRange.first,
            range = 0.5f..1f,
            valueDisplay = { "%.2f".format(it) },
            onValueChange = {
                onConfigChanged(config.copy(
                    scaleRange = Pair(it, config.scaleRange.second)
                ))
            }
        )

        LabeledSlider(
            label = "Max Scale",
            value = config.scaleRange.second,
            range = 1f..1.5f,
            valueDisplay = { "%.2f".format(it) },
            onValueChange = {
                onConfigChanged(config.copy(
                    scaleRange = Pair(config.scaleRange.first, it)
                ))
            }
        )
    }
}

@Composable
private fun SectionSubheader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun LabeledSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    valueDisplay: (Float) -> String = { "%.1f".format(it) },
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = valueDisplay(value),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = steps,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            )
        )
    }
}

@Composable
private fun SliderWithHelp(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    valueDisplay: (Float) -> String = { "%.1f".format(it) },
    helpText: String,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = valueDisplay(value),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = steps,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            )
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DayButton(
    day: DayOfWeek,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val dayName = day.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    val buttonColors = if (isSelected) {
        ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    } else {
        ButtonDefaults.filledTonalButtonColors()
    }

    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 4.dp),
        enabled = enabled,
        colors = buttonColors
    ) {
        Text(
            text = dayName.take(1),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// Helper functions to calculate and apply intensity
private fun calculateIntensity(config: DirectionalTransitionConfig): Float {
    // Simplified - in reality would need more advanced normalization
    val blurFactor = config.maxBlur.value / 20f
    val trailFactor = config.trailCount / 12f
    val verticalFactor = config.maxVerticalOffset.value / 60f

    return (blurFactor + trailFactor + verticalFactor) / 3f
}

private fun applyIntensity(config: DirectionalTransitionConfig, intensity: Float): DirectionalTransitionConfig {
    // Scale key parameters based on intensity
    return config.copy(
        maxBlur = (intensity * 20f).dp,
        trailCount = (1 + intensity * 11).toInt(),
        maxVerticalOffset = (intensity * 60f).dp,
        bunching = 1f + intensity * 4f
    )
}

// Data class for presets
data class TransitionPreset(
    val name: String,
    val description: String,
    val visualTags: List<String>,
    val config: DirectionalTransitionConfig
)