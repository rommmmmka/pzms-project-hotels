package com.kravets.hotels.booker.service.api_object

import com.kravets.hotels.booker.model.entity.CityEntity
import com.kravets.hotels.booker.service.Config
import com.kravets.hotels.booker.service.api.CityApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CityApiObject {
    private var cityApi: CityApi? = null

    private fun getInstance() : CityApi {
        if (cityApi == null) {
            cityApi = Retrofit
                .Builder()
                .baseUrl(Config.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CityApi::class.java)
        }
        return cityApi!!
    }

    suspend fun getCitiesList(): List<CityEntity> {
        val response = getInstance().getCitiesList()
        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList()
        }
    }
}