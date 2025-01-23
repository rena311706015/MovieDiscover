package com.example.moviediscover.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _isSnackbarDismissManually = MutableStateFlow(false)
    val isSnackbarDismissManually = _isSnackbarDismissManually.asStateFlow()

    fun updateIsSnackbarDismissManually(value: Boolean) {
        _isSnackbarDismissManually.value = value
    }
}