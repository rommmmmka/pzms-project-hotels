package com.kravets.hotels.booker.service.api

import com.kravets.hotels.booker.model.entity.HotelEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HotelApi {
    @GET("api/hotel/get_list")
    suspend fun getHotelsList(
        @Query("filterCity") filterCity: Long,
        @Query("sortingProperty") sortingProperty: String,
        @Query("sortingDirection") sortingDirection: String
    ): Response<List<HotelEntity>>
}