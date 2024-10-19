package com.ts0ra.dicodingevent.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ts0ra.dicodingevent.data.database.local.entity.EventEntity
import com.ts0ra.dicodingevent.data.repository.EventRepository
import com.ts0ra.dicodingevent.data.repository.SettingRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val eventRepository: EventRepository,
    private val settingRepository: SettingRepository
) : ViewModel() {
    fun getUpcomingEvent() = eventRepository.getEventList(1, null, null)
    fun getFinishedEvent() = eventRepository.getEventList(0, null, null)
    fun getFavoriteEvent() = eventRepository.getFavoriteEvent()

//    fun searchEvent(query: String) = eventRepository.searchEvent(query)
    fun searchEvent(query: String) = eventRepository.searchEvent(query)

    fun addFavoriteEvent(event: EventEntity) {
        viewModelScope.launch {
            eventRepository.setFavoriteEvent(event)
        }
    }

    fun deleteFavoriteEvent(event: EventEntity) {
        viewModelScope.launch {
            eventRepository.setFavoriteEvent(event)
        }
    }

    fun getDetailEvent(id: Int) = eventRepository.getDetailEvent(id)

    fun getThemeSetting() = settingRepository.getThemeSetting().asLiveData()
    fun getReminderSetting() = settingRepository.getReminderSetting().asLiveData()

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            settingRepository.saveThemeSetting(isDarkModeActive)
        }
    }

    fun saveReminderSetting(isReminderActive: Boolean) {
        viewModelScope.launch {
            settingRepository.saveReminderSetting(isReminderActive)
        }
    }
}