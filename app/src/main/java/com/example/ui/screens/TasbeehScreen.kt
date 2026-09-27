package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.DeveloperCard
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.viewmodel.DuaViewModel

@Composable
fun TasbeehScreen(
    viewModel: DuaViewModel,
    modifier: Modifier = Modifier
) {
    val count by viewModel.tasbeehCount.collectAsStateWithLifecycle()
    val target by viewModel.tasbeehTarget.collectAsStateWithLifecycle()
    val rounds by viewModel.tasbeehRounds.collectAsStateWithLifecycle()
    val selectedPreset by viewModel.selectedTasbeehPreset.collectAsStateWithLifecycle()
    val displaySettings by viewModel.displaySettings.collectAsStateWithLifecycle()

    var showResetDialog by remember { mutableStateOf(false) }

    val progress = if (target > 0) (count.toFloat() / target).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("তাসবীহ রিসেট করুন") },
            text = { Text("আপনি কি বর্তমান গণনা ও চক্কর শূন্য (০) করতে চান?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetTasbeeh()
                        showResetDialog = false
                    }
                ) {
                    Text("হ্যাঁ, রিসেট করুন", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Dhikr Presets Horizontal Scroll
        Text(
            text = "যিকির নির্বাচন করুন",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(horizontal = 18.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            viewModel.tasbeehPresets.forEach { preset ->
                val isSelected = selectedPreset == preset
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setTasbeehPreset(preset) },
                    label = { Text(preset.nameBn, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("preset_${preset.nameEn}")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Active Dhikr Display Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedPreset.arabicText,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${selectedPreset.nameBn} (${selectedPreset.nameEn})",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Large Interactive Circular Tasbeeh Button
        Box(
            modifier = Modifier
                .size(240.dp)
                .testTag("tasbeeh_touch_counter"),
            contentAlignment = Alignment.Center
        ) {
            // Circular Progress Indicator Ring
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.size(236.dp),
                color = GoldAccent,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = 10.dp
            )

            // Inner Ripple Tap Circle
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(EmeraldPrimary, EmeraldDark)
                        )
                    )
                    .clickable { viewModel.incrementTasbeeh() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.displayMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Text(
                        text = "লক্ষ্য: $target",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ট্যাপ করুন",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Target Selector & Rounds info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rounds Badge
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldContainer.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "মোট চক্কর: ",
                        style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        text = "$rounds",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    )
                }
            }

            // Quick Target Selector Chips
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(33, 99, 100).forEach { goal ->
                    val isTarget = target == goal
                    FilledTonalButton(
                        onClick = { viewModel.setTasbeehTarget(goal) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isTarget) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isTarget) Color.White else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text(text = "$goal", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Reset and Vibration Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { showResetDialog = true },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("রিসেট করুন")
            }

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedButton(
                onClick = { viewModel.toggleHapticFeedback() },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Vibration,
                    contentDescription = "Vibration",
                    tint = if (displaySettings.hapticFeedbackEnabled) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (displaySettings.hapticFeedbackEnabled) "ভাইব্রেশন: অন" else "ভাইব্রেশন: অফ")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Developer Credit Card
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            DeveloperCard(compact = true)
        }
    }
}
