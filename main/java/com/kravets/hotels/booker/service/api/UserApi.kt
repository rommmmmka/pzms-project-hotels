package com.kravets.hotels.booker.service.api

import com.kravets.hotels.booker.model.other.UserInfo
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {

    @POST("api/user/register/")
    suspend fun registerUser(
        @Query("login") login: String,
        @Query("password") password: String,
        @Query("lastName") lastname: String,
        @Query("firstName") firstname: String,
        @Query("patronymic") patronymic: String,
    ): Response<Void>

    @POST("api/user/login/")
    suspend fun loginUser(
        @Query("login") login: String,
        @Query("password") password: String,
        @Query("rememberMe") rememberMe: Boolean = true
    ): Response<UserInfo>
}