package com.kravets.hotels.booker.model.entity

data class HotelEntity(
    val id: Long,
    val name: String,
    val description: String,
    val coverPhoto: String,
    val city: CityEntity,
    val roomsCount: Long
)
