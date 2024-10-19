package com.ts0ra.dicodingevent.data.database.remote.retrofit

import com.ts0ra.dicodingevent.data.database.remote.reponse.EventObject
import com.ts0ra.dicodingevent.data.database.remote.reponse.EventResponse
import retrofit2.http.*

interface ApiService {
    @GET("events")
    suspend fun getListEvent(
        @Query("active") active: Int?,
        @Query("q") query: String?,
        @Query("limit") limit: Int?
    ): EventResponse

    @GET("events/{id}")
    suspend fun getDetailEvent(
        @Path("id") id: String
    ): EventObject
}