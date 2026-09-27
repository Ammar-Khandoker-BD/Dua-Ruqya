package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.DuaCategory
import com.example.data.model.DuaItem
import com.example.ui.components.DeveloperCard
import com.example.ui.components.DuaCard
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.DuaViewModel

@Composable
fun HomeScreen(
    viewModel: DuaViewModel,
    modifier: Modifier = Modifier
) {
    val filteredDuas by viewModel.filteredDuas.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val counters by viewModel.counters.collectAsStateWithLifecycle()
    val displaySettings by viewModel.displaySettings.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val bookmarkedIds = bookmarks.map { it.duaId }.toSet()
    val counterMap = counters.associate { it.duaId to it.currentCount }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_dua_list"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Islamic Banner Section
        if (searchQuery.isBlank()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(145.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.islamic_banner),
                        contentDescription = "Islamic Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Soft dark gradient overlay for text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                                )
                            )
                    )

                    // Hero Text Content
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Featured",
                                tint = GoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "মাসনূন দোয়া ও রুকইয়াহ শরীয়াহ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "বাংলা, আরবি ও ইংরেজি উচ্চারণে সহীহ আমল",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Text(
                            text = "Developed By Ammar Khandoker",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Horizontal Category Chips
        item {
            val categories = DuaCategory.values()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectCategory(category) },
                        label = {
                            Text(
                                text = category.nameBn,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("category_chip_${category.id}")
                    )
                }
            }
        }

        // Duas Section Title / Count
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (searchQuery.isNotBlank()) "অনুসন্ধানের ফলাফল (${filteredDuas.size})" else "${selectedCategory.nameBn} (${filteredDuas.size})",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Text(
                    text = "ট্যাপ করে বিস্তারিত পড়ুন",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
            }
        }

        // Empty state when search has no results
        if (filteredDuas.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "Not Found",
                            modifier = Modifier.size(54.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "কোনো দোয়া পাওয়া যায়নি",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "অন্য শব্দ দিয়ে অনুসন্ধান করুন অথবা ক্যাটাগরি পরিবর্তন করুন",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Dua Cards List
        items(
            items = filteredDuas,
            key = { it.id }
        ) { dua ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                DuaCard(
                    dua = dua,
                    isBookmarked = bookmarkedIds.contains(dua.id),
                    currentCount = counterMap[dua.id] ?: 0,
                    displaySettings = displaySettings,
                    onCardClick = { viewModel.openDuaDetail(dua) },
                    onBookmarkToggle = { viewModel.toggleBookmark(dua.id) },
                    onCountIncrement = { viewModel.incrementDuaCount(dua.id, dua.targetCount) },
                    onCountReset = { viewModel.resetDuaCount(dua.id) }
                )
            }
        }

        // Developer Credit Card at Bottom of Home
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                DeveloperCard()
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
