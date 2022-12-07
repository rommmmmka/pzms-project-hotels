package com.kravets.hotels.booker.model.entity

import java.util.*

data class OrderEntity(
    val id: Long,
    val checkInDate: String,
    val checkOutDate: String,
    val expireDateTime: String,
    val cost: Long,
    val user: UserEntity,
    val room: RoomEntity,
    val status: StatusEntity

)
