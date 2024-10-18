package com.ts0ra.dicodingevent.data.retrofit

import com.ts0ra.dicodingevent.data.reponse.EventObject
import com.ts0ra.dicodingevent.data.reponse.EventResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("events")
    fun getEvent(
        @Query("active") int: Int
    ): Call<EventResponse>

    @GET("events/{id}")
    fun getDetailEvent(
        @Path("id") id: String
    ): Call<EventObject>

    @GET("events")
    fun searchEvent(
        @Query("active") int: Int,
        @Query("q") query: String
    ): Call<EventResponse>

    @GET("events")
    fun getLatestEvent(
        @Query("active") active: Int = -1,
        @Query("limit") limit: Int = 1
    ): Call<EventResponse>
}