package com.example.data.model

enum class DuaCategory(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val nameAr: String,
    val iconName: String
) {
    ALL("all", "সকল দোয়া", "All Duas", "جميع الأدعية", "all"),
    MORNING_EVENING("morning_evening", "সকাল ও সন্ধ্যা", "Morning & Evening", "أذكار الصباح والمساء", "wb_sunny"),
    RUQYAH("ruqyah", "রুকইয়াহ শরীয়াহ", "Ruqyah Shariah", "الرقية الشرعية", "healing"),
    DAILY_LIFE("daily_life", "দৈনন্দিন দোয়া", "Daily Life", "أدعية يومية", "home"),
    PRAYER_WUDU("prayer_wudu", "নামাজ ও পবিত্রতা", "Prayer & Wudu", "الصلاة والوضوء", "mosque"),
    DISTRESS_FORGIVENESS("distress_forgiveness", "বিপদ ও ক্ষমা", "Distress & Forgiveness", "الكرب والاستغفার", "sentiment_dissatisfied"),
    RABBANA_QURAN("rabbana_quran", "কুরআনি রব্বানা", "Quranic Rabbana", "ربنا في القرآن", "menu_book"),
    SHIFA_ILLNESS("shifa_illness", "রোগ ও শিফা", "Illness & Shifa", "المرض والشفاء", "local_hospital"),
    PROTECTION("protection", "নিরাপত্তা ও হেফাজত", "Protection & Safety", "الحفظ والوقاية", "shield"),
    TRAVEL_FOOD("travel_food", "সফর ও আহার", "Travel & Sustenance", "السفر والرزق", "flight"),
    SPECIAL_OCCASIONS("special_occasions", "রমজান ও বিশেষ", "Special Occasions", "المناسبات الخاصة", "star")
}

data class DuaItem(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val titleAr: String,
    val category: DuaCategory,
    val arabicText: String,
    val banglaPronunciation: String,
    val banglaMeaning: String,
    val englishTranslation: String,
    val englishTransliteration: String,
    val reference: String,
    val virtueBn: String,
    val virtueEn: String,
    val targetCount: Int = 1,
    val isRuqyah: Boolean = false,
    val ruqyahType: String? = null // e.g. "Evil Eye", "Black Magic", "Physical Pain", "Anxiety & Waswas"
)

data class RuqyahGuideStep(
    val stepNumber: Int,
    val titleBn: String,
    val titleEn: String,
    val descriptionBn: String,
    val descriptionEn: String,
    val recommendedAyat: String
)
