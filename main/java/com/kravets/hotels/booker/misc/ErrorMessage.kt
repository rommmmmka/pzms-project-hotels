package com.kravets.hotels.booker.misc

import com.kravets.hotels.booker.R

object ErrorMessage {
    fun getStringId(code: Int): Int {
        return when (code) {
            1 -> R.string.error_connecting_to_server
            403 -> R.string.error_no_access
            460 -> R.string.error_invalid_filters
            461 -> R.string.error_form_not_valid
            462 -> R.string.error_no_free_rooms
            463 -> R.string.error_order_does_not_exist
            464 -> R.string.error_user_already_exists
            465 -> R.string.error_user_not_found
            466 -> R.string.error_invalid_password
            else -> R.string.error_unknown
        }
    }
}