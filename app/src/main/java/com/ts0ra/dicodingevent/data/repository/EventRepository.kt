package com.ts0ra.dicodingevent.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.ts0ra.dicodingevent.data.database.local.entity.EventEntity
import com.ts0ra.dicodingevent.data.database.local.room.EventDao
import com.ts0ra.dicodingevent.data.database.remote.retrofit.ApiService
import com.ts0ra.dicodingevent.utils.Result

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventsDao: EventDao
) {

    fun getEventList(active: Int?, query: String?, limit: Int?): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getListEvent(active, query, limit)
            val listEvents = response.listEvents
            val eventList = listEvents.map { event ->
                val isFavorite = eventsDao.isEventFavorite(event.id)
                val isUpcoming = active == 1
                val isFinished = active == 0
                EventEntity(
                    event.id,
                    event.summary,
                    event.mediaCover,
                    event.registrants,
                    event.imageLogo,
                    event.link,
                    event.description,
                    event.ownerName,
                    event.cityName,
                    event.quota,
                    event.name,
                    event.beginTime,
                    event.endTime,
                    event.category,
                    isUpcoming,
                    isFinished,
                    isFavorite
                )
            }
            eventsDao.insertEvent(eventList)
        } catch (e: Exception) {
            Log.e("EventRepository", "getEventList: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<EventEntity>>> =
            when (active) {
                1 -> eventsDao.getUpcomingEvent().map { Result.Success(it) }
                0 -> eventsDao.getFinishedEvent().map { Result.Success(it) }
                else -> eventsDao.getAllEvent().map { Result.Success(it) }
            }
        emitSource(localData)
    }

    fun getFavoriteEvent(): LiveData<List<EventEntity>> {
        return eventsDao.getFavoriteEvent()
    }

    suspend fun setFavoriteEvent(event: EventEntity) {
        eventsDao.updateEvent(event)
    }

    fun getDetailEvent(id: Int): LiveData<Result<EventEntity>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailEvent(id.toString())
            val event = response.event
            val isFavorite = eventsDao.isEventFavorite(id)
            val isUpcoming = eventsDao.isEventUpcoming(id)
            val isFinished = eventsDao.isEventFinished(id)
            val detailEvent = EventEntity(
                event.id,
                event.summary,
                event.mediaCover,
                event.registrants,
                event.imageLogo,
                event.link,
                event.description,
                event.ownerName,
                event.cityName,
                event.quota,
                event.name,
                event.beginTime,
                event.endTime,
                event.category,
                isUpcoming,
                isFinished,
                isFavorite
            )
            emit(Result.Success(detailEvent))
        } catch (e: Exception) {
            Log.e("EventRepository", "getDetailEvent: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun getLastUpcomingEvent(active: Int?, query: String?, limit: Int?): Result<EventEntity> {
        return try {
            val response = apiService.getListEvent(active, query, limit)
            val listEventItem = response.listEvents.first()
            val isFavorite = eventsDao.isEventFavorite(listEventItem.id)
            val isUpcoming = eventsDao.isEventUpcoming(listEventItem.id)
            val isFinished = eventsDao.isEventFinished(listEventItem.id)
            val event = EventEntity(
                listEventItem.id,
                listEventItem.summary,
                listEventItem.mediaCover,
                listEventItem.registrants,
                listEventItem.imageLogo,
                listEventItem.link,
                listEventItem.description,
                listEventItem.ownerName,
                listEventItem.cityName,
                listEventItem.quota,
                listEventItem.name,
                listEventItem.beginTime,
                listEventItem.endTime,
                listEventItem.category,
                isUpcoming,
                isFinished,
                isFavorite
            )
            Result.Success(event)
        } catch (e: Exception) {
            Log.e("EventRepository", "getDetailEvent: ${e.message.toString()} ")
            Result.Error(e.message.toString())
        }
    }

    fun searchEvent(query: String): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getListEvent(-1, query, null)
            val listEvents = response.listEvents
            val eventList = listEvents.map { event ->
                val isFavorite = eventsDao.isEventFavorite(event.id)
                val isUpcoming = eventsDao.isEventUpcoming(event.id)
                val isFinished = eventsDao.isEventFinished(event.id)
                EventEntity(
                    event.id,
                    event.summary,
                    event.mediaCover,
                    event.registrants,
                    event.imageLogo,
                    event.link,
                    event.description,
                    event.ownerName,
                    event.cityName,
                    event.quota,
                    event.name,
                    event.beginTime,
                    event.endTime,
                    event.category,
                    isUpcoming,
                    isFinished,
                    isFavorite
                )
            }
            eventsDao.insertEvent(eventList)
            emit(Result.Success(eventList))
        } catch (e: Exception) {
            Log.e("EventRepository", "getEventList: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            eventsDao: EventDao
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, eventsDao)
            }.also { instance = it }
    }
}