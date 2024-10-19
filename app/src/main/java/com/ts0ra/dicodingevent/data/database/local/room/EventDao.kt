package com.ts0ra.dicodingevent.data.database.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ts0ra.dicodingevent.data.database.local.entity.EventEntity
import com.ts0ra.dicodingevent.data.database.local.entity.FavoriteEvent

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(listEvent: List<EventEntity>)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Query("SELECT * FROM events ORDER BY id DESC")
    fun getAllEvent(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM events WHERE upcoming = 1 ORDER BY id DESC")
    fun getUpcomingEvent(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM events WHERE finished = 1 ORDER BY id DESC")
    fun getFinishedEvent(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM events WHERE favorite = 1 ORDER BY id DESC")
    fun getFavoriteEvent(): LiveData<List<EventEntity>>

    @Query("SELECT EXISTS(SELECT * FROM events WHERE id = :id AND favorite = 1)")
    suspend fun isEventFavorite(id: Int): Boolean

    @Query("SELECT EXISTS(SELECT * FROM events WHERE id = :id AND upcoming = 1)")
    suspend fun isEventUpcoming(id: Int): Boolean

    @Query("SELECT EXISTS(SELECT * FROM events WHERE id = :id AND finished = 1)")
    suspend fun isEventFinished(id: Int): Boolean

    @Query("SELECT * FROM events WHERE name LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchEvent(query: String): LiveData<List<EventEntity>>
}