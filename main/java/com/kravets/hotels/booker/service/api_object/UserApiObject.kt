package com.kravets.hotels.booker.service.api_object

import com.kravets.hotels.booker.Config
import com.kravets.hotels.booker.model.other.UserInfo
import com.kravets.hotels.booker.service.api.UserApi
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserApiObject {
    private var userApi: UserApi? = null

    private fun getInstance(): UserApi {
        if (userApi == null) {
            userApi = Retrofit
                .Builder()
                .baseUrl(Config.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserApi::class.java)
        }
        return userApi!!
    }

    suspend fun registerUser(
        login: String,
        password: String,
        lastname: String,
        firstname: String,
        patronymic: String
    ): Response<Void> {
        return getInstance().registerUser(login, password, lastname, firstname, patronymic)
    }

    suspend fun loginUser(login: String, password: String): Response<UserInfo> {
        return getInstance().loginUser(login, password)
    }


}