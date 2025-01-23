package com.example.moviediscover.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowConnectivityManager
import org.robolectric.shadows.ShadowNetworkCapabilities

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Config.OLDEST_SDK])
class NetworkStateUtilRobolectricTest {

    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var shadowConnectivityManager: ShadowConnectivityManager

    private val testDispatcher = StandardTestDispatcher()


    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        shadowConnectivityManager = Shadows.shadowOf(connectivityManager)

        shadowConnectivityManager.clearAllNetworks() // 清除所有模擬網路
        shadowConnectivityManager.setActiveNetworkInfo(null)
    }

    @Test
    fun `initConnectivityManager SHOULD set initial isConnectedState correctly when network is available`() = runTest(testDispatcher) {
        NetworkStateUtil.initConnectivityManager(context)

        val isConnected = NetworkStateUtil.isConnectedFlow.first()
        assertFalse(isConnected)
    }

    @Test
    fun `initConnectivityManager SHOULD set initial isConnectedState correctly when network is unavailable`() = runTest(testDispatcher) {

        NetworkStateUtil.initConnectivityManager(context)

        val isConnected = NetworkStateUtil.isConnectedFlow.first()
        assertFalse(isConnected)
    }

    @Test
    fun `networkCallback onAvailable SHOULD update isConnectedFlow to true`() = runTest(testDispatcher) {
        val network = mockk<Network>()
        NetworkStateUtil_AccessHelper.getPrivateIsConnectedFlow(NetworkStateUtil).value = false

        NetworkStateUtil_AccessHelper.getPrivateNetworkCallback(NetworkStateUtil).onAvailable(network)

        val isConnected = NetworkStateUtil.isConnectedFlow.first()
        assertTrue(isConnected)
    }

    @Test
    fun `networkCallback onLost SHOULD update isConnectedFlow to false`() = runTest(testDispatcher) {
        val network = mockk<Network>()
        NetworkStateUtil_AccessHelper.getPrivateIsConnectedFlow(NetworkStateUtil).value = true

        NetworkStateUtil_AccessHelper.getPrivateNetworkCallback(NetworkStateUtil).onLost(network)

        val isConnected = NetworkStateUtil.isConnectedFlow.first()
        assertFalse(isConnected)
    }

    @Test
    fun `registerNetworkCallback SHOULD call registerDefaultNetworkCallback on ConnectivityManager`() {
        NetworkStateUtil.registerNetworkCallback()

        assertFalse(shadowConnectivityManager.getNetworkCallbacks().isEmpty())
    }

    @Test
    fun `unregisterNetworkCallback SHOULD call unregisterNetworkCallback on ConnectivityManager`() {
        NetworkStateUtil.registerNetworkCallback()
        assertFalse(shadowConnectivityManager.getNetworkCallbacks().isEmpty())

        NetworkStateUtil.unregisterNetworkCallback()

        assertTrue(shadowConnectivityManager.getNetworkCallbacks().isEmpty())
    }

    @After
    fun tearDown() {
        if (shadowConnectivityManager.getNetworkCallbacks().isNotEmpty()) {
            NetworkStateUtil.unregisterNetworkCallback()
        }
    }
}

object NetworkStateUtil_AccessHelper {
    fun getPrivateNetworkCallback(util: NetworkStateUtil): ConnectivityManager.NetworkCallback {
        val field = util::class.java.getDeclaredField("networkCallback")
        field.isAccessible = true
        return field.get(util) as ConnectivityManager.NetworkCallback
    }

    fun getPrivateIsConnectedFlow(util: NetworkStateUtil): MutableStateFlow<Boolean> {
        val field = util::class.java.getDeclaredField("_isConnectedFlow")
        field.isAccessible = true
        return field.get(util) as MutableStateFlow<Boolean>
    }
}