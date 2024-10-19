package com.ts0ra.dicodingevent.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.ts0ra.dicodingevent.data.database.preference.SettingPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingRepository private constructor(private val dataStore: DataStore<Preferences>) {

    private val themeKey = booleanPreferencesKey("theme_setting")
    private val reminderKey = booleanPreferencesKey("reminder_setting")

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[themeKey] ?: false
        }
    }

    fun getReminderSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[reminderKey] ?: false
        }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[themeKey] = isDarkModeActive
        }
    }

    suspend fun saveReminderSetting(isReminderActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[reminderKey] = isReminderActive
        }
    }

    companion object {
        @Volatile
        private var instance: SettingRepository? = null

//        fun getInstance(dataStore: DataStore<Preferences>): SettingRepository {
//            return INSTANCE ?: synchronized(this) {
//                val instance = SettingRepository(dataStore)
//                INSTANCE = instance
//                instance
//            }
//        }

        fun getInstance(dataStore: DataStore<Preferences>): SettingRepository =
            instance ?: synchronized(this) {
                instance ?: SettingRepository(dataStore)
            }.also { instance = it }
    }
}