package com.kravets.hotels.booker.service.api_object

import com.kravets.hotels.booker.Config
import com.kravets.hotels.booker.service.api.OrderApi
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object OrderApiObject {
    private var orderApi: OrderApi? = null

    private fun getInstance(): OrderApi {
        if (orderApi == null) {
            orderApi = Retrofit
                .Builder()
                .baseUrl(Config.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OrderApi::class.java)
        }
        return orderApi!!
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
}