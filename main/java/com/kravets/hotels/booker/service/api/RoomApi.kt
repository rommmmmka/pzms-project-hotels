package com.kravets.hotels.booker.service.api

import com.kravets.hotels.booker.model.entity.RoomEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RoomApi {
    
    @GET( "api/room/search/")
    suspend fun searchRooms(
        @Query("city") city: Long,
        @Query("adultsCount") adultsCount: Int,
        @Query("childrenCount") childrenCount: Int,
        @Query("checkInDate") checkInDate: String,
        @Query("checkOutDate") checkOutDate: String
    ): Response<List<RoomEntity>>
}