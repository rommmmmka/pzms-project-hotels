package com.kravets.hotels.booker.entity

import java.time.LocalDate

data class SearchForm(
    val currentServerDate: LocalDate,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    var adultsCount: Int = 1,
    var childrenCount: Int = 0,
) {

    fun validate(): HashSet<String> {
        val errors: HashSet<String> = HashSet()
        if (checkInDate >= checkOutDate)
            errors.add("error_check_in_date_after_check_out")
        if (checkInDate < currentServerDate || checkOutDate < currentServerDate)
            errors.add("error_check_in_date_before_current_date")
        if (adultsCount < 1 || adultsCount > 30)
            errors.add("error_adults_count")
        if (childrenCount < 0 || childrenCount > 30)
            errors.add("error_children_count")
        return errors
    }
}