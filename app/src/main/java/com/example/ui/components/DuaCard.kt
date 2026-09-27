package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DuaItem
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.viewmodel.AppDisplaySettings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DuaCard(
    dua: DuaItem,
    isBookmarked: Boolean,
    currentCount: Int,
    displaySettings: AppDisplaySettings,
    onCardClick: () -> Unit,
    onBookmarkToggle: () -> Unit,
    onCountIncrement: () -> Unit,
    onCountReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isCompleted = currentCount >= dua.targetCount && dua.targetCount > 0

    val cardBorderColor by animateColorAsState(
        targetValue = if (isCompleted) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        label = "borderColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("dua_card_${dua.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Category Badge & Ruqyah Tag & Bookmark Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Category chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = dua.category.nameBn,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    // Ruqyah Tag if applicable
                    if (dua.isRuqyah) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldContainer)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.HealthAndSafety,
                                    contentDescription = "Ruqyah",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "রুকইয়াহ" + (dua.ruqyahType?.let { " • $it" } ?: ""),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onBookmarkToggle,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("bookmark_button_${dua.id}")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title in Bangla and English
            Text(
                text = dua.titleBn,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Text(
                text = dua.titleEn,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Arabic Text Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(14.dp)
            ) {
                Text(
                    text = dua.arabicText,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = displaySettings.arabicFontSize.sp,
                        lineHeight = (displaySettings.arabicFontSize * 1.6f).sp,
                        fontFamily = FontFamily.Serif,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Bangla Pronunciation (Transliteration)
            if (displaySettings.showBanglaPronunciation && dua.banglaPronunciation.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "উচ্চারণ:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = dua.banglaPronunciation,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    )
                }
            }

            // Bangla Meaning
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "অর্থ: ${dua.banglaMeaning}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp,
                    fontSize = 13.5.sp
                )
            )

            // English Translation (Toggleable)
            if (displaySettings.showEnglishTranslation && dua.englishTranslation.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Meaning: ${dua.englishTranslation}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp,
                        fontSize = 12.sp
                    )
                )
            }

            // Reference Source
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "সূত্র: ",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
                Text(
                    text = dua.reference,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Counter & Actions Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive Counter Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tap to count button
                    FilledTonalIconButton(
                        onClick = onCountIncrement,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("count_increment_${dua.id}"),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (isCompleted) EmeraldPrimary else MaterialTheme.colorScheme.primaryContainer,
                            contentColor = if (isCompleted) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = "+1",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Column {
                        Text(
                            text = if (isCompleted) "সম্পন্ন ✓" else "$currentCount / ${dua.targetCount} বার",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isCompleted) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        if (dua.targetCount > 1) {
                            val progress = (currentCount.toFloat() / dua.targetCount).coerceIn(0f, 1f)
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .width(70.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = EmeraldPrimary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }

                    if (currentCount > 0) {
                        IconButton(
                            onClick = onCountReset,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Count",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Action Buttons: Copy & Share
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(
                                dua.titleBn,
                                "${dua.titleBn} (${dua.titleEn})\n\n" +
                                "${dua.arabicText}\n\n" +
                                "উচ্চারণ: ${dua.banglaPronunciation}\n\n" +
                                "অর্থ: ${dua.banglaMeaning}\n\n" +
                                "English: ${dua.englishTranslation}\n\n" +
                                "রেফারেন্স: ${dua.reference}\n\n" +
                                "Developed By Ammar Khandoker"
                            )
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "দোয়া কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("copy_button_${dua.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "✨ ${dua.titleBn} (${dua.titleEn})\n\n" +
                                    "${dua.arabicText}\n\n" +
                                    "উচ্চারণ: ${dua.banglaPronunciation}\n\n" +
                                    "অর্থ: ${dua.banglaMeaning}\n\n" +
                                    "English: ${dua.englishTranslation}\n\n" +
                                    "রেফারেন্স: ${dua.reference}\n\n" +
                                    "ফজিলত: ${dua.virtueBn}\n\n" +
                                    "— ইসলামিক দোয়া ও রুকইয়াহ অ্যাপ\n" +
                                    "Developed By Ammar Khandoker"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "দোয়া শেয়ার করুন"))
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("share_button_${dua.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
