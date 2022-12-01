package com.kravets.hotels.booker.service.api

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface OrderApi {
    @POST("api/order/add/")
    suspend fun addOrder(
        @Query("sessionKey") sessionKey: String,
        @Query("checkInDate") checkInDate: String,
        @Query("checkOutDate") checkOutDate: String,
        @Query("roomId") roomId: Long
    ): Response<Void>
}