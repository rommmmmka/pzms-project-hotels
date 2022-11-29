package com.kravets.hotels.booker.model.entity

data class RoomEntity(
    val id: Long,
    val name: String,
    val description: String,
    val coverPhoto: String,
    val guestsLimit: Int,
    val adultsLimit: Int,
    val costPerNight: Int,
    val bedsForOnePersonCount: Int,
    val bedsForTwoPersonsCount: Int,
    val roomsCount: Int,
    val prepaymentRequired: Boolean,
    val freeRoomsLeft: Int,
    val hotel: HotelEntity
)