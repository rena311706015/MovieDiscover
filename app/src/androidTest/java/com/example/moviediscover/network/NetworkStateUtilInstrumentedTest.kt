package com.example.moviediscover.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NetworkStateUtilInstrumentedTest {

    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        NetworkStateUtil.initConnectivityManager(context)
        NetworkStateUtil.registerNetworkCallback()
    }

    @Test
    fun initConnectivityManager_shouldInitializeConnectivityManager() {
        assertNotNull(
            "ConnectivityManager should be initialized in NetworkStateUtil",
            NetworkStateUtil_AccessHelper.getPrivateConnectivityManager(NetworkStateUtil)
        )
    }

    @Test
    fun isConnectedFlow_reflectsInitialNetworkState() = runBlocking {
        // 獲取當前裝置的實際網路狀態
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        val expectedIsConnected = activeNetwork != null &&
                capabilities != null &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

        val actualIsConnected = NetworkStateUtil.isConnectedFlow.first()

        assertEquals(
            "isConnectedFlow should reflect the initial network state",
            expectedIsConnected,
            actualIsConnected
        )
    }

    @Test
    fun networkCallback_updatesFlowOnNetworkChange() = runBlocking {

        println("Current isConnectedFlow value: ${NetworkStateUtil.isConnectedFlow.value}")
        println("Please MANUALLY DISABLE the network (Wi-Fi/Mobile Data) on the device/emulator now.")

        val disconnectedState = withTimeoutOrNull(15000L) { // 15 秒超時
            NetworkStateUtil.isConnectedFlow.first { !it } // 等待 Flow 變為 false
        }

        if (disconnectedState == null) {
            println("Timeout: Network did not change to disconnected, or callback was not triggered.")
        }
        assertEquals(
            "Network should be disconnected after manual change",
            false,
            NetworkStateUtil.isConnectedFlow.value
        )

        println("Current isConnectedFlow value: ${NetworkStateUtil.isConnectedFlow.value}")
        println("Please MANUALLY ENABLE the network (Wi-Fi/Mobile Data) on the device/emulator now.")

        val connectedState = withTimeoutOrNull(15000L) {
            NetworkStateUtil.isConnectedFlow.first { it } // 等待 Flow 變為 true
        }

        if (connectedState == null) {
            println("Timeout: Network did not change to connected, or callback was not triggered.")
        }
        assertEquals(
            "Network should be connected after manual change",
            true,
            NetworkStateUtil.isConnectedFlow.value
        )
    }


    @After
    fun tearDown() {
        NetworkStateUtil.unregisterNetworkCallback()
    }
}

object NetworkStateUtil_AccessHelper {
    fun getPrivateConnectivityManager(util: NetworkStateUtil): ConnectivityManager? {
        return try {
            val field = util::class.java.getDeclaredField("connectivityManager")
            field.isAccessible = true
            field.get(util) as? ConnectivityManager
        } catch (e: NoSuchFieldException) {
            null
        } catch (e: IllegalAccessException) {
            null
        }
    }
}