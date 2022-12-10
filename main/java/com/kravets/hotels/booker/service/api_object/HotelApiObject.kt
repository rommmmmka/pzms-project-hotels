package com.kravets.hotels.booker.service.api_object

import com.kravets.hotels.booker.Config
import com.kravets.hotels.booker.model.entity.HotelEntity
import com.kravets.hotels.booker.service.api.HotelApi
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HotelApiObject {
    private var hotelApi: HotelApi? = null

    private fun getInstance(): HotelApi {
        if (hotelApi == null) {
            hotelApi = Retrofit
                .Builder()
                .baseUrl(Config.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(HotelApi::class.java)
        }
        return hotelApi!!
    }

    suspend fun getHotelsList(
        filterCity: Long = 0,
        sortingProperty: String = "creationDate",
        sortingDirection: String = "descending"
    ) : Response<List<HotelEntity>> {
        return getInstance().getHotelsList(filterCity, sortingProperty, sortingDirection)
    }
}