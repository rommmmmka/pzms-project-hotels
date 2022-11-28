package com.kravets.hotels.booker.service.api_object

import com.google.gson.GsonBuilder
import com.kravets.hotels.booker.service.Config
import com.kravets.hotels.booker.service.api.DateApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter


object DateApiObject {
    private var dateApi: DateApi? = null

    private fun getInstance(): DateApi {
        if (dateApi == null) {
            dateApi = Retrofit
                .Builder()
                .baseUrl(Config.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DateApi::class.java)
        }
        return dateApi!!
    }

    suspend fun getServerDate(): LocalDate? {
        val response = getInstance().getServerDate()
        return if (response.isSuccessful) {
            LocalDate.parse(response.body()?.get("currentDate"), DateTimeFormatter.ISO_DATE)
        } else {
            null
        }
    }
}