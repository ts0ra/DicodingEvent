package com.ts0ra.dicodingevent.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ts0ra.dicodingevent.data.reponse.EventResponse
import com.ts0ra.dicodingevent.data.reponse.ListEventsItem
import com.ts0ra.dicodingevent.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {
    private val _upcomingEvent = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvent: LiveData<List<ListEventsItem>> = _upcomingEvent
    private val _finishedEvent = MutableLiveData<List<ListEventsItem>>()
    val finishedEvent: LiveData<List<ListEventsItem>> = _finishedEvent
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _searchEvent = MutableLiveData<List<ListEventsItem>>()
    val searchEvent: LiveData<List<ListEventsItem>> = _searchEvent
    private val _eventSearchLoading = MutableLiveData<Boolean>()
    val eventSearchLoading: LiveData<Boolean> = _eventSearchLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    companion object{
        private const val HOME_VIEW_MODEL_UPCOMING = "HomeViewModelUpcoming"
        private const val HOME_VIEW_MODEL_FINISHED = "HomeViewModelFinished"
        private const val HOME_VIEW_MODEL_SEARCH = "HomeViewModelSearch"
    }

    init {
        findUpcomingEvents()
        findFinishedEvents()
    }

    fun clearSearch() {
        _searchEvent.value = emptyList()
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun searchEvent(query: String) {
        _eventSearchLoading.value = true
        val client = ApiConfig.getApiService().searchEvent(-1, query)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(
                call: Call<EventResponse>,
                response: Response<EventResponse>
            ) {
                _eventSearchLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val eventList = responseBody.listEvents
                        if (eventList.isNotEmpty()) {
                            _searchEvent.value = eventList
                        } else {
                            _errorMessage.value = "No events found for $query"
                        }
                    }
                } else {
                    Log.e(HOME_VIEW_MODEL_SEARCH, "onFailure: ${response.message()}")
                    _errorMessage.value = "Search failed: ${response.message()}"
                }
            }
            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _eventSearchLoading.value = false
                Log.e(HOME_VIEW_MODEL_SEARCH, "onFailure: ${t.message}")
                _errorMessage.value = "Network error: ${t.message}"
            }
        })
    }

    private fun findUpcomingEvents() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEvent(1) // TODO: Change this from 0 to 1 after testing
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(
                call: Call<EventResponse>,
                response: Response<EventResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _upcomingEvent.value = response.body()?.listEvents
                    }
                } else {
                    Log.e(HOME_VIEW_MODEL_UPCOMING, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(HOME_VIEW_MODEL_UPCOMING, "onFailure: ${t.message}")
                _errorMessage.value = "Network error: ${t.message}"
            }
        })
    }

    private fun findFinishedEvents() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEvent(0) // TODO: Change this from 0 to 1 after testing
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(
                call: Call<EventResponse>,
                response: Response<EventResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _finishedEvent.value = response.body()?.listEvents
                    }
                } else {
                    Log.e(HOME_VIEW_MODEL_FINISHED, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(HOME_VIEW_MODEL_FINISHED, "onFailure: ${t.message}")
                _errorMessage.value = "Network error: ${t.message}"
            }
        })
    }
}