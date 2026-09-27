package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.BookmarkEntity
import com.example.data.local.DuaCounterEntity
import com.example.data.local.TasbeehEntity
import com.example.data.model.DuaCategory
import com.example.data.model.DuaItem
import com.example.data.model.RuqyahGuideStep
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DuaRepository(private val db: AppDatabase) {

    val allBookmarks: Flow<List<BookmarkEntity>> = db.bookmarkDao().getAllBookmarks()
    val allCounters: Flow<List<DuaCounterEntity>> = db.duaCounterDao().getAllCounters()
    val allTasbeeh: Flow<List<TasbeehEntity>> = db.tasbeehDao().getAllTasbeeh()

    fun isBookmarked(duaId: String): Flow<Boolean> = db.bookmarkDao().isBookmarked(duaId)

    suspend fun toggleBookmark(duaId: String, currentStatus: Boolean) {
        if (currentStatus) {
            db.bookmarkDao().deleteBookmark(duaId)
        } else {
            db.bookmarkDao().insertBookmark(BookmarkEntity(duaId = duaId))
        }
    }

    suspend fun updateDuaCount(duaId: String, newCount: Int, targetCount: Int) {
        val completed = if (newCount >= targetCount && targetCount > 0) 1 else 0
        db.duaCounterDao().saveCounter(
            DuaCounterEntity(
                duaId = duaId,
                currentCount = newCount,
                targetCount = targetCount,
                completedTimes = completed
            )
        )
    }

    suspend fun resetDuaCount(duaId: String) {
        db.duaCounterDao().resetCounter(duaId)
    }

    suspend fun saveTasbeehCount(id: Long, count: Int, rounds: Int) {
        db.tasbeehDao().updateCount(id, count, rounds, System.currentTimeMillis())
    }

    suspend fun addTasbeeh(tasbeeh: TasbeehEntity): Long {
        return db.tasbeehDao().insertOrUpdateTasbeeh(tasbeeh)
    }

    suspend fun deleteTasbeeh(id: Long) {
        db.tasbeehDao().deleteTasbeeh(id)
    }

    fun getAllDuas(): List<DuaItem> = DuaDataset.duas

    fun getRuqyahDuas(): List<DuaItem> = DuaDataset.duas.filter { it.isRuqyah || it.category == DuaCategory.RUQYAH }

    fun getDuasByCategory(category: DuaCategory): List<DuaItem> {
        return if (category == DuaCategory.ALL) {
            DuaDataset.duas
        } else {
            DuaDataset.duas.filter { it.category == category }
        }
    }

    fun getRuqyahSteps(): List<RuqyahGuideStep> = DuaDataset.ruqyahSteps
}

object DuaDataset {
    val ruqyahSteps = listOf(
        RuqyahGuideStep(
            stepNumber = 1,
            titleBn = "১. নিয়্যত ও পবিত্রতা অর্জন",
            titleEn = "1. Pure Intention & Taharah (Ablution)",
            descriptionBn = "অজু করে পবিত্র হয়ে বসুন। দৃঢ় বিশ্বাস রাখুন যে আরোগ্য দানকারী একমাত্র আল্লাহ তা'আলা। রুকইয়াহ হলো আল্লাহর কালাম ও সুন্নাহ সম্মত দোয়ার মাধ্যমে ওসিলা গ্রহণ।",
            descriptionEn = "Perform complete ablution (Wudu). Firmly believe that true cure (Shifa) comes solely from Allah SWT. Ruqyah is seeking help through Allah's words.",
            recommendedAyat = "নিয়্যাত: হে আল্লাহ, আপনার কালামের বরকতে আমাকে বা রোগীকে সকল অনিষ্ট ও রোগ থেকে শেফা দান করুন।"
        ),
        RuqyahGuideStep(
            stepNumber = 2,
            titleBn = "২. ইস্তিগফার ও দরূদ শরীফ পাঠ",
            titleEn = "2. Istighfar & Sending Blessings (Durood)",
            descriptionBn = "শুরুতে অন্তরের সাথে আস্তাগফিরুল্লাহ পাঠ করে তওবা করুন এবং রাসুলুল্লাহ (সা.) এর উপর ৩ বার দরূদে ইব্রাহিম পাঠ করুন।",
            descriptionEn = "Repent with sincere Istighfar (Astaghfirullah) and send blessings upon Prophet Muhammad (PBUH) by reciting Durood Ibrahim 3 times.",
            recommendedAyat = "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ (৩ বা ৭ বার)"
        ),
        RuqyahGuideStep(
            stepNumber = 3,
            titleBn = "৩. সূরা ফাতিহা ও আয়াতুল কুরসী তিলাওয়াত",
            titleEn = "3. Reciting Surah Al-Fatihah & Ayat al-Kursi",
            descriptionBn = "সূরা ফাতিহা (উম্মুল কুরআন ও আশ-শিফা) ৭ বার পাঠ করুন। এরপর অত্যন্ত একাগ্রতার সাথে আয়াতুল কুরসী পাঠ করুন।",
            descriptionEn = "Recite Surah Al-Fatihah 7 times with focused intention. Then recite Ayat al-Kursi with deep devotion.",
            recommendedAyat = "সূরা আল-ফাতিহা (৭ বার) ও আয়াতুল কুরসী (সূরা বাকারা: ২৫৫)"
        ),
        RuqyahGuideStep(
            stepNumber = 4,
            titleBn = "৪. তিন কুল (ইখলাস, ফালাক্ব, নাস) ও ফুঁ দেওয়া",
            titleEn = "4. Reciting the 3 Quls & Blowing with saliva mist",
            descriptionBn = "উভয় হাত একত্রিত করে সূরা ইখলাস, সূরা ফালাক্ব ও সূরা নাস পাঠ করে হালকা থুতুমিশ্রিত ফুঁ দিন। তারপর মাথা ও মুখ থেকে শুরু করে সাধ্যমতো সারা শরীরে হাত বুলিয়ে দিন। এভাবে ৩ বার করুন।",
            descriptionEn = "Cup both hands together, recite Surah Al-Ikhlas, Al-Falaq, and An-Nas, blow gently into your hands, and wipe over your head, face, and front of the body. Repeat 3 times.",
            recommendedAyat = "সুন্নাহ অনুযায়ী ঘুমানোর আগে ও ব্যথার স্থানে এই আমল অত্যন্ত ফলপ্রসূ।"
        ),
        RuqyahGuideStep(
            stepNumber = 5,
            titleBn = "৫. ব্যথার স্থানে হাত রেখে সুন্নতি দোয়া",
            titleEn = "5. Hand on Afflicted Area with Sunnah Duas",
            descriptionBn = "যে স্থানে ব্যথা বা সমস্যা, সেখানে ডান হাত রাখুন এবং ৩ বার 'বিসমিল্লাহ' বলুন। এরপর ৭ বার বলুন: 'আউযু বি'ইযযাতিল্লাহি ওয়া কুদরাতিহী মিন শাররি মা আজিদু ওয়া উহাযিরু'।",
            descriptionEn = "Place your right hand on the painful area, say 'Bismillah' 3 times, then say 7 times: 'A'udhu bi'izzatillahi wa qudratihi min sharri ma ajidu wa uhadhiru'.",
            recommendedAyat = "بِسْمِ اللَّهِ (৩ বার) + أَعُوذُ بِعِزَّةِ اللَّهِ وَقُدْرَتِهِ مِنْ شَرِّ مَا أَجِدُ وَأُحَاذِرُ (৭ বার)"
        ),
        RuqyahGuideStep(
            stepNumber = 6,
            titleBn = "৬. পানিতে ফুঁ দিয়ে পান করা ও গোসল (রুকইয়াহ ওয়াটার)",
            titleEn = "6. Ruqyah Water: Blowing into Water for Drinking & Bathing",
            descriptionBn = "একটি পাত্রে পরিষ্কার পানি (সম্ভব হলে জমজম পানি) নিয়ে সূরা ফাতিহা, আয়াতুল কুরসী, বাকারা শেষ ২ আয়াত এবং তিন কুল পাঠ করে পানিতে ফুঁ দিন। এই পানি পান করুন এবং আক্রান্ত স্থানে ছিটিয়ে দিন।",
            descriptionEn = "Recite Surah Al-Fatihah, Ayat al-Kursi, last 2 verses of Al-Baqarah, and the 3 Quls close to clean drinking water, blow into it, drink it throughout the day, and wash with it.",
            recommendedAyat = "বিহিত সুন্নাহ ও সালাফদের অনুসৃত কার্যকর রুকইয়াহ পদ্ধতি।"
        )
    )

