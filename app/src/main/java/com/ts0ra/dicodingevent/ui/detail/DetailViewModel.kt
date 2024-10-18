package com.ts0ra.dicodingevent.ui.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ts0ra.dicodingevent.data.reponse.Event
import com.ts0ra.dicodingevent.data.reponse.EventObject
import com.ts0ra.dicodingevent.data.retrofit.ApiConfig
import com.ts0ra.dicodingevent.database.FavoriteEvent
import com.ts0ra.dicodingevent.repository.EventRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailViewModel(application: Application) : ViewModel() {
    private val _event = MutableLiveData<Event>()
    val event: LiveData<Event> = _event
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val mEventRepository: EventRepository = EventRepository(application)

    fun insert(event: FavoriteEvent) {
        mEventRepository.insert(event)
    }

    fun getFavoriteEventById(id: Int): LiveData<FavoriteEvent> = mEventRepository.getFavoriteEventById(id)

    fun delete(event: FavoriteEvent) {
        mEventRepository.delete(event)
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun getDetailEvent(eventId: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailEvent(eventId)
        client.enqueue(object : Callback<EventObject> {
            override fun onResponse(call: Call<EventObject>, response: Response<EventObject>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _event.value = response.body()?.event
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    _errorMessage.value = "Network error: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventObject>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
                _errorMessage.value = "Network error: ${t.message}"
            }
        })
    }

    companion object{
        private const val TAG = "DetailViewModel"
    }
}