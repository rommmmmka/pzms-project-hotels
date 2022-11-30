package com.kravets.hotels.booker.model.other

data class UserInfo(
    val sessionKey: String,
    val shortName: String,
    val isAdmin: Boolean
)
