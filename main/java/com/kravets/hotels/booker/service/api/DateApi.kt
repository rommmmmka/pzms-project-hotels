package com.kravets.hotels.booker.service.api

import retrofit2.Response
import retrofit2.http.GET
import java.time.LocalDate

interface DateApi {
    @GET("api/date/get_server_date")
    suspend fun getServerDate(): Response<Map<String, String>>
}