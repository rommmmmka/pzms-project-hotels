package com.kravets.hotels.booker.model.form

import java.time.LocalDate

data class SearchForm(
    val currentServerDate: LocalDate,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    var adultsCount: Int = 1,
    var childrenCount: Int = 0,
)