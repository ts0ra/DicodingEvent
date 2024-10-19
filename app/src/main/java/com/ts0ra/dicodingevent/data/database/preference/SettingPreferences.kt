package com.ts0ra.dicodingevent.data.database.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    fun getData() = dataStore

    companion object {
        @Volatile
        private var instance: SettingPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences =
            instance ?: synchronized(this) {
                instance ?: SettingPreferences(dataStore)
            }.also { instance = it }
    }
}