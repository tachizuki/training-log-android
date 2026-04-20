package com.traininglog.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    private val REMOTE_URL = "https://gregarious-bombolone-bdabaa.netlify.app/index.html"
    private val LOCAL_URL = "file:///android_asset/index.html"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            allowFileAccess = true
            cacheMode = WebSettings.LOAD_DEFAULT
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportZoom(false)
            displayZoomControls = false
            builtInZoomControls = false
        }

        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView, request: WebResourceRequest
            ): Boolean = false
        }

        // まずローカルのHTMLを即表示
        webView.loadUrl(LOCAL_URL)

        // バックグラウンドで最新HTMLを取得して更新
        fetchAndUpdateHtml()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) webView.goBack()
                else { isEnabled = false; onBackPressedDispatcher.onBackPressed() }
            }
        })
    }

    private fun fetchAndUpdateHtml() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val html = URL(REMOTE_URL).readText()
                withContext(Dispatchers.Main) {
                    val escaped = html
                        .replace("\\", "\\\\")
                        .replace("`", "\\`")
                    val js = """
                        (function() {
                            const saved = {};
                            for (let i = 0; i < localStorage.length; i++) {
                                const k = localStorage.key(i);
                                saved[k] = localStorage.getItem(k);
                            }
                            document.open();
                            document.write(`${'$'}{escaped}`);
                            document.close();
                            for (const [k, v] of Object.entries(saved)) {
                                localStorage.setItem(k, v);
                            }
                            if (typeof init === 'function') init();
                        })();
                    """.trimIndent()
                    webView.evaluateJavascript(js, null)
                }
            } catch (e: Exception) {
                // ネット接続なしの場合はローカル版をそのまま使用
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView.restoreState(savedInstanceState)
    }
}
