package com.kravets.hotels.booker.service.api

import com.kravets.hotels.booker.model.entity.CityEntity
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface CityApi {
    @GET("api/city/get_list")
    suspend fun getCitiesList(): Response<List<CityEntity>>
}