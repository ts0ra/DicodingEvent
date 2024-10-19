package com.ts0ra.dicodingevent.data.di

import android.content.Context
import com.ts0ra.dicodingevent.data.database.local.room.EventDatabase
import com.ts0ra.dicodingevent.data.database.preference.SettingPreferences
import com.ts0ra.dicodingevent.data.database.preference.dataStore
import com.ts0ra.dicodingevent.data.database.remote.retrofit.ApiConfig
import com.ts0ra.dicodingevent.data.repository.EventRepository
import com.ts0ra.dicodingevent.data.repository.SettingRepository

object Injection {
    fun provideEventRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.eventDao()
        return EventRepository.getInstance(apiService, dao)
    }

    fun provideSettingRepository(context: Context): SettingRepository {
        val dataStore = SettingPreferences.getInstance(context.dataStore)
        val setting = dataStore.getData()
        return SettingRepository.getInstance(setting)
    }
}