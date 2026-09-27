package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY savedAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE duaId = :duaId)")
    fun isBookmarked(duaId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE duaId = :duaId")
    suspend fun deleteBookmark(duaId: String)
}

@Dao
interface DuaCounterDao {
    @Query("SELECT * FROM dua_counters")
    fun getAllCounters(): Flow<List<DuaCounterEntity>>

    @Query("SELECT * FROM dua_counters WHERE duaId = :duaId LIMIT 1")
    fun getCounter(duaId: String): Flow<DuaCounterEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCounter(counter: DuaCounterEntity)

    @Query("DELETE FROM dua_counters WHERE duaId = :duaId")
    suspend fun resetCounter(duaId: String)
}

@Dao
interface TasbeehDao {
    @Query("SELECT * FROM tasbeeh_records ORDER BY id ASC")
    fun getAllTasbeeh(): Flow<List<TasbeehEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTasbeeh(tasbeeh: TasbeehEntity): Long

    @Query("UPDATE tasbeeh_records SET currentCount = :count, totalRounds = :rounds, lastUpdated = :timestamp WHERE id = :id")
    suspend fun updateCount(id: Long, count: Int, rounds: Int, timestamp: Long)

    @Query("DELETE FROM tasbeeh_records WHERE id = :id")
    suspend fun deleteTasbeeh(id: Long)
}
