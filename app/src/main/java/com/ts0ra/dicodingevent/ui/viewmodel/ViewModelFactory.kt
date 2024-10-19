package com.ts0ra.dicodingevent.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ts0ra.dicodingevent.data.database.preference.SettingPreferences
import com.ts0ra.dicodingevent.data.di.Injection
import com.ts0ra.dicodingevent.data.repository.EventRepository
import com.ts0ra.dicodingevent.data.repository.SettingRepository

class ViewModelFactory private constructor(
    private val eventRepository: EventRepository,
    private val settingRepository: SettingRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(eventRepository, settingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                val event = Injection.provideEventRepository(context)
                val preference = Injection.provideSettingRepository(context)
                instance ?: ViewModelFactory(event, preference)
            }.also { instance = it }

    }
}