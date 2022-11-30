package com.kravets.hotels.booker.ui.screen.view_model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class LoginPageViewModel(transferredLogin: String?) : ViewModel() {
    val login: MutableStateFlow<String> = MutableStateFlow(transferredLogin ?: "")
}