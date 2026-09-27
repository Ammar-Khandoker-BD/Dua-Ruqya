package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BookmarkEntity
import com.example.data.local.DuaCounterEntity
import com.example.data.local.TasbeehEntity
import com.example.data.model.DuaCategory
import com.example.data.model.DuaItem
import com.example.data.model.RuqyahGuideStep
import com.example.data.repository.DuaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AppDisplaySettings(
    val arabicFontSize: Float = 26f,
    val showBanglaPronunciation: Boolean = true,
    val showEnglishTranslation: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true
)

data class TasbeehPreset(
    val nameBn: String,
    val nameEn: String,
    val arabicText: String,
    val defaultGoal: Int
)

class DuaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = DuaRepository(db)

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(DuaCategory.ALL)
    val selectedCategory: StateFlow<DuaCategory> = _selectedCategory.asStateFlow()

    private val _selectedRuqyahType = MutableStateFlow<String?>(null)
    val selectedRuqyahType: StateFlow<String?> = _selectedRuqyahType.asStateFlow()

    private val _activeDuaDetail = MutableStateFlow<DuaItem?>(null)
    val activeDuaDetail: StateFlow<DuaItem?> = _activeDuaDetail.asStateFlow()

    private val _displaySettings = MutableStateFlow(AppDisplaySettings())
    val displaySettings: StateFlow<AppDisplaySettings> = _displaySettings.asStateFlow()

    // Independent Digital Tasbeeh State
    private val _tasbeehCount = MutableStateFlow(0)
    val tasbeehCount: StateFlow<Int> = _tasbeehCount.asStateFlow()

    private val _tasbeehTarget = MutableStateFlow(33)
    val tasbeehTarget: StateFlow<Int> = _tasbeehTarget.asStateFlow()

    private val _tasbeehRounds = MutableStateFlow(0)
    val tasbeehRounds: StateFlow<Int> = _tasbeehRounds.asStateFlow()

    val tasbeehPresets = listOf(
        TasbeehPreset("সুবহানাল্লাহ", "SubhanAllah", "سُبْحَانَ اللَّهِ", 33),
        TasbeehPreset("আলহামদুলিল্লাহ", "Alhamdulillah", "الْحَمْدُ لِلَّهِ", 33),
        TasbeehPreset("আল্লাহু আকবার", "Allahu Akbar", "اللَّهُ أَكْبَرُ", 34),
        TasbeehPreset("লা ইলাহা ইল্লাল্লাহ", "La ilaha illallah", "لَا إِلَٰهَ إِلَّا اللَّهُ", 100),
        TasbeehPreset("আস্তাগফিরুল্লাহ", "Astaghfirullah", "أَسْتَغْفِرُ اللَّهَ", 100),
        TasbeehPreset("দরূদ শরীফ", "Durood Sharif", "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ", 100),
        TasbeehPreset("লা হাওলা ওয়ালা কুওয়্যাতা", "La Hawla", "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ", 100)
    )

    private val _selectedTasbeehPreset = MutableStateFlow(tasbeehPresets[0])
    val selectedTasbeehPreset: StateFlow<TasbeehPreset> = _selectedTasbeehPreset.asStateFlow()

    // Room DB Observations
    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val counters: StateFlow<List<DuaCounterEntity>> = repository.allCounters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined filtered Dua list
    val filteredDuas: StateFlow<List<DuaItem>> = combine(
        _searchQuery,
        _selectedCategory,
        bookmarks
    ) { query, category, bookmarkList ->
        val bookmarkedIds = bookmarkList.map { it.duaId }.toSet()
        val allDuas = repository.getAllDuas()

        allDuas.filter { dua ->
            val matchesCategory = when (category) {
                DuaCategory.ALL -> true
                else -> dua.category == category
            }

            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                val q = query.trim().lowercase()
                dua.titleBn.lowercase().contains(q) ||
                dua.titleEn.lowercase().contains(q) ||
                dua.arabicText.contains(q) ||
                dua.banglaMeaning.lowercase().contains(q) ||
                dua.englishTranslation.lowercase().contains(q) ||
                dua.reference.lowercase().contains(q)
            }

            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.getAllDuas())

    // Filtered Ruqyah Duas
    val filteredRuqyahDuas: StateFlow<List<DuaItem>> = combine(
        _searchQuery,
        _selectedRuqyahType
    ) { query, ruqyahType ->
        val allRuqyah = repository.getRuqyahDuas()
        allRuqyah.filter { dua ->
            val matchesType = ruqyahType == null || dua.ruqyahType == ruqyahType
            val matchesQuery = if (query.isBlank()) true else {
                val q = query.trim().lowercase()
                dua.titleBn.lowercase().contains(q) ||
                dua.titleEn.lowercase().contains(q) ||
                dua.arabicText.contains(q) ||
                dua.banglaMeaning.lowercase().contains(q) ||
                dua.englishTranslation.lowercase().contains(q)
            }
            matchesType && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.getRuqyahDuas())

    // Bookmarked Duas list
    val bookmarkedDuas: StateFlow<List<DuaItem>> = combine(
        bookmarks,
        _searchQuery
    ) { bookmarkList, query ->
        val bookmarkedIds = bookmarkList.map { it.duaId }.toSet()
        val allDuas = repository.getAllDuas().filter { bookmarkedIds.contains(it.id) }
        if (query.isBlank()) allDuas else {
            val q = query.trim().lowercase()
            allDuas.filter {
                it.titleBn.lowercase().contains(q) ||
                it.titleEn.lowercase().contains(q) ||
                it.banglaMeaning.lowercase().contains(q)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getRuqyahSteps(): List<RuqyahGuideStep> = repository.getRuqyahSteps()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: DuaCategory) {
        _selectedCategory.value = category
    }

    fun selectRuqyahType(type: String?) {
        _selectedRuqyahType.value = type
    }

    fun openDuaDetail(dua: DuaItem) {
        _activeDuaDetail.value = dua
    }

    fun closeDuaDetail() {
        _activeDuaDetail.value = null
    }

    fun toggleBookmark(duaId: String) {
        viewModelScope.launch {
            val isCurrentBookmarked = bookmarks.value.any { it.duaId == duaId }
            repository.toggleBookmark(duaId, isCurrentBookmarked)
            triggerHaptic(50)
        }
    }

    fun incrementDuaCount(duaId: String, targetCount: Int) {
        viewModelScope.launch {
            val currentCounter = counters.value.find { it.duaId == duaId }
            val count = (currentCounter?.currentCount ?: 0) + 1
            repository.updateDuaCount(duaId, count, targetCount)
            if (count >= targetCount) {
                triggerHaptic(120) // Longer celebration buzz
            } else {
                triggerHaptic(40)
            }
        }
    }

    fun resetDuaCount(duaId: String) {
        viewModelScope.launch {
            repository.resetDuaCount(duaId)
            triggerHaptic(60)
        }
    }

    // Digital Tasbeeh Functions
    fun incrementTasbeeh() {
        val next = _tasbeehCount.value + 1
        val target = _tasbeehTarget.value
        if (next >= target && target > 0) {
            _tasbeehCount.value = 0
            _tasbeehRounds.value += 1
            triggerHaptic(150)
        } else {
            _tasbeehCount.value = next
            triggerHaptic(40)
        }
    }

    fun resetTasbeeh() {
        _tasbeehCount.value = 0
        _tasbeehRounds.value = 0
        triggerHaptic(70)
    }

    fun setTasbeehPreset(preset: TasbeehPreset) {
        _selectedTasbeehPreset.value = preset
        _tasbeehCount.value = 0
        _tasbeehTarget.value = preset.defaultGoal
        triggerHaptic(50)
    }

    fun setTasbeehTarget(target: Int) {
        _tasbeehTarget.value = target
    }

    // Settings
    fun updateArabicFontSize(size: Float) {
        _displaySettings.value = _displaySettings.value.copy(arabicFontSize = size)
    }

    fun toggleShowBanglaPronunciation() {
        val current = _displaySettings.value.showBanglaPronunciation
        _displaySettings.value = _displaySettings.value.copy(showBanglaPronunciation = !current)
    }

    fun toggleShowEnglishTranslation() {
        val current = _displaySettings.value.showEnglishTranslation
        _displaySettings.value = _displaySettings.value.copy(showEnglishTranslation = !current)
    }

    fun toggleHapticFeedback() {
        val current = _displaySettings.value.hapticFeedbackEnabled
        _displaySettings.value = _displaySettings.value.copy(hapticFeedbackEnabled = !current)
    }

    private fun triggerHaptic(durationMs: Long) {
        if (!_displaySettings.value.hapticFeedbackEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {
            // Gracefully ignore if vibrator is unavailable
        }
    }
}