    val duas = listOf(
        // ================= MORNING & EVENING =================
        DuaItem(
            id = "me_ayatul_kursi",
            titleBn = "আয়াতুল কুরসী (সর্বশ্রেষ্ঠ আয়াত)",
            titleEn = "Ayat al-Kursi (The Greatest Verse)",
            titleAr = "آية الكرسي",
            category = DuaCategory.MORNING_EVENING,
            arabicText = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
            banglaPronunciation = "আল্লাহু লা ইলাহা ইল্লা হুওয়াল হাইয়্যুল ক্বাইয়্যূম। লা তা'খুযুহু সিনাতুঁও ওয়ালা নাওম। লাহূ মা ফিস সামাওয়াতি ওয়ামা ফিল আরদ্ব। মান যাল্লাযী ইয়্যাশফা'উ 'ইনদাহূ ইল্লা বিইযনিহ। ইয়া'লামু মা বাইনা আইদীহিম ওয়ামা খালফাহুম, ওয়ালা ইউহীতূনা বিশাইইম মিন 'ইলমিহী ইল্লা বিমা শা-আ। ওয়াসি'আ কুরসিইয়্যুহুস সামাওয়াতি ওয়াল আরদ্ব, ওয়ালা ইয়াউদুহু হিফযুহুমা, ওয়া হুওয়াল 'আলিয়্যুল 'আজীম।",
            banglaMeaning = "আল্লাহ, তিনি ছাড়া কোনো সত্য ইলাহ নেই। তিনি চিরঞ্জীব, সর্বসত্তার ধারক। তন্দ্রা ও নিদ্রা তাঁকে স্পর্শ করে না। আসমানসমূহে যা রয়েছে ও যমীনে যা রয়েছে সবই তাঁর। কে সে, যে তাঁর অনুমতি ছাড়া তাঁর নিকট সুপারিশ করবে? তাদের সামনে ও পেছনে যা কিছু আছে তা তিনি জানেন। তাঁর জ্ঞানের কোনো কিছু তারা পরিবেষ্টন করতে পারে না, কেবল যা তিনি ইচ্ছা করেন তা ছাড়া। তাঁর কুরসী আসমান ও যমীন পরিবেষ্টন করে আছে। এ দুটোর রক্ষণাবেক্ষণ তাঁকে ক্লান্ত করে না। আর তিনি সর্বোচ্চ, মহান।",
            englishTranslation = "Allah! There is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. Who is it that could intercede with Him except by His permission? He knows what is before them and what will be after them, and they encompass not a thing of His knowledge except for what He wills. His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great.",
            englishTransliteration = "Allahu la ilaha illa Huwa, Al-Hayyul-Qayyum. La ta'khudhuhu sinatuw-wa la nawm. Lahu ma fis-samawati wa ma fil-ard...",
            reference = "সূরা আল-বাক্বারা: ২৫৫; সহীহ ইবনে হিব্বান: ৬৫৪",
            virtueBn = "যে ব্যক্তি সকালে ও সন্ধ্যায় এটি পাঠ করবে, সে সন্ধ্যা/সকাল পর্যন্ত সকল শয়তান ও ক্ষতি থেকে সুরক্ষিত থাকবে। প্রতি ফরজ নামাজের পর পাঠ করলে জান্নাতে প্রবেশে মৃত্যু ছাড়া আর কোনো বাধা থাকে না।",
            virtueEn = "Reciting it in morning and evening grants divine protection from jinns and harm until night/morning.",
            targetCount = 1,
            isRuqyah = true,
            ruqyahType = "General Protection"
        ),
        DuaItem(
            id = "me_sayyidul_istighfar",
            titleBn = "সাইয়েদুল ইস্তিগফার (শ্রেষ্ঠ ক্ষমা প্রার্থনার দোয়া)",
            titleEn = "Sayyidul Istighfar (The Master Supplication for Forgiveness)",
            titleAr = "سيد الاستغفار",
            category = DuaCategory.MORNING_EVENING,
            arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
            banglaPronunciation = "আল্লাহুম্মা আনতা রব্বী, লা ইলাহা ইল্লা আনতা, খালাক্বতানী ওয়া আনা 'আব্দুকা, ওয়া আনা 'আলা 'আহদিকা ওয়া ওয়া'দিকা মাস্তাত্বা'তু। আ'উযু বিকা মিন শাররি মা ছানা'তু, আবূউ লাকা বিনি'মাতিকা 'আলাইয়্যা, ওয়া আবূউ বিযাম্বী, ফাগফির লী, ফাইন্নাহূ লা ইয়াগফিরুয যুনূবা ইল্লা আনতা।",
            banglaMeaning = "হে আল্লাহ! আপনি আমার রব, আপনি ছাড়া কোনো সত্য উপাস্য নেই। আপনি আমাকে সৃষ্টি করেছেন এবং আমি আপনার বান্দা। আমি সাধ্যমতো আপনার সাথে কৃত অঙ্গীকার ও প্রতিশ্রুতির ওপর কায়েম আছি। আমি আমার কৃতকর্মের অনিষ্ট থেকে আপনার আশ্রয় চাই। আমার ওপর আপনার নিয়ামতের স্বীকৃতি দিচ্ছি এবং আমার অপরাধও স্বীকার করছি। অতএব আমাকে ক্ষমা করে দিন; নিশ্চয় আপনি ছাড়া গুনাহ মাফ করার কেউ নেই।",
            englishTranslation = "O Allah, You are my Lord; none has the right to be worshipped but You. You created me and I am Your servant, and I abide by Your covenant and promise as best I can. I seek refuge in You from the evil of what I have done. I acknowledge Your favor upon me, and I acknowledge my sin, so forgive me, for verily none can forgive sins except You.",
            englishTransliteration = "Allahumma Anta Rabbi, la ilaha illa Anta, khalaqtani wa ana 'abduka, wa ana 'ala 'ahdika wa wa'dika mastata'tu...",
            reference = "সহীহ বুখারী: ৬৩০৬",
            virtueBn = "রাসুলুল্লাহ (সা.) বলেছেন: যে ব্যক্তি দিনে দৃঢ় বিশ্বাসের সাথে এই দোয়া পড়বে এবং সন্ধ্যার পূর্বে মারা যাবে, সে জান্নাতবাসী হবে। আর যে রাতে পড়বে এবং সকালের পূর্বে মারা যাবে, সেও জান্নাতবাসী হবে।",
            virtueEn = "Whoever recites this during the day with firm faith and dies before evening will be of the people of Paradise.",
            targetCount = 1,
            isRuqyah = false
        ),
        DuaItem(
            id = "me_bismillahillazi",
            titleBn = "সকল ক্ষতি ও বিপদ থেকে সুরক্ষার দোয়া (৩ বার)",
            titleEn = "Protection from All Harm (3 Times)",
            titleAr = "بسم الله الذي لا يضر مع اسمه شيء",
            category = DuaCategory.MORNING_EVENING,
            arabicText = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
            banglaPronunciation = "বিসমিল্লাহিল্লাযী লা ইয়াদুররু মা'আসমিহী শাইউন ফিল আরদ্বি ওয়ালা ফিস সামা-ই, ওয়া হুওয়াস সামী'উল 'আলীম।",
            banglaMeaning = "আল্লাহর নামে, যাঁর নামের বরকতে আসমান ও যমীনের কোনো কিছুই কোনো ক্ষতি করতে পারে না। আর তিনি সর্বশ্রোতা, সর্বজ্ঞানী।",
            englishTranslation = "In the name of Allah, with whose name nothing on earth or in heaven can cause harm, and He is the All-Hearing, the All-Knowing.",
            englishTransliteration = "Bismillahil-ladhi la yadurru ma'as-mihi shay'un fil-ardi wa la fis-sama'i wa Huwas-Sami'ul-'Alim.",
            reference = "সুনান আবু দাউদ: ৫০৮৮; জামে আত-তিরমিযী: ৩৩৪৭ (হাসান সহীহ)",
            virtueBn = "সকাল ও সন্ধ্যায় ৩ বার পাঠ করলে আসমান ও যমীনের কোনো সৃষ্টি, বিষাক্ত প্রাণী বা আকস্মিক বালা-মুসিবত কোনো ক্ষতি করতে পারবে না।",
            virtueEn = "Whoever recites it 3 times in the morning and evening, nothing will harm them.",
            targetCount = 3,
            isRuqyah = true,
            ruqyahType = "General Protection"
        ),
        DuaItem(
            id = "me_raditu_billahi",
            titleBn = "আল্লাহর সন্তুষ্টি ও জান্নাত ওয়াজিব হওয়ার দোয়া (৩ বার)",
            titleEn = "Pleased with Allah as Lord (3 Times)",
            titleAr = "رضيت بالله ربا وبالإسلام دينا",
            category = DuaCategory.MORNING_EVENING,
            arabicText = "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا",
            banglaPronunciation = "রাদ্বীতু বিল্লাহি রববাওঁ, ওয়া বিল ইসলামি দ্বীনাওঁ, ওয়া বিমুহাম্মাদিন সাল্লাল্লাহু 'আলাইহি ওয়া সাল্লামা নাবিয়্যা।",
            banglaMeaning = "আমি আল্লাহকে রব হিসেবে, ইসলামকে দ্বীন হিসেবে এবং মুহাম্মাদ (সাল্লাল্লাহু আলাইহি ওয়া সাল্লাম)-কে নবী হিসেবে পেয়ে সন্তুষ্ট হলাম।",
            englishTranslation = "I am pleased with Allah as my Lord, with Islam as my religion, and with Muhammad (peace and blessings of Allah be upon him) as my Prophet.",
            englishTransliteration = "Raditu billahi Rabba, wa bil-Islami deena, wa bi-Muhammadin sallallahu 'alayhi wa sallama Nabiyya.",
            reference = "মুসনাদে আহমাদ: ১৮৯৬৭; সুনান আবু দাউদ: ৫০৭২",
            virtueBn = "যে ব্যক্তি সকাল ও সন্ধ্যায় ৩ বার এটি বলবে, কিয়ামতের দিন আল্লাহ তা'আলার দায়িত্ব হয়ে যায় তাকে সন্তুষ্ট করা।",
            virtueEn = "Allah will definitely please whoever recites this 3 times in the morning and evening on the Day of Judgment.",
            targetCount = 3,
            isRuqyah = false
        ),
        DuaItem(
            id = "me_subhanallahi_wa_bihamdihi",
            titleBn = "সাগর পরিমাণ গুনাহ মাফের তাসবীহ (১০০ বার)",
            titleEn = "Praise that Erases Sins (100 Times)",
            titleAr = "سبحان الله وبحمده",
            category = DuaCategory.MORNING_EVENING,
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
            banglaPronunciation = "সুবহানাল্লাহি ওয়া বিহামদিহী।",
            banglaMeaning = "আমি আল্লাহর পবিত্রতা ঘোষণা করছি এবং তাঁর প্রশংসা করছি।",
            englishTranslation = "Glory be to Allah and all praise is due to Him.",
            englishTransliteration = "Subhanallahi wa bihamdihi.",
            reference = "সহীহ বুখারী: ৬৪০৫; সহীহ মুসলিম: ২৬৯১",
            virtueBn = "যে ব্যক্তি দিনে ১০০ বার 'সুবহানাল্লাহি ওয়া বিহামদিহী' বলবে, তার পাপরাশি ক্ষমা করে দেওয়া হবে যদিও তা সমুদ্রের ফেনার মতো বেশি হয়।",
            virtueEn = "Whoever recites this 100 times a day, their sins will be forgiven even if they were like the foam of the sea.",
            targetCount = 100,
            isRuqyah = false
        ),
        DuaItem(
            id = "me_hasbiyallahu",
            titleBn = "সকল পেরেশানি দূর হওয়ার দোয়া (৭ বার)",
            titleEn = "Sufficient for All Worries (7 Times)",
            titleAr = "حسبي الله لا إله إلا هو",
            category = DuaCategory.MORNING_EVENING,
            arabicText = "حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
            banglaPronunciation = "হাসবিয়াল্লাহু লা ইলাহা ইল্লা হুওয়া, 'আলাইহি তাওয়াক্কালতু, ওয়া হুওয়া রব্বুল 'আরশিল 'আজীম।",
            banglaMeaning = "আল্লাহই আমার জন্য যথেষ্ট, তিনি ছাড়া কোনো সত্য উপাস্য নেই। তাঁর ওপরই আমি ভরসা করেছি এবং তিনি মহান আরশের অধিপতি।",
            englishTranslation = "Allah is sufficient for me; there is no deity except Him. Upon Him I have relied, and He is the Lord of the Great Throne.",
            englishTransliteration = "Hasbiyallahu la ilaha illa Huwa, 'alayhi tawakkaltu wa Huwa Rabbul-'Arshil-'Azeem.",
            reference = "সুনান আবু দাউদ: ৫০৮১",
            virtueBn = "যে ব্যক্তি সকাল ও সন্ধ্যায় ৭ বার এটি পড়বে, দুনিয়া ও আখিরাতের সকল দুশ্চিন্তা ও সংকটের জন্য আল্লাহ তা'আলাই তার জন্য যথেষ্ট হয়ে যাবেন।",
            virtueEn = "Whoever recites this 7 times morning and evening, Allah will suffice them against whatever worries them.",
            targetCount = 7,
            isRuqyah = false
        ),

        // ================= RUQYAH SHARIAH =================
        DuaItem(
            id = "rq_fatihah",
            titleBn = "সূরা আল-ফাতিহা (আশ-শিফা ও প্রধান রুকইয়াহ)",
            titleEn = "Surah Al-Fatihah (The Ultimate Cure)",
            titleAr = "سورة الفاتحة للرقية",
            category = DuaCategory.RUQYAH,
            arabicText = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۝ الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ ۝ الرَّحْمَٰنِ الرَّحِيمِ ۝ مَالِكِ يَوْمِ الدِّينِ ۝ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ ۝ اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ ۝ صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ",
            banglaPronunciation = "বিসমিল্লাহির রাহমানির রাহীম। আলহামদু লিল্লাহি রব্বিল 'আলামীন। আর-রাহমানির রাহীম। মালিকি ইয়াওমিদ্দীন। ইয়্যাকা না'বুদু ওয়া ইয়্যাকা নাস্তা'ঈন। ইহদিনাস সিরাতাল মুস্তাক্বীম। সিরাতাল্লাযীনা আন'আমতা 'আলাইহিম, গাইরিল মাগদূবি 'আলাইহিম ওয়ালাদ্দাল্লীন।",
            banglaMeaning = "পরম করুণাময় অসীম দয়ালু আল্লাহর নামে শুরু। যাবতীয় প্রশংসা একমাত্র আল্লাহর যিনি সকল জাহানের পালনকর্তা। যিনি পরম করুণাময় ও দয়ালু। যিনি বিচার দিবসের মালিক। আমরা কেবল আপনারই ইবাদত করি এবং কেবল আপনারই সাহায্য চাই। আমাদের সরল ও সঠিক পথ প্রদর্শন করুন। তাদের পথ যাদেরকে আপনি নেয়ামত দান করেছেন, তাদের পথ নয় যারা ক্রোধের শিকার ও বিভ্রান্ত।",
            englishTranslation = "In the name of Allah, the Entirely Merciful, the Especially Merciful. [All] praise is [due] to Allah, Lord of the worlds. The Entirely Merciful, the Especially Merciful. Sovereign of the Day of Recompense. It is You we worship and You we ask for help. Guide us to the straight path. The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.",
            englishTransliteration = "Bismillahir-Rahmanir-Raheem. Alhamdu lillahi Rabbil-'alameen...",
            reference = "সহীহ বুখারী: ৫৭৩৬ (সাহাবী আবু সাঈদ খুদরী রা. এর রুকইয়াহ হাদিস)",
            virtueBn = "সূরা ফাতিহার আরেক নাম 'আশ-শিফা' (রোগমুক্তি)। সাহাবীগণ বিষাক্ত সাপে দংশন করা ব্যক্তিকে সূরা ফাতিহা পড়ে ফুঁ দিয়ে সম্পূর্ণ সুস্থ করেছিলেন এবং রাসুলুল্লাহ (সা.) তা অনুমোদন করেছেন।",
            virtueEn = "Known as Ash-Shifa (The Cure). The Prophet (PBUH) approved using it for curing illness, poison, and evil eye.",
            targetCount = 7,
            isRuqyah = true,
            ruqyahType = "General Shifa & Evil Eye"
        ),
        DuaItem(
            id = "rq_three_quls",
            titleBn = "তিন কুল: ইখলাস, ফালাক্ব ও নাস (সকল অনিষ্ট হতে রক্ষা)",
            titleEn = "The Three Quls: Ikhlas, Falaq & Nas",
            titleAr = "المعوذات (الإخلاص، الفلق، والناس)",
            category = DuaCategory.RUQYAH,
            arabicText = "قُلْ هُوَ اللَّهُ أَحَدٌ ۝ اللَّهُ الصَّمَدُ ۝ لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ ۝\nقُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۝ مِن شَرِّ مَا خَلَقَ ۝ وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ ۝ وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ ۝ وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ ۝\nقُلْ أَعُوذُ بِرَبِّ النَّاسِ ۝ مَلِكِ النَّاسِ ۝ إِلَٰهِ النَّاسِ ۝ مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ ۝ الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ ۝ مِنَ الْجِنَّةِ وَالنَّاسِ",
            banglaPronunciation = "কুল হুওয়াল্লাহু আহাদ... কুল আ'উযু বি রব্বিল ফালাক্ব... কুল আ'উযু বি রব্বিন নাস...",
            banglaMeaning = "বলুন: তিনিই আল্লাহ, এক-অদ্বিতীয়... বলুন: আমি আশ্রয় গ্রহণ করছি ভোরের রবের নিকট, তাঁর সৃষ্ট অনিষ্ট হতে এবং গিরায় ফুঁকদানকারী যাদুকরিদের অনিষ্ট হতে এবং হিংসুকের অনিষ্ট হতে যখন সে হিংসা করে... বলুন: আমি আশ্রয় চাচ্ছি মানুষের রবের কাছে...",
            englishTranslation = "Say: He is Allah, [who is] One... Say: I seek refuge in the Lord of daybreak from the evil of what He created, and from the evil of witchcraft and the envier when he envies... Say: I seek refuge in the Lord of mankind...",
            englishTransliteration = "Qul Huwallahu Ahad... Qul a'udhu bi Rabbil-falaq... Qul a'udhu bi Rabbin-nas...",
            reference = "সুনান আবু দাউদ: ৫০৮২; সুনান নাসাঈ: ৫৪২৮; সহীহ বুখারী: ৫০১৭",
            virtueBn = "রাসুলুল্লাহ (সা.) বলেছেন: সকাল ও সন্ধ্যায় এই তিন সূরা ৩ বার পাঠ করলে তা তোমার সকল বিপদের মোকাবেলায় যথেষ্ট হবে। জাদু, বদনজর ও শয়তানের ওয়াসওয়াসার বিরুদ্ধে এটি দুর্ভেদ্য ঢাল।",
            virtueEn = "Reciting the 3 Quls 3 times in morning and evening suffices against every harm, witchcraft, and evil eye.",
            targetCount = 3,
            isRuqyah = true,
            ruqyahType = "Black Magic & Evil Eye"
        ),
        DuaItem(
            id = "rq_pain_relief_body",
            titleBn = "শরীরের যেকোনো ব্যথার স্থানে হাত রেখে রুকইয়াহ",
            titleEn = "Ruqyah for Physical Pain & Sickness",
            titleAr = "رقية الألم في الجسد",
            category = DuaCategory.RUQYAH,
            arabicText = "بِسْمِ اللَّهِ (৩ বার)\nأَعُوذُ بِعِزَّةِ اللَّهِ وَقُدْرَتِهِ مِنْ شَرِّ مَا أَجِدُ وَأُحَاذِرُ (৭ বার)",
            banglaPronunciation = "বিসমিল্লাহ (৩ বার)।\nআ'উযু বি'ইযযাতিল্লাহি ওয়া ক্বুদরাতিহী মিন শাররি মা আজিদু ওয়া উহাযিরু (৭ বার)।",
            banglaMeaning = "আল্লাহর নামে (৩ বার)। আমি আল্লাহর ইজ্জত ও কুদরতের আশ্রয় প্রার্থনা করছি, যা আমি অনুভব করছি এবং যে ক্ষতির আশঙ্কা করছি তার অনিষ্ট হতে (৭ বার)।",
            englishTranslation = "In the name of Allah (3 times). I seek refuge in the might and power of Allah from the evil of what I feel and what I fear (7 times).",
            englishTransliteration = "Bismillah (3x). A'udhu bi'izzatillahi wa qudratihi min sharri ma ajidu wa uhadhiru (7x).",
            reference = "সহীহ মুসলিম: ২২০২ (উসমান ইবনে আবিল আস রা. এর হাদিস)",
            virtueBn = "উসমান ইবনে আবিল আস (রা.) বলেন, আমি ইসলাম গ্রহণের পর থেকে শরীরে প্রচণ্ড ব্যথায় ভুগছিলাম। রাসুলুল্লাহ (সা.) আমাকে এই দোয়াটি শেখালেন। আমি তা আমল করার সাথে সাথেই আল্লাহ আমার ব্যথা সম্পূর্ণ দূর করে দিলেন।",
            virtueEn = "Place your right hand on the pain, say Bismillah 3 times, then recite this supplication 7 times for instant relief.",
            targetCount = 7,
            isRuqyah = true,
            ruqyahType = "Physical Pain"
        ),
        DuaItem(
            id = "rq_jibril_cure",
            titleBn = "জিবরীল (আ.) কর্তৃক রাসুল (সা.)-কে করা রুকইয়াহ",
            titleEn = "Ruqyah of Angel Jibril (AS) for the Prophet",
            titleAr = "رقية جبريل عليه السلام",
            category = DuaCategory.RUQYAH,
            arabicText = "بِسْمِ اللَّهِ أَرْقِيكَ، مِنْ كُلِّ شَيْءٍ يُؤْذِيكَ، مِنْ شَرِّ كُلِّ نَفْسٍ أَوْ عَيْنِ حَاسِدٍ، اللَّهُ يَشْفِيكَ، بِسْمِ اللَّهِ أَرْقِيكَ",
            banglaPronunciation = "বিসমিল্লাহি আরক্বীকা, মিন কুল্লি শাইয়িন ইউ'যীকা, মিন শাররি কুল্লি নাফসিন আও 'আইনি হাসিদিন, আল্লাহু ইয়্যাশফীকা, বিসমিল্লাহি আরক্বীকা।",
            banglaMeaning = "আল্লাহর নামে আমি আপনাকে রুকইয়াহ করছি, এমন প্রতিটি বস্তু থেকে যা আপনাকে কষ্ট দেয় এবং প্রতিটি প্রাণের অনিষ্ট অথবা হিংসুকের বদনজর থেকে। আল্লাহ আপনাকে আরোগ্য দান করুন। আল্লাহর নামে আমি আপনাকে রুকইয়াহ করছি।",
            englishTranslation = "In the name of Allah I perform Ruqyah for you, from everything that harms you, from the evil of every soul or envious eye. May Allah cure you. In the name of Allah I perform Ruqyah for you.",
            englishTransliteration = "Bismillahi arqeek, min kulli shay'in yu'dheek, min sharri kulli nafsin aw 'ayni hasid, Allahu yashfeek, Bismillahi arqeek.",
            reference = "সহীহ মুসলিম: ২১৮৬",
            virtueBn = "যখন রাসুলুল্লাহ (সা.) অসুস্থ হয়েছিলেন, তখন জিবরীল (আ.) এসে এই দোয়ার মাধ্যমে তাঁকে রুকইয়াহ করেছিলেন। এটি বদনজর, অনিষ্ট এবং সকল অসুস্থতায় অত্যন্ত কার্যকর। নিজের জন্য পাঠ করার সময় 'আরক্বীকা' এর স্থলে 'আরক্বী নাফসী' বলতে পারেন।",
            virtueEn = "Angel Jibril recited this Ruqyah upon the Messenger of Allah (PBUH) when he fell ill.",
            targetCount = 3,
            isRuqyah = true,
            ruqyahType = "Evil Eye & Sickness"
        ),
        DuaItem(
            id = "rq_baqarah_last_two",
            titleBn = "সূরা বাকারার শেষ দুই আয়াত (রাতের জন্য যথেষ্ট ঢাল)",
            titleEn = "Last Two Verses of Surah Al-Baqarah",
            titleAr = "خواتيم سورة البقرة",
            category = DuaCategory.RUQYAH,
            arabicText = "آمَنَ الرَّسُولُ بِمَا أُنزِلَ إِلَيْهِ مِن رَّبِّهِ وَالْمُؤْمِنُونَ ۚ كُلٌّ آمَنَ بِاللَّهِ وَمَلَائِكَتِهِ وَكُتُبِهِ وَرُسُلِهِ لَا نُفَرِّقُ بَيْنَ أَحَدٍ مِّن رُّسُلِهِ ۚ وَقَالُوا سَمِعْنَا وَأَطَعْنَا ۖ غُفْرَانَكَ رَبَّنَا وَإِلَيْكَ الْمَصِيرُ ۝ لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا ۚ لَهَا مَا كَسَبَتْ وَعَلَيْهَا مَا اكْتَسَبَتْ ۗ رَبَّنَا لَا تُؤَاخِذْنَا إِن نَّسِينَا أَوْ أَخْطَأْنَا ۚ رَبَّنَا وَلَا تَحْمِلْ عَلَيْنَا إِصْرًا كَمَا حَمَلْتَهُ عَلَى الَّذِينَ مِن قَبْلِنَا ۚ رَبَّنَا وَلَا تُحَمِّلْنَا مَا لَا طَاقَةَ لَنَا بِهِ ۖ وَاعْفُ عَنَّا وَاغْفِرْ لَنَا وَارْحَمْنَا ۚ أَنتَ مَوْلَانَا فَانصُرْنَا عَلَى الْقَوْمِ الْكَافِرِينَ",
            banglaPronunciation = "আ-মানার রাসূলু বিমা উনযিলা ইলাইহি মির রব্বিহী ওয়াল মু'মিনূন... রব্বানা লা তুআখিযনা ইন নাসীনা আও আখত্বা'না...",
            banglaMeaning = "রাসূল ঈমান এনেছেন যা তাঁর রবের পক্ষ থেকে তাঁর প্রতি নাযিল করা হয়েছে এবং মুমিনগণও... হে আমাদের রব! যদি আমরা ভুলে যাই কিংবা ভুল করি, তবে আমাদের পাকড়াও করবেন না। হে আমাদের রব! আমাদের পূর্ববর্তীদের ওপর যেমন বোঝা চাপিয়ে দিয়েছিলেন আমাদের ওপর তেমন বোঝা চাপাবেন না...",
            englishTranslation = "The Messenger has believed in what was revealed to him from his Lord, and [so have] the believers... Our Lord, do not impose blame upon us if we have forgotten or erred. Our Lord, and lay not upon us a burden like that which You laid upon those before us... You are our protector, so give us victory over the disbelieving people.",
            englishTransliteration = "Aamanar-Rasoolu bimaa unzila ilayhi mir-Rabbihee wal-mu'minoon...",
            reference = "সূরা আল-বাক্বারা: ২৮৫-২৮৬; সহীহ বুখারী: ৫০০৯",
            virtueBn = "রাসুলুল্লাহ (সা.) বলেছেন: যে ব্যক্তি রাতে সূরা বাকারার এই শেষ দুটি আয়াত পাঠ করবে, তা তার সকল অনিষ্ট থেকে রক্ষার জন্য যথেষ্ট হবে এবং শয়তান সেই ঘরের কাছে আসতে পারে না।",
            virtueEn = "Whoever recites the last two verses of Surah Al-Baqarah at night, it will suffice him against every harm.",
            targetCount = 1,
            isRuqyah = true,
            ruqyahType = "Black Magic & Jinn"
        ),
        DuaItem(
            id = "rq_kalimatillah",
            titleBn = "পূর্ণাঙ্গ কালিমার মাধ্যমে শয়তান ও বিষাক্ত প্রাণী হতে মুক্তি",
            titleEn = "Refuge in Allah's Perfect Words",
            titleAr = "أعوذ بكلمات الله التامة من كل شيطان وهامة",
            category = DuaCategory.RUQYAH,
            arabicText = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّةِ، مِنْ كُلِّ شَيْطَانٍ وَهَامَّةٍ، وَمِنْ كُلِّ عَيْنٍ لَامَّةٍ",
            banglaPronunciation = "আ'উযু বিকালিমা-তিল্লাহিত তা-ম্মাতি, মিন কুল্লি শাইত্বানিওঁ ওয়া হা-ম্মাহ, ওয়া মিন কুল্লি 'আইনিল লা-ম্মাহ।",
            banglaMeaning = "আমি আল্লাহর পরিপূর্ণ কালেমাসমূহের আশ্রয়ে পানাহ চাই প্রতিটি শয়তান, বিষাক্ত কীট-পতঙ্গ এবং অনিষ্টকারী বদনজরের কুপ্রভাব হতে।",
            englishTranslation = "I seek refuge in the Perfect Words of Allah from every devil and poisonous reptile, and from every harmful envious eye.",
            englishTransliteration = "A'udhu bi kalimatillahit-tammati min kulli shaytanin wa hammah, wa min kulli 'aynin lammah.",
            reference = "সহীহ বুখারী: ৩৩৭১ (ইব্রাহিম আ. ও রাসুল সা. এর সন্তানদের জন্য পঠিত দোয়া)",
            virtueBn = "ইব্রাহিম (আ.) ইসমাইল ও ইসহাক (আ.)-কে এই দোয়ার মাধ্যমে সুরক্ষা দিতেন। রাসুলুল্লাহ (সা.) হাসান ও হুসাইন (রা.)-এর ওপর এটি পাঠ করে ফুঁ দিতেন। শিশুদের বদনজর থেকে বাঁচানোর জন্য এটি সর্বোত্তম।",
            virtueEn = "The Prophet (PBUH) used this prayer to protect his grandsons Hasan and Husain from the evil eye.",
            targetCount = 3,
            isRuqyah = true,
            ruqyahType = "Evil Eye"
        ),

        // ================= DAILY LIFE DUAS =================
        DuaItem(
            id = "dl_wake_up",
            titleBn = "ঘুম থেকে ওঠার দোয়া",
            titleEn = "Dua Upon Waking Up",
            titleAr = "دعاء الاستيقاظ من النوم",
            category = DuaCategory.DAILY_LIFE,
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            banglaPronunciation = "আলহামদু লিল্লাহিল্লাযী আহ্ইয়ানা বা'দা মা আমাতানা ওয়া ইলাইহিন নুশূর।",
            banglaMeaning = "সকল প্রশংসা আল্লাহর জন্য, যিনি আমাদেরকে মৃত্যুর (ঘুমের) পর পুনরায় জীবিত করলেন এবং তাঁরই দিকে সকলের পুনরুত্থান।",
            englishTranslation = "All praise is for Allah who gave us life after having taken it from us, and unto Him is the resurrection.",
            englishTransliteration = "Alhamdu lillahil-ladhi ahyana ba'da ma amatana wa ilayhin-nushoor.",
            reference = "সহীহ বুখারী: ৬৩১২; সহীহ মুসলিম: ২৭১১",
            virtueBn = "ঘুম থেকে জেগে এই দোয়া পাঠ করে মিসওয়াক বা অজু করলে মনের সকল অলসতা দূর হয় এবং আল্লাহর রহমতের গণ্ডিতে দিন শুরু হয়।",
            virtueEn = "Reciting this upon waking acknowledges Allah's gift of life and starts the day with gratitude.",
            targetCount = 1,
            isRuqyah = false
        ),
        DuaItem(
            id = "dl_sleep",
            titleBn = "ঘুমানোর সময় দোয়া",
            titleEn = "Dua Before Sleeping",
            titleAr = "دعاء النوم",
            category = DuaCategory.DAILY_LIFE,
            arabicText = "اللَّهُمَّ بِاسْمِكَ أَمُوتُ وَأَحْيَا",
            banglaPronunciation = "আল্লাহুম্মা বিসমিকা আমূতু ওয়া আহ্ইয়া।",
            banglaMeaning = "হে আল্লাহ! আপনারই নামে আমি মৃত্যুবরণ করছি (ঘুমাচ্ছি) এবং আপনারই নামে পুনর্জীবিত হব।",
            englishTranslation = "O Allah, in Your name I die and I live.",
            englishTransliteration = "Allahumma bismika amootu wa ahya.",
            reference = "সহীহ বুখারী: ৬৩২৪",
            virtueBn = "ডান কাত হয়ে ডান হাত গালের নিচে রেখে এই দোয়া পাঠ করা সুন্নাত।",
            virtueEn = "Lying on the right side and reciting this surrenders one's soul into Allah's care during sleep.",
            targetCount = 1,
            isRuqyah = false
        ),
        DuaItem(
            id = "dl_leave_home",
            titleBn = "ঘর থেকে বের হওয়ার দোয়া (শয়তান থেকে সম্পূর্ণ নিরাপদ)",
            titleEn = "Dua When Leaving Home",
            titleAr = "دعاء الخروج من المنزل",
            category = DuaCategory.DAILY_LIFE,
            arabicText = "بِسْمِ اللَّهِ، تَوَكَّلْتُ عَلَى اللَّهِ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            banglaPronunciation = "বিসমিল্লাহি তাওয়াক্কালতু 'আলাল্লাহ, ওয়ালা হাওলা ওয়ালা কুউওয়াতা ইল্লা বিল্লাহ।",
            banglaMeaning = "আল্লাহর নামে, আল্লাহর ওপর ভরসা করলাম। আর আল্লাহর সাহায্য ছাড়া কোনো অনিষ্ট হতে বাঁচার উপায় নেই এবং কোনো নেক কাজ করার শক্তি নেই।",
            englishTranslation = "In the name of Allah, I place my trust in Allah; there is no might nor power except with Allah.",
            englishTransliteration = "Bismillahi tawakkaltu 'alallah, wa la hawla wa la quwwata illa billah.",
            reference = "সুনান আবু দাউদ: ৫০৯৫; জামে আত-তিরমিযী: ৩৪২৬",
            virtueBn = "রাসুলুল্লাহ (সা.) বলেছেন: যখন কেউ ঘর থেকে বের হওয়ার সময় এটি বলে, তখন তাকে বলা হয়, তোমাকে হেদায়াত দেওয়া হলো, তোমার যাবতীয় দায়িত্ব গ্রহণ করা হলো এবং তোমাকে রক্ষা করা হলো। আর শয়তান তার থেকে দূরে সরে যায়।",
            virtueEn = "Whoever says this, angels proclaim: You have been guided, defended and protected, and Satan retreats from him.",
            targetCount = 1,
            isRuqyah = true,
            ruqyahType = "General Protection"
        ),
        DuaItem(
            id = "dl_enter_home",
            titleBn = "ঘরে প্রবেশের দোয়া",
            titleEn = "Dua When Entering Home",
            titleAr = "دعاء دخول المنزل",
            category = DuaCategory.DAILY_LIFE,
            arabicText = "بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَى رَبِّنَا تَوَكَّلْنَا",
            banglaPronunciation = "বিসমিল্লাহি ওয়ালাজনা, ওয়া বিসমিল্লাহি খারাজনা, ওয়া 'আলা রব্বিনা তাওয়াক্কালনা।",
            banglaMeaning = "আল্লাহর নামে আমরা প্রবেশ করলাম, আল্লাহর নামে আমরা বের হয়েছিলাম এবং আমাদের রবের ওপর আমরা ভরসা করলাম। (এরপর পরিবারকে সালাম দিন)",
            englishTranslation = "In the name of Allah we enter, and in the name of Allah we leave, and upon our Lord we rely.",
            englishTransliteration = "Bismillahi walajna, wa bismillahi kharajna, wa 'ala Rabbina tawakkalna.",
            reference = "সুনান আবু দাউদ: ৫০৯৬",
            virtueBn = "ঘরে প্রবেশের সময় আল্লাহর নাম স্মরণ করলে শয়তান বলে: এখানে তোমাদের রাত কাটানোর কোনো জায়গা নেই এবং কোনো আহার নেই।",
            virtueEn = "Reciting this keeps the devil away from spending the night or sharing food in your home.",
            targetCount = 1,
            isRuqyah = false
        ),
        DuaItem(
            id = "dl_toilet_enter",
            titleBn = "টয়লেটে প্রবেশের দোয়া",
            titleEn = "Dua Before Entering Toilet",
            titleAr = "دعاء دخول الخلاء",
            category = DuaCategory.DAILY_LIFE,
            arabicText = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْخُبُثِ وَالْخَبَائِثِ",
            banglaPronunciation = "আল্লাহুম্মা ইন্নী আ'উযু বিকা মিনাল খুবুসি ওয়াল খাবা-ইছ।",
            banglaMeaning = "হে আল্লাহ! আমি আপনার নিকট অপবিত্র পুরুষ শয়তান ও নারী শয়তানের অনিষ্ট হতে আশ্রয় প্রার্থনা করছি।",
            englishTranslation = "O Allah, I seek refuge in You from all evil and evil spirits (male and female devils).",
            englishTransliteration = "Allahumma inni a'udhu bika minal-khubuthi wal-khaba'ith.",
            reference = "সহীহ বুখারী: ১৪২; সহীহ মুসলিম: ৩৭৫",
            virtueBn = "বাম পা দিয়ে প্রবেশের পূর্বে এই দোয়া পাঠ করলে মানুষের সতর ও জ্বিন-শয়তানের চোখের মাঝে পর্দা পড়ে যায়।",
            virtueEn = "Protects against harm from male and female evil jinn residing in unclean places.",
            targetCount = 1,
            isRuqyah = true,
            ruqyahType = "General Protection"
        ),
        DuaItem(
            id = "dl_toilet_leave",
            titleBn = "টয়লেট থেকে বের হওয়ার দোয়া",
            titleEn = "Dua After Leaving Toilet",
            titleAr = "دعاء الخروج من الخلاء",
            category = DuaCategory.DAILY_LIFE,
            arabicText = "غُفْرَانَكَ",
            banglaPronunciation = "গুফরা-নাক।",
            banglaMeaning = "হে আল্লাহ! আমি আপনার নিকট ক্ষমা প্রার্থনা করছি।",
            englishTranslation = "I ask You for Your forgiveness.",
            englishTransliteration = "Ghufranaka.",
            reference = "সুনান আবু দাউদ: ৩০; জামে আত-তিরমিযী: ৭",
            virtueBn = "ডান পা দিয়ে বের হয়ে এই দোয়া পাঠ করা সুন্নাত। শরীর থেকে কষ্টদায়ক বস্তু দূর করায় আল্লাহর শুকরিয়া ও ক্ষমা চাওয়া হয়।",
            virtueEn = "Sunnah supplication asking for Allah's forgiveness after relieving bodily discomfort.",
            targetCount = 1,
            isRuqyah = false
        ),

        // ================= PRAYER & PURIFICATION =================
        DuaItem(
            id = "pw_after_wudu",
            titleBn = "ওজুর পরের দোয়া (জান্নাতের ৮টি দরজা খোলার সনদ)",
            titleEn = "Dua After Ablution (Wudu)",
            titleAr = "الذكر بعد الفراغ من الوضوء",
            category = DuaCategory.PRAYER_WUDU,
            arabicText = "أَشْهَدُ أَنْ لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ، اللَّهُمَّ اجْعَلْنِي مِنَ التَّوَّابِينَ، وَاجْعَلْنِي مِنَ الْمُتَطَهِّرِينَ",
            banglaPronunciation = "আশহাদু আল লা ইলাহা ইল্লাল্লাহু ওয়াহদাহু লা শারীকা লাহু, ওয়া আশহাদু আন্না মুহাম্মাদান 'আবদুহু ওয়া রাসূলুহু। আল্লাহুম্মাজ'আলনী মিনাত তাওওয়াবীন, ওয়াজ'আলনী মিনাল মুতাত্বহহিরীন।",
            banglaMeaning = "আমি সাক্ষ্য দিচ্ছি যে, এক আল্লাহ ছাড়া কোনো সত্য উপাস্য নেই, তাঁর কোনো শরিক নেই। আমি আরও সাক্ষ্য দিচ্ছি যে, মুহাম্মাদ (সা.) তাঁর বান্দা ও রাসূল। হে আল্লাহ! আমাকে তাওবাকারীদের অন্তর্ভুক্ত করুন এবং আমাকে পবিত্রতা অর্জনকারীদের দলভুক্ত করুন।",
            englishTranslation = "I testify that there is no deity except Allah alone without partner, and I testify that Muhammad is His servant and messenger. O Allah, make me of the repentant and make me of the purified.",
            englishTransliteration = "Ashhadu alla ilaha illallahu wahdahu la shareeka lah, wa ashhadu anna Muhammadan 'abduhu wa Rasooluh...",
            reference = "সহীহ মুসলিম: ২৩৪; জামে আত-তিরমিযী: ৫৫",
            virtueBn = "রাসুলুল্লাহ (সা.) বলেছেন: যে ব্যক্তি উত্তমরূপে অজু করার পর এই দোয়া পাঠ করবে, তার জন্য জান্নাতের আটটি দরজাই খুলে দেওয়া হয়; সে যে দরজা দিয়ে ইচ্ছা প্রবেশ করতে পারে।",
            virtueEn = "Whoever recites this after performing wudu properly, all eight gates of Paradise are opened for him.",
            targetCount = 1,
            isRuqyah = false
        ),
        DuaItem(
            id = "pw_after_adhan",
            titleBn = "আযানের পরের দোয়া (রাসুল সা. এর শাফাআত লাভ)",
            titleEn = "Dua After the Adhan",
            titleAr = "الدعاء بعد الأذان",
            category = DuaCategory.PRAYER_WUDU,
            arabicText = "اللَّهُمَّ رَبَّ هَذِهِ الدَّعْوَةِ التَّامَّةِ، وَالصَّلَاةِ الْقَائِمَةِ، آتِ مُحَمَّدًا الْوَسِيلَةَ وَالْفَضِيلَةَ، وَابْعَثْهُ مَقَامًا مَحْمُودًا الَّذِي وَعَدْتَهُ",
            banglaPronunciation = "আল্লাহুম্মা রব্বা হাযিহিদ দা'ওয়াতিত তা-ম্মাহ, ওয়াস সালাতিল ক্বা-ইমাহ, আতি মুহাম্মাদানিল ওয়াসীলাতা ওয়াল ফাদ্বীলাহ, ওয়াব'আসহু মাক্বামাম মাহমূদানিল্লাযী ওয়া'আদতাহ।",
            banglaMeaning = "হে আল্লাহ! এই পরিপূর্ণ আহ্বান ও কায়েম হতে যাওয়া নামাজের রব! মুহাম্মাদ (সাল্লাল্লাহু আলাইহি ওয়া সাল্লাম)-কে দান করুন উসিলা (জান্নাতের বিশেষ সর্বোচ্চ মর্যাদা) ও মর্যাদা, এবং তাঁকে পৌঁছে দিন সেই প্রশংসিত স্থানে যার প্রতিশ্রুতি আপনি তাঁকে দিয়েছেন।",
            englishTranslation = "O Allah, Lord of this perfect call and established prayer, grant Muhammad the status of Wasilah and superiority, and raise him up to the praiseworthy station which You have promised him.",
            englishTransliteration = "Allahumma Rabba hazihid-da'watit-tammah, was-salatil-qa'imah, ati Muhammadanil-waseelata wal-fadeelah...",
            reference = "সহীহ বুখারী: ৬১৪",
            virtueBn = "যে ব্যক্তি আযান শুনে এই দোয়া পাঠ করবে, কিয়ামতের দিন তার জন্য রাসুলুল্লাহ (সা.) এর শাফাআত ওয়াজিব হয়ে যাবে।",
            virtueEn = "Whoever recites this after hearing the Adhan will be granted intercession (Shafa'ah) of the Prophet on Judgment Day.",
            targetCount = 1,
            isRuqyah = false
        ),
        DuaItem(
            id = "pw_qunut",
            titleBn = "বিতর নামাজের দোয়া কুনুত",
            titleEn = "Dua Qunut in Witr Prayer",
            titleAr = "دعاء القنوت في الوتر",
            category = DuaCategory.PRAYER_WUDU,
            arabicText = "اللَّهُمَّ اهْدِنِي فِيمَنْ هَدَيْتَ، وَعَافِنِي فِيمَنْ عَافَيْتَ، وَتَوَلَّنِي فِيمَنْ تَوَلَّيْتَ، وَبَارِكْ لِي فِيمَا أَعْطَيْتَ، وَقِنِي شَرَّ مَا قَضَيْتَ، فَإِنَّكَ تَقْضِي وَلَا يُقْضَى عَلَيْكَ، وَإِنَّهُ لَا يَذِلُّ مَنْ وَالَيْتَ، وَلَا يَعِزُّ مَنْ عَادَيْتَ، تَبَارَكْتَ رَبَّنَا وَتَعَالَيْتَ",
            banglaPronunciation = "আল্লাহুম্মাহদিনী ফীমান হাদায়ত, ওয়া 'আফিনী ফীমান 'আফায়ত, ওয়া তাওয়াল্লানী ফীমান তাওয়াল্লায়ত, ওয়া বারিক লী ফীমা আ'ত্বায়ত, ওয়া ক্বিনী শাররা মা ক্বাদ্বায়ত, ফাইন্নাকা তাক্বদ্বী ওয়ালা ইয়ুক্বদ্বা 'আলাইক, ওয়া ইন্নাহূ লা ইয়াযিল্লু মাওঁ ওয়ালায়ত, ওয়ালা ইয়া'ইযযু মান 'আদায়ত, তাবারাকতা রব্বানা ওয়া তা'আলায়ত।",
            banglaMeaning = "হে আল্লাহ! আপনি যাদের হেদায়াত দিয়েছেন তাদের সাথে আমাকেও হেদায়াত দিন। যাদের নিরাপত্তা দিয়েছেন তাদের সাথে আমাকেও নিরাপত্তা দিন। যাদের দায়িত্ব গ্রহণ করেছেন তাদের সাথে আমারও অভিভাবকত্ব নিন। যা দান করেছেন তাতে বরকত দিন এবং যা ফয়সালা করেছেন তার মন্দ হতে আমাকে রক্ষা করুন...",
            englishTranslation = "O Allah, guide me among those You have guided, grant me health among those You have granted health, look after me among those You have looked after, bless for me what You have given, and save me from the evil of what You have decreed...",
            englishTransliteration = "Allahummahdini feeman hadayt, wa 'aafinee feeman 'aafayt...",
            reference = "সুনান আবু দাউদ: ১৪২৫; জামে আত-তিরমিযী: ৪৬৪",
            virtueBn = "রাসুলুল্লাহ (সা.) স্বয়ং তাঁর প্রিয় দৌহিত্র হাসান ইবনে আলী (রা.)-কে বিতর নামাজে পাঠ করার জন্য এই দোয়াটি শিখিয়েছিলেন।",
            virtueEn = "Taught by the Prophet (PBUH) to Hasan ibn Ali for recitation in Witr prayer.",
            targetCount = 1,
            isRuqyah = false
        ),

        // ================= DISTRESS & FORGIVENESS =================
        DuaItem(
            id = "df_yunus",
            titleBn = "দোয়া ইউনুস (সকল প্রকার সংকট ও বিপদ মুক্তির অব্যর্থ দোয়া)",
            titleEn = "Dua of Prophet Yunus (AS) in Distress",
            titleAr = "دعاء يونس عليه السلام (دعوة ذي النون)",
            category = DuaCategory.DISTRESS_FORGIVENESS,
            arabicText = "لَّا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
            banglaPronunciation = "লা ইলাহা ইল্লা আনতা, সুবহানাকা ইন্নী কুনতু মিনায যালিমীন।",
            banglaMeaning = "আপনি ছাড়া কোনো সত্য উপাস্য নেই, আপনি পরম পবিত্র ও নির্দোষ। নিশ্চয়ই আমি অপরাধীদের অন্তর্ভুক্ত হয়ে গেছি।",
            englishTranslation = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.",
            englishTransliteration = "La ilaha illa Anta, subhanaka inni kuntu minaz-zalimeen.",
            reference = "সূরা আল-আম্বিয়া: ৮৭; জামে আত-তিরমিযী: ৩৫০৫ (সহীহ)",
            virtueBn = "রাসুলুল্লাহ (সা.) বলেছেন: মাছের পেটে থাকা অবস্থায় যুন-নূন (ইউনুস আ.) যে দোয়া করেছিলেন, কোনো মুসলিম যদি যেকোনো সংকট বা বিপদে এই দোয়ার মাধ্যমে আল্লাহর কাছে দোয়া করে, আল্লাহ অবশ্যই তার দোয়া কবুল করবেন।",
            virtueEn = "No Muslim supplication with this in any distress except that Allah will answer his prayer.",
            targetCount = 1,
            isRuqyah = true,
            ruqyahType = "Mental Distress & Anxiety"
        ),
        DuaItem(
            id = "df_anxiety_sorrow",
            titleBn = "দুশ্চিন্তা, পেরেশানি ও অক্ষমতা থেকে মুক্তির দোয়া",
            titleEn = "Dua for Relief from Anxiety, Sorrow & Debt",
            titleAr = "دعاء الهم والحزن والدين",
            category = DuaCategory.DISTRESS_FORGIVENESS,
            arabicText = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْبُخْلِ وَالْجُبْنِ، وَضَلَعِ الدَّيْنِ، وَغَلَبَةِ الرِّجَالِ",
            banglaPronunciation = "আল্লাহুম্মা ইন্নী আ'উযু বিকা মিনাল হাম্মি ওয়াল হাযানি, ওয়াল 'আজযি ওয়াল কাসালি, ওয়াল বুখলি ওয়াল জুবনি, ওয়া দ্বালা'ইদ দাইনি, ওয়া গালাবাতির রিজাল।",
            banglaMeaning = "হে আল্লাহ! আমি আপনার আশ্রয় নিচ্ছি দুশ্চিন্তা ও পেরেশানি থেকে, অক্ষমতা ও অলসতা থেকে, কৃপণতা ও কাপুরুষতা থেকে, ঋণের বোঝা এবং মানুষের জুলুম ও আধিপত্য থেকে।",
            englishTranslation = "O Allah, I seek refuge in You from anxiety and grief, weakness and laziness, miserliness and cowardice, the burden of debts and from being overpowered by men.",
            englishTransliteration = "Allahumma inni a'udhu bika minal-hammi wal-hazani, wal-'ajzi wal-kasali...",
            reference = "সহীহ বুখারী: ২৮৯৩; সহীহ মুসলিম: ২৭০৬",
            virtueBn = "আনাস (রা.) বলেন, রাসুলুল্লাহ (সা.) এই দোয়াটি সর্বাধিক পাঠ করতেন। জীবনের মানসিক অশান্তি, হতাশা ও অভাব দূর করতে এই দোয়া অতুলনীয়।",
            virtueEn = "One of the most frequent prayers of the Prophet (PBUH) for peace of mind and overcoming debts.",
            targetCount = 3,
            isRuqyah = true,
            ruqyahType = "Anxiety & Depression"
        ),
        DuaItem(
            id = "df_debt_relief",
            titleBn = "পাহাড় সমান ঋণ পরিশোধের মোক্ষম দোয়া",
            titleEn = "Dua for Relief from Debt",
            titleAr = "دعاء قضاء الدين",
            category = DuaCategory.DISTRESS_FORGIVENESS,
            arabicText = "اللَّهُمَّ اكْفِنِي بِحَلَالِكَ عَنْ حَرَامِكَ، وَأَغْنِنِي بِفَضْلِكَ عَمَّنْ سِوَاكَ",
            banglaPronunciation = "আল্লাহুম্মাকফিনী বিহালালিকা 'আন হারামিক, ওয়া আগনিনী বিফাদ্বলিকা 'আম্মান সিওয়াক।",
            banglaMeaning = "হে আল্লাহ! আমাকে আপনার হালাল রিজিক দ্বারা তৃপ্ত করে হারাম থেকে বাঁচিয়ে রাখুন এবং আপনার অসীম করুণার দ্বারা আপনি ছাড়া অন্য সকলের মুখাপেক্ষিতা থেকে আমাকে ধনী ও অমুখাপেক্ষী করে দিন।",
            englishTranslation = "O Allah, suffice me with what You have allowed instead of what You have forbidden, and make me independent of all others by Your grace.",
            englishTransliteration = "Allahummak-finee bi-halalika 'an haramika, wa aghninee bi-fadlika 'amman siwaka.",
            reference = "জামে আত-তিরমিযী: ৩৫৬৩ (হাসান)",
            virtueBn = "আলী (রা.) এক ঋণগ্রস্ত ব্যক্তিকে বললেন, আমি কি তোমাকে এমন কিছু বাক্য শেখাব যা রাসুল (সা.) আমাকে শিখিয়েছিলেন? তোমার ওপর যদি সীর পাহাড় পরিমাণ ঋণও থাকে, আল্লাহ তোমার পক্ষ থেকে তা পরিশোধের ব্যবস্থা করে দেবেন।",
            virtueEn = "Ali (RA) taught this saying that even if one had debt like a massive mountain, Allah would clear it.",
            targetCount = 3,
            isRuqyah = false
        ),

        // ================= QURANIC RABBANA DUAS =================
        DuaItem(
            id = "rb_dunya_akhirah",
            titleBn = "রব্বানা: দুনিয়া ও আখিরাতে কল্যাণের শ্রেষ্ঠ দোয়া",
            titleEn = "Rabbana: For Good in This World & the Hereafter",
            titleAr = "ربنا آتنا في الدنيا حسنة",
            category = DuaCategory.RABBANA_QURAN,
            arabicText = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            banglaPronunciation = "রব্বানা আতিনা ফিদ দুনইয়া হাসানাতাওঁ ওয়া ফিল আখিরাতি হাসানাতাওঁ ওয়াক্বিনা 'আযাবান নার।",
            banglaMeaning = "হে আমাদের রব! আমাদের দুনিয়াতে কল্যাণ দান করুন এবং আখিরাতেও কল্যাণ দান করুন, আর আমাদেরকে জাহান্নামের আগুন থেকে রক্ষা করুন।",
            englishTranslation = "Our Lord, give us in this world [that which is] good and in the Hereafter [that which is] good and protect us from the punishment of the Fire.",
            englishTransliteration = "Rabbana atina fid-dunya hasanatan wa fil-akhirati hasanatan wa qina 'adhaban-nar.",
            reference = "সূরা আল-বাক্বারা: ২০১; সহীহ বুখারী: ৪৫২২",
            virtueBn = "আনাস (রা.) বলেন, রাসুলুল্লাহ (সা.) দোয়ার মধ্যে এই আয়াতটি সবচাইতে বেশি বেশি পাঠ করতেন। তাওয়াফের সময় ও দুই সেজদার মাঝেও পাঠ করা বরকতময়।",
            virtueEn = "The most frequent supplication of the Prophet Muhammad (peace be upon him).",
            targetCount = 3,
            isRuqyah = false
        ),
        DuaItem(
            id = "rb_parents",
            titleBn = "রব্বানা: পিতা-মাতা ও নিজের ক্ষমার দোয়া",
            titleEn = "Rabbana: For Forgiveness of Parents & Believers",
            titleAr = "ربنا اغفر لي ولوالدي",
            category = DuaCategory.RABBANA_QURAN,
            arabicText = "رَبَّنَا اغْفِرْ لِي وَلِوَالِدَيَّ وَلِلْمُؤْمِنِينَ يَوْمَ يَقُومُ الْحِسَابُ",
            banglaPronunciation = "রব্বানাগফির লী ওয়ালি ওয়ালিদাইয়্যা ওয়া লিলমু'মিনীনা ইয়াওমা ইয়াক্বূমুল হিসা-ব।",
            banglaMeaning = "হে আমাদের রব! যেদিন হিসাব কায়েম হবে, সেদিন আমাকে, আমার পিতা-মাতাকে এবং সকল মুমিনকে ক্ষমা করে দিন।",
            englishTranslation = "Our Lord, forgive me and my parents and the believers the Day the account is established.",
            englishTransliteration = "Rabbanagh-fir lee wa li-walidayya wa lil-mu'mineena yawma yaqoomul-hisaab.",
            reference = "সূরা ইব্রাহিম: ৪১ (হযরত ইব্রাহিম আ. এর দোয়া)",
            virtueBn = "পিতা-মাতার জন্য এর চেয়ে সুন্দর সমন্বিত দোয়া আর হতে পারে না। প্রতি নামাজের শেষ বৈঠকে বা সেজদায় এই দোয়া পাঠ করা উত্তম।",
            virtueEn = "Prophet Ibrahim's noble supplication for his parents and all believers on the Day of Account.",
            targetCount = 1,
            isRuqyah = false
        ),
        DuaItem(
            id = "rb_family_children",
            titleBn = "রব্বানা: নেককার স্ত্রী, স্বামী ও চক্ষু শীতলকারী সন্তানের দোয়া",
            titleEn = "Rabbana: For Righteous Spouse & Children",
            titleAr = "ربنا هب لنا من أزواجنا وذرياتنا قرة أعين",
            category = DuaCategory.RABBANA_QURAN,
            arabicText = "رَبَّنَا هَبْ لَنَا مِنْ أَزْوَاجِنَا وَذُرِّيَّاتِنَا قُرَّةَ أَعْيُنٍ وَاجْعَلْنَا لِلْمُتَّقِينَ إِمَامًا",
            banglaPronunciation = "রব্বানা হাব লানা মিন আযওয়াজিনা ওয়া যুররিইয়্যাতিনা ক্বুররাতা আ'ইউনিওঁ ওয়াজ'আলনা লিল মুত্তাক্বীনা ইমামা।",
            banglaMeaning = "হে আমাদের রব! আমাদের জন্য এমন স্ত্রী/স্বামী ও সন্তান-সন্ততি দান করুন যারা আমাদের চোখ শীতল করবে, আর আমাদের পরহেযগার মুত্তাকীদের জন্য আদর্শ নেতা বানিয়ে দিন।",
            englishTranslation = "Our Lord, grant us from among our wives and offspring comfort to our eyes and make us a leader for the righteous.",
            englishTransliteration = "Rabbana hab lana min azwajina wa dhurriyyatina qurrata a'yunin waj'alna lil-muttaqeena imama.",
            reference = "সূরা আল-ফুরকান: ৭৪ (রহমানের খাঁটি বান্দাদের গুণাবলী)",
            virtueBn = "দাম্পত্য কলহ দূর করতে, সংসারে শান্তি ও সন্তানকে দ্বীনদার বানাতে এই দোয়া অতুলনীয়।",
            virtueEn = "Supplication of the servants of the Most Merciful for family happiness and righteous descendants.",
            targetCount = 1,
            isRuqyah = false
        ),
        DuaItem(
            id = "rb_firmness_faith",
            titleBn = "রব্বানা: দ্বীন ও হেদায়াতের ওপর অবিচল থাকার দোয়া",
            titleEn = "Rabbana: For Steadfastness in Faith",
            titleAr = "ربنا لا تزغ قلوبنا بعد إذ هديتنا",
            category = DuaCategory.RABBANA_QURAN,
            arabicText = "رَبَّنَا لَا تُزِغْ قُلُوبَنَا بَعْدَ إِذْ هَدَيْتَنَا وَهَبْ لَنَا مِن لَّدُنكَ رَحْمَةً ۚ إِنَّكَ أَنتَ الْوَهَّابُ",
            banglaPronunciation = "রব্বানা লা তুযিগ ক্বুলূবানা বা'দা ইয হাদায়তানা ওয়া হাব লানা মিল্লাদুনকা রাহমাহ, ইন্নাকা আনতাল ওয়াহ্হাব।",
            banglaMeaning = "হে আমাদের রব! আমাদের হেদায়াত দান করার পর আপনি আমাদের অন্তরসমূহকে সত্যচ্যুত ও বিপথগামী করবেন না। আর আপনার পক্ষ থেকে আমাদের প্রতি বিশেষ রহমত দান করুন। নিশ্চয় আপনি মহাদাতা।",
            englishTranslation = "Our Lord, let not our hearts deviate after You have guided us and grant us from Yourself mercy. Indeed, You are the Bestower.",
            englishTransliteration = "Rabbana la tuzigh quloobana ba'da idh hadaytana wa hab lana mil-ladunka rahmah, innaka Antal-Wahhab.",
            reference = "সূরা আলে-ইমরান: ৮",
            virtueBn = "মানুষের হৃদয় পরিবর্তনশীল। ঈমান রক্ষা ও ফেতনার যুগে পথভ্রষ্টতা থেকে বাঁচার জন্য এটি অন্যতম সেরা কোরআনি প্রার্থনা।",
            virtueEn = "Shields one's heart from deviation and doubts after having received guidance.",
            targetCount = 1,
            isRuqyah = false
        ),

        // ================= ILLNESS & SHIFA =================
        DuaItem(
            id = "sh_general_sickness",
            titleBn = "রোগমুক্তির সার্বজনীন সুন্নতি দোয়া (আজহিবিল বা'স)",
            titleEn = "Sunnah Prayer for Healing Sickness",
            titleAr = "دعاء الشفاء للمريض",
            category = DuaCategory.SHIFA_ILLNESS,
            arabicText = "اللَّهُمَّ رَبَّ النَّاسِ، أَذْهِبِ الْبَاسَ، اشْفِهِ وَأَنْتَ الشَّافِي، لَا شِفَاءَ إِلَّا شِفَاؤُكَ، شِفَاءً لَا يُغَادِرُ سَقَمًا",
            banglaPronunciation = "আল্লাহুম্মা রব্বান নাস, আযহিবিল বা'স, ইশফি ওয়া আনতাশ শাফী, লা শিফা-আ ইল্লা শিফা-উকা, শিফা-আল লা ইউগাদিরু সাক্বামা।",
            banglaMeaning = "হে আল্লাহ! মানবজাতির রব! কষ্ট দূর করে দিন, আরোগ্য দান করুন; আপনিই প্রকৃত আরোগ্যদানকারী। আপনার শিফা ছাড়া আর কোনো শিফা নেই—এমন এক শিফা দান করুন যা কোনো রোগ বা ব্যাধি বাকি রাখে না।",
            englishTranslation = "O Allah, Lord of mankind, remove the affliction and heal him, for You are the Healer. There is no healing except Your healing—a healing that leaves behind no illness.",
            englishTransliteration = "Allahumma Rabban-nas, adhhibil-ba's, ishfi wa Antash-Shafi, la shifa'a illa shifa'uk, shifa'an la yughadiru saqama.",
            reference = "সহীহ বুখারী: ৫৭৪২; সহীহ মুসলিম: ২১৯১",
            virtueBn = "রাসুলুল্লাহ (সা.) কোনো রোগী দেখতে গেলে নিজের ডান হাত রোগীর ওপর বুলিয়ে দিতেন এবং এই দোয়া পাঠ করতেন।",
            virtueEn = "Recited by the Prophet (PBUH) while wiping his right hand over the sick person.",
            targetCount = 3,
            isRuqyah = true,
            ruqyahType = "Physical Pain & Illness"
        ),
        DuaItem(
            id = "sh_visiting_sick_seven_times",
            titleBn = "রোগী দেখতে গিয়ে নিশ্চিত আরোগ্যের দোয়া (৭ বার)",
            titleEn = "Supplication When Visiting the Sick (7 Times)",
            titleAr = "دعاء زيارة المريض سبع مرات",
            category = DuaCategory.SHIFA_ILLNESS,
            arabicText = "أَسْأَلُ اللَّهَ الْعَظِيمَ، رَبَّ الْعَرْشِ الْعَظِيمِ، أَنْ يَشْفِيَكَ",
            banglaPronunciation = "আসআলুল্লাহাল 'আজীম, রব্বাল 'আরশিল 'আজীম, আইঁ ইয়াশফিয়াক।",
            banglaMeaning = "আমি মহান আল্লাহর নিকট, যিনি সুমহান আরশের অধিপতি, প্রার্থনা করছি যেন তিনি তোমাকে পূর্ণ রোগমুক্তি দান করেন।",
            englishTranslation = "I ask Allah the Almighty, Lord of the Magnificent Throne, to cure you.",
            englishTransliteration = "As'alullahal-'Azeem, Rabbal-'Arshil-'Azeem, ay-yashfiyak.",
            reference = "সুনান আবু দাউদ: ৩১০৬; জামে আত-তিরমিযী: ২০৮৩ (সহীহ)",
            virtueBn = "যে ব্যক্তি এমন কোনো রোগীর কাছে গিয়ে ৭ বার এই দোয়া পাঠ করবে যার মৃত্যুর সময় এখনও আসেনি, আল্লাহ তাকে অবশ্যই সেই রোগ থেকে সুস্থতা দান করবেন।",
            virtueEn = "Reciting this 7 times for a patient whose appointed time has not arrived guarantees cure by Allah's will.",
            targetCount = 7,
            isRuqyah = true,
            ruqyahType = "Physical Pain"
        ),
        DuaItem(
            id = "sh_ayyub_prayer",
            titleBn = "হযরত আইয়ুব (আ.)-এর চরম অসুস্থতার মোনাজাত",
            titleEn = "Prophet Ayyub's (AS) Supplication in Extreme Illness",
            titleAr = "دعاء أيوب عليه السلام عند الضر",
            category = DuaCategory.SHIFA_ILLNESS,
            arabicText = "أَنِّي مَسَّنِيَ الضُّرُّ وَأَنتَ أَرْحَمُ الرَّاحِمِينَ",
            banglaPronunciation = "আন্নী মাস্সানিয়াদ্ব দুররু ওয়া আনতা আরহামুর রাহিমীন।",
            banglaMeaning = "নিশ্চয়ই ব্যাধি ও কষ্ট আমাকে স্পর্শ করেছে, আর আপনি সর্বশ্রেষ্ঠ দয়ালু।",
            englishTranslation = "Indeed, adversity has touched me, and You are the Most Merciful of the merciful.",
            englishTransliteration = "Annee massaniyad-durru wa Anta Arhamur-Rahimeen.",
            reference = "সূরা আল-আম্বিয়া: ৮৩",
            virtueBn = "দীর্ঘ বছর দুরারোগ্য ব্যাধিতে কাতর থাকার পর আইয়ুব (আ.) বিনম্রভাবে এই আরজি করেছিলেন এবং আল্লাহ তাঁর সকল রোগব্যাধি দূর করে সুস্থতা ও হারানো নিয়ামত দ্বিগুণ ফিরিয়ে দিয়েছিলেন।",
            virtueEn = "Prophet Ayyub's prayer through which Allah miraculously cured him from prolonged illness.",
            targetCount = 3,
            isRuqyah = true,
            ruqyahType = "Physical Pain & Illness"
        ),

        // ================= PROTECTION =================
        DuaItem(
            id = "pr_epidemic_diseases",
            titleBn = "মহামারী, শ্বেতরোগ ও দূরারোগ্য ব্যাধি থেকে মুক্তির দোয়া",
            titleEn = "Protection from Epidemics & Dreadful Diseases",
            titleAr = "دعاء الوقاية من الأمراض الوبائية",
            category = DuaCategory.PROTECTION,
            arabicText = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْبَرَصِ، وَالْجُنُونِ، وَالْجُذَامِ، وَمِنْ سَيِّئِ الْأَسْقَامِ",
            banglaPronunciation = "আল্লাহুম্মা ইন্নী আ'উযু বিকা মিনাল বারাসি, ওয়াল জুনূনি, ওয়াল জুযামি, ওয়া মিন সাইয়্যিইল আসক্বাম।",
            banglaMeaning = "হে আল্লাহ! আমি আপনার নিকট আশ্রয় চাই শ্বেত রোগ হতে, উন্মাদনা (পাগলামি) হতে, কুষ্ঠ রোগ হতে এবং যাবতীয় দুরারোগ্য জটিল রোগব্যাধি ও মহামারী হতে।",
            englishTranslation = "O Allah, I seek refuge in You from vitiligo, madness, leprosy, and evil diseases.",
            englishTransliteration = "Allahumma inni a'udhu bika minal-barasi, wal-junooni, wal-judhami, wa min sayyi'il-asqam.",
            reference = "সুনান আবু দাউদ: ১৫৫৪; সুনান নাসাঈ: ৫৪৯৩ (সহীহ)",
            virtueBn = "সংক্রামক রোগ, ক্যান্সার, প্যারালাইসিস ও অজানা মহামারী থেকে নিজেকে সুরক্ষিত রাখতে এই দোয়া নিয়মিত পাঠ করা অত্যন্ত গুরুত্বপূর্ণ।",
            virtueEn = "A comprehensive defense against contagious epidemics and terrible terminal illnesses.",
            targetCount = 1,
            isRuqyah = false
        ),
        DuaItem(
            id = "pr_sudden_calamity",
            titleBn = "আকস্মিক গজব ও নিয়ামত ছিনতাই থেকে বাঁচার দোয়া",
            titleEn = "Protection Against Sudden Calamity",
            titleAr = "الاستعاذة من زوال النعمة وفجاءة النقمة",
            category = DuaCategory.PROTECTION,
            arabicText = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنْ زَوَالِ نِعْمَتِكَ، وَتَحَوُّلِ عَافِيَتِكَ، وَفُجَاءَةِ نِقْمَتِكَ، وَجَمِيعِ سَخَطِكَ",
            banglaPronunciation = "আল্লাহুম্মা ইন্নী আ'উযু বিকা মিন যাওয়ালি নি'মাতিকা, ওয়া তাহাওউউলি 'আফিয়াতিকা, ওয়া ফুজা-আতি নিক্বমাতিকা, ওয়া জামী'ই সাখাত্বিকা।",
            banglaMeaning = "হে আল্লাহ! আমি আপনার আশ্রয় প্রার্থনা করছি আপনার নিয়ামত বিলুপ্ত হওয়া থেকে, সুস্থতা ও নিরাপত্তার পরিবর্তন থেকে, আকস্মিক শাস্তি থেকে এবং আপনার সকল অসন্তোষ ও ক্ষোভ থেকে।",
            englishTranslation = "O Allah, I seek refuge in You from the withdrawal of Your blessing, the transformation of Your good health, the sudden onset of Your punishment, and all of Your anger.",
            englishTransliteration = "Allahumma inni a'udhu bika min zawali ni'matika, wa tahawwuli 'afiyatika, wa fuja'ati niqmatika...",
            reference = "সহীহ মুসলিম: ২৭৩৯",
            virtueBn = "অর্থনৈতিক ধস, হঠাৎ দুর্ঘটনা এবং সুস্থতা নষ্ট হয়ে যাওয়ার ভয় থেকে আল্লাহর সুরক্ষার জন্য রাসুলুল্লাহ (সা.) নিয়মিত এটি পড়তেন।",
            virtueEn = "Shields against sudden catastrophes, loss of health, and loss of livelihood.",
            targetCount = 1,
            isRuqyah = false
        ),

        // ================= TRAVEL & FOOD =================
        DuaItem(
            id = "tf_vehicle",
            titleBn = "যানবাহনে আরোহণের সুন্নতি দোয়া",
            titleEn = "Dua for Boarding Vehicle / Travel",
            titleAr = "دعاء ركوب الدابة والسفر",
            category = DuaCategory.TRAVEL_FOOD,
            arabicText = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ ۝ وَإِنَّا إِلَىٰ رَبِّنَا لَمُنقَلِبُونَ",
            banglaPronunciation = "সুবহানাল্লাযী সাখখারা লানা হাযা ওয়ামা কুন্না লাহু মুক্বরিনীনা, ওয়া ইন্না ইলা রব্বিনা লামুনক্বালিবূন।",
            banglaMeaning = "পবিত্র ও মহান সেই সত্তা, যিনি একে আমাদের অধীনস্থ করে দিয়েছেন; অথচ আমরা একে বশীভূত করতে সক্ষম ছিলাম না। আর নিশ্চয়ই আমরা আমাদের রবের দিকেই প্রত্যাবর্তনকারী।",
            englishTranslation = "Exalted is He who has subjected this to us, and we could not have [otherwise] subdued it. And indeed we, to our Lord, will return.",
            englishTransliteration = "Subhanal-ladhi sakh-khara lana hadha wa ma kunna lahu muqrineen, wa inna ila Rabbina lamunqaliboon.",
            reference = "সূরা আয-যুখরুফ: ১৩-১৪; সহীহ মুসলিম: ১৩৪২",
            virtueBn = "গাড়ি, বাস, ট্রেন, বিমান বা যেকোনো বাহনে চড়ে এই আয়াত ও দোয়া পাঠ করলে ভ্রমণ নিরাপদ ও দুর্ঘটনামুক্ত হয়।",
            virtueEn = "Recited when boarding any conveyance, ensuring a safe and blessed journey.",
            targetCount = 1,
            isRuqyah = false
        ),
        DuaItem(
            id = "tf_barakah_sustenance",
            titleBn = "উপকারী ইলম, পবিত্র রিজিক ও কবুল আমলের দোয়া",
            titleEn = "Dua for Beneficial Knowledge, Good Sustenance & Accepted Deeds",
            titleAr = "دعاء طلب الرزق الطيب والعلم النافع",
            category = DuaCategory.TRAVEL_FOOD,
            arabicText = "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا، وَرِزْقًا طَيِّبًا، وَعَمَلًا مُتَقَبَّلًا",
            banglaPronunciation = "আল্লাহুম্মা ইন্নী আসআলুকা 'ইলমান নাফি'আওঁ, ওয়া রিযক্বান ত্বাইয়্যিবাওঁ, ওয়া 'আমালান মুতাক্বাব্বালা।",
            banglaMeaning = "হে আল্লাহ! আমি আপনার নিকট প্রার্থনা করছি উপকারী জ্ঞান, পবিত্র হালাল রিজিক এবং গ্রহণযোগ্য নেক আমল।",
            englishTranslation = "O Allah, I ask You for beneficial knowledge, good and pure provision, and deeds that are accepted.",
            englishTransliteration = "Allahumma inni as'aluka 'ilman nafi'a, wa rizqan tayyiba, wa 'amalan mutaqabbala.",
            reference = "সুনান ইবনে মাজাহ: ৯২৫; মুসনাদে আহমাদ: ২৬৫২১ (সহীহ)",
            virtueBn = "উম্মে সালামা (রা.) বলেন, রাসুলুল্লাহ (সা.) ফজরের নামাজের সালাম ফেরানোর পর প্রতিদিন এই দোয়াটি পাঠ করতেন।",
            virtueEn = "Recited by Prophet Muhammad (PBUH) every day after the morning prayer (Fajr).",
            targetCount = 1,
            isRuqyah = false
        ),

        // ================= SPECIAL OCCASIONS =================
        DuaItem(
            id = "so_iftar",
            titleBn = "রোজার ইফতারের সুন্নতি দোয়া",
            titleEn = "Dua for Breaking the Fast (Iftar)",
            titleAr = "دعاء الإفطار للصائم",
            category = DuaCategory.SPECIAL_OCCASIONS,
            arabicText = "ذَهَبَ الظَّمَأُ، وَابْتَلَّتِ الْعُرُوقُ، وَثَبَتَ الْأَجْرُ إِنْ شَاءَ اللَّهُ",
            banglaPronunciation = "যাহাবায যামা-উ, ওয়াবতাল্লাতিল 'উরূকু, ওয়া ছাবাতাল আজরু ইনশাআল্লাহ।",
            banglaMeaning = "পিপাসা দূরীভূত হলো, শিরা-উপশিরা সিক্ত হলো এবং ইনশাআল্লাহ প্রতিদানও সুনিশ্চিত হলো।",
            englishTranslation = "The thirst has gone, the veins are moistened, and the reward is confirmed, if Allah wills.",
            englishTransliteration = "Dhahabadh-dhama'u, wabtallatil-'urooqu, wa thabatal-ajru in sha Allah.",
            reference = "সুনান আবু দাউদ: ২৩৫৭; সুনান দারাকুতনী: ২২৭৯ (হাসান সহীহ)",
            virtueBn = "ইফতারের ঠিক পর পরই রাসুলুল্লাহ (সা.) এই দোয়া পাঠ করতেন। ইফতারের সময় দোয়া কবুল হওয়ার সুসংবাদ রয়েছে।",
            virtueEn = "Authentic Sunnah supplication recited immediately after breaking the fast.",
            targetCount = 1,
            isRuqyah = false
        ),
        DuaItem(
            id = "so_laylatul_qadr",
            titleBn = "লাইলাতুল কদরের শ্রেষ্ঠ দোয়া",
            titleEn = "Dua for Laylatul Qadr (Night of Power)",
            titleAr = "دعاء ليلة القدر",
            category = DuaCategory.SPECIAL_OCCASIONS,
            arabicText = "اللَّهُمَّ إِنَّكَ عَفُوٌّ تُحِبُّ الْعَفْوَ فَاعْفُ عَنِّي",
            banglaPronunciation = "আল্লাহুম্মা ইন্নাকা 'আফুউউন, তুহিব্বুল 'আফওয়া, ফা'ফু 'আন্নী।",
            banglaMeaning = "হে আল্লাহ! নিশ্চয় আপনি মহাক্ষমাশীল, আপনি ক্ষমা করতে ভালোবাসেন; অতএব আমাকে ক্ষমা করে দিন।",
            englishTranslation = "O Allah, You are Forgiving and love forgiveness, so forgive me.",
            englishTransliteration = "Allahumma innaka 'Afuwwun tuhibbul-'afwa fa'fu 'annee.",
            reference = "জামে আত-তিরমিযী: ৩৫১৩; সুনান ইবনে মাজাহ: ৩৮৫০ (সহীহ)",
            virtueBn = "আয়েশা (রা.) রাসুল (সা.)-কে জিজ্ঞেস করেছিলেন, আমি যদি জানতে পারি কোন রাতটি লাইলাতুল কদর, তবে আমি কী বলব? রাসুল (সা.) তাঁকে এই দোয়া পড়তে বললেন।",
            virtueEn = "Taught by Prophet Muhammad (PBUH) to Aisha (RA) for the Blessed Night of Decree.",
            targetCount = 7,
            isRuqyah = false
        )
    )
}
