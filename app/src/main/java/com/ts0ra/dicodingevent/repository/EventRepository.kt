package com.ts0ra.dicodingevent.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.ts0ra.dicodingevent.database.FavoriteEvent
import com.ts0ra.dicodingevent.database.FavoriteEventDao
import com.ts0ra.dicodingevent.database.FavoriteEventDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class EventRepository(application: Application) {
    private val mEventsDao: FavoriteEventDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = FavoriteEventDatabase.getDatabase(application)
        mEventsDao = db.eventDao()
    }
    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>> = mEventsDao.getAllFavoriteEvents()
    fun getFavoriteEventById(id: Int): LiveData<FavoriteEvent> = mEventsDao.getFavoriteEventById(id)
    fun insert(event: FavoriteEvent) {
        executorService.execute { mEventsDao.insert(event) }
    }
    fun delete(event: FavoriteEvent) {
        executorService.execute { mEventsDao.delete(event) }
    }
}