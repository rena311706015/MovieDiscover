package com.example.moviediscover.data

sealed class ToastState {
    data object Hidden : ToastState()
    class AddSuccess(movieId: Int) : ToastState()
    class AddFailed(movieId: Int) : ToastState()
    class RemoveSuccess(movieId: Int) : ToastState()
    class RemoveFailed(movieId: Int) : ToastState()
}

fun getToastMessage(state: ToastState) = when (state) {
    is ToastState.AddSuccess -> "Add Success"
    is ToastState.AddFailed -> "Add Failed"
    is ToastState.RemoveSuccess -> "Remove Success"
    is ToastState.RemoveFailed -> "Remove Failed"
    else -> ""
}