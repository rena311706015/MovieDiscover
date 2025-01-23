package com.example.moviediscover.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NetworkStateUtil {
    private lateinit var connectivityManager: ConnectivityManager

    private val _isConnectedFlow = MutableStateFlow(true)
    val isConnectedFlow: StateFlow<Boolean> = _isConnectedFlow

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isConnectedFlow.value = true
        }

        override fun onLost(network: Network) {
            _isConnectedFlow.value = false
        }
    }

    fun initConnectivityManager(context: Context) {
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val isConnected =
            activeNetwork != null && connectivityManager.getNetworkCapabilities(activeNetwork)
                ?.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                ) == true

        _isConnectedFlow.value = isConnected
    }

    fun registerNetworkCallback() {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    fun unregisterNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}