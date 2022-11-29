package com.kravets.hotels.booker.service.api_object

import com.kravets.hotels.booker.Config
import com.kravets.hotels.booker.model.entity.RoomEntity
import com.kravets.hotels.booker.service.api.RoomApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RoomApiObject {
    private var roomApi: RoomApi? = null

    private fun getInstance(): RoomApi {
        if (roomApi == null) {
            roomApi = Retrofit
                .Builder()
                .baseUrl(Config.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RoomApi::class.java)
        }
        return roomApi!!
    }

    suspend fun searchRooms(
        city: Long, adultsCount: Int, childrenCount: Int, checkInDate: String, checkOutDate: String
    ): List<RoomEntity> {
        val response = getInstance().searchRooms(
            city, adultsCount, childrenCount, checkInDate, checkOutDate
        )
        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList()
        }
    }
}