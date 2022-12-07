package com.kravets.hotels.booker.service.api

import com.kravets.hotels.booker.model.entity.OrderEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface OrderApi {
    @GET("api/order/get_list")
    suspend fun getOrdersList(
        @Query("sessionKey") sessionKey: String,
        @Query("filterStatus") filterStatus: Long
    ): Response<List<OrderEntity>>

    @POST("api/order/add/")
    suspend fun addOrder(
        @Query("sessionKey") sessionKey: String,
        @Query("checkInDate") checkInDate: String,
        @Query("checkOutDate") checkOutDate: String,
        @Query("roomId") roomId: Long
    ): Response<Void>

    @POST("api/order/remove/")
    suspend fun removeOrder(
        @Query("sessionKey") sessionKey: String,
        @Query("id") id: Long
    ): Response<Void>
}