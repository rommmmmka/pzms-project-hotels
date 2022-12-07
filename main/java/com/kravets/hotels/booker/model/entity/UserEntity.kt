package com.kravets.hotels.booker.model.entity

data class UserEntity(
    val id: Long,
    val login: String,
    val lastName: String,
    val firstName: String,
    val patronymic: String,
    val isAdmin: Boolean
)
