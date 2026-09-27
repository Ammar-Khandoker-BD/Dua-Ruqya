package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppHeader
import com.example.ui.components.DuaDetailModal
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.RuqyahScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TasbeehScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.DuaViewModel

enum class NavigationTab(
    val titleBn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("দোয়া", Icons.Filled.MenuBook, Icons.Outlined.MenuBook, "tab_home"),
    RUQYAH("রুকইয়াহ", Icons.Filled.HealthAndSafety, Icons.Outlined.HealthAndSafety, "tab_ruqyah"),
    TASBEEH("তাসবীহ", Icons.Filled.TouchApp, Icons.Outlined.TouchApp, "tab_tasbeeh"),
    SETTINGS("সংরক্ষিত", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder, "tab_settings")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                IslamicDuaApp()
            }
        }
    }
}

@Composable
fun IslamicDuaApp(
    viewModel: DuaViewModel = viewModel()
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = NavigationTab.values()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val activeDuaDetail by viewModel.activeDuaDetail.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val counters by viewModel.counters.collectAsStateWithLifecycle()
    val displaySettings by viewModel.displaySettings.collectAsStateWithLifecycle()

    val bookmarkedIds = bookmarks.map { it.duaId }.toSet()
    val counterMap = counters.associate { it.duaId to it.currentCount }

    // Back handling: If search active or not on home tab, handle gracefully
    BackHandler(enabled = searchQuery.isNotEmpty() || selectedTabIndex != 0) {
        if (searchQuery.isNotEmpty()) {
            viewModel.setSearchQuery("")
        } else {
            selectedTabIndex = 0
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Header with search bar and "Developed By Ammar Khandoker" badge
            if (selectedTabIndex == 0 || selectedTabIndex == 1) {
                AppHeader(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = selectedTabIndex == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.titleBn,
                                tint = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                text = tab.titleBn,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp,
                                    color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = selectedTabIndex,
                label = "ScreenTransition"
            ) { screenIndex ->
                when (screenIndex) {
                    0 -> HomeScreen(viewModel = viewModel)
                    1 -> RuqyahScreen(viewModel = viewModel)
                    2 -> TasbeehScreen(viewModel = viewModel)
                    3 -> SettingsScreen(viewModel = viewModel)
                }
            }

            // Fullscreen / Modal Detail Bottom Sheet when a dua is tapped
            activeDuaDetail?.let { dua ->
                DuaDetailModal(
                    dua = dua,
                    isBookmarked = bookmarkedIds.contains(dua.id),
                    currentCount = counterMap[dua.id] ?: 0,
                    displaySettings = displaySettings,
                    onDismiss = { viewModel.closeDuaDetail() },
                    onBookmarkToggle = { viewModel.toggleBookmark(dua.id) },
                    onCountIncrement = { viewModel.incrementDuaCount(dua.id, dua.targetCount) },
                    onCountReset = { viewModel.resetDuaCount(dua.id) },
                    onFontSizeChange = { viewModel.updateArabicFontSize(it) }
                )
            }
        }
    }
}
