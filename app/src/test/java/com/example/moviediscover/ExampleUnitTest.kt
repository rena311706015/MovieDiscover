package com.example.moviediscover

import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import org.junit.Test
import org.junit.Assert.*
import java.io.File
import java.io.FileOutputStream
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    // 1. 硬編碼憑證（Hardcoded Credential）
    private val hardcodedApiKey = "my_secret_api_key_1234"  // BAD: CodeQL 可偵測

    // 2. 明文加密金鑰（Hardcoded encryption key）
    fun encryptData(data: String): String {
        val key = "1234567890123456"  // BAD: 明文密鑰
        val secretKey = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encrypted = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    // 3. 不安全儲存資料（Insecure local storage）
    fun saveSensitiveData(context: Context, content: String) {
        val file = File(context.filesDir, "user_token.txt")  // BAD: 儲存敏感資訊到明文檔案
        val output = FileOutputStream(file)
        output.write(content.toByteArray())
        output.close()
    }

    // 4. WebView 加載非信任來源（WebView load URL from untrusted source）
    fun loadUntrustedWebView(webView: WebView, url: String) {
        webView.settings.javaScriptEnabled = true  // BAD: 與外部 URL 一起使用有風險
        webView.loadUrl(url)  // BAD: CodeQL 可偵測 WebView 安全問題
    }

    // 5. 未驗證的 Bundle 反序列化（Untrusted Bundle deserialization）
    class DangerousActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val value = intent.extras?.getString("user_input")  // BAD: 若來自外部，可能導致注入
            println("Received: $value")
        }
    }
}
