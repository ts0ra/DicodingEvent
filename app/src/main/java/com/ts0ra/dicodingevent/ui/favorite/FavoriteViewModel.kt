package com.ts0ra.dicodingevent.ui.favorite

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ts0ra.dicodingevent.database.FavoriteEvent
import com.ts0ra.dicodingevent.repository.EventRepository


class FavoriteViewModel(application: Application) : ViewModel() {
    private val mEventRepository: EventRepository = EventRepository(application)
    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>> = mEventRepository.getAllFavoriteEvents()


}