package com.jayanthr.spendisense.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ViewModel for holding SMS details
class SmsViewModel : ViewModel() {
    // Mutable state flow to hold the SMS data
    private val _smsDetails = MutableStateFlow<String?>(null) // Use String for now, will update when parsing
    val smsDetails: StateFlow<String?> = _smsDetails // Expose it as StateFlow for the UI to observe

    // Method to update SMS details
    fun updateSmsDetails(smsContent: String) {
        _smsDetails.value = smsContent
    }
}
