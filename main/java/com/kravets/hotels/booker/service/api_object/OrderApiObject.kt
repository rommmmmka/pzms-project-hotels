package com.kravets.hotels.booker.service.api_object

import com.google.gson.GsonBuilder
import com.kravets.hotels.booker.Config
import com.kravets.hotels.booker.model.entity.OrderEntity
import com.kravets.hotels.booker.service.api.OrderApi
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter


object OrderApiObject {
    private var orderApi: OrderApi? = null

    private fun getInstance(): OrderApi {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        if (orderApi == null) {
            orderApi = Retrofit
                .Builder()
                .baseUrl(Config.baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(OrderApi::class.java)
        }
        return orderApi!!
    }

    suspend fun getOrdersList(
        sessionKey: String,
        filterStatus: Long = 0
    ): Response<List<OrderEntity>> {
        return getInstance().getOrdersList(sessionKey, filterStatus)
    }

    suspend fun addOrder(
        sessionKey: String,
        checkInDate: LocalDate,
        checkOutDate: LocalDate,
        roomId: Long
    ): Response<Void> {
        return getInstance().addOrder(
            sessionKey,
            checkInDate.format(DateTimeFormatter.ISO_DATE),
            checkOutDate.format(DateTimeFormatter.ISO_DATE),
            roomId
        )
    }

    suspend fun removeOrder(
        sessionKey: String,
        id: Long
    ): Response<Void> {
        return getInstance().removeOrder(sessionKey, id)
    }
}