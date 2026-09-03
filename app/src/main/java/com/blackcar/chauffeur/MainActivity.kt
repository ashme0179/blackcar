package com.blackcar.chauffeur

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.blackcar.chauffeur.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val siteUrl by lazy { getString(R.string.site_url) }

    // Domains that should open inside the app's WebView.
    private val ownedHosts = listOf("blackcarchauffeurservices.com")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        setupWebView()
        binding.swipeRefresh.setOnRefreshListener { binding.webView.reload() }
        binding.retryButton.setOnClickListener { loadSite() }

        loadSite()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val webView = binding.webView
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val url = request.url
                val scheme = url.scheme

                // Let tel:, mailto:, sms:, geo: etc. open in their native handlers.
                if (scheme != "http" && scheme != "https") {
                    return openExternally(url)
                }

                // Keep the chauffeur service site inside the app; send anything
                // else (maps, socials, third-party booking) to the browser/app.
                val host = url.host ?: ""
                val isOwned = ownedHosts.any { host.contains(it) }
                return if (isOwned) {
                    false
                } else {
                    openExternally(url)
                }
            }

            override fun onPageStarted(view: WebView, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.loadProgress.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                binding.loadProgress.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: android.webkit.WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                if (request.isForMainFrame) {
                    showOfflineState()
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                binding.loadProgress.progress = newProgress
                if (newProgress >= 100) {
                    binding.loadProgress.visibility = View.GONE
                }
            }
        }
    }

    private fun openExternally(url: Uri): Boolean {
        return try {
            startActivity(Intent(Intent.ACTION_VIEW, url))
            true
        } catch (e: ActivityNotFoundException) {
            true // Swallow: no app to handle this scheme, avoid crashing/loading it inline.
        }
    }

    private fun loadSite() {
        if (isOnline()) {
            binding.offlineView.visibility = View.GONE
            binding.webView.visibility = View.VISIBLE
            binding.webView.loadUrl(siteUrl)
        } else {
            showOfflineState()
        }
    }

    private fun showOfflineState() {
        binding.loadProgress.visibility = View.GONE
        binding.swipeRefresh.isRefreshing = false
        binding.webView.visibility = View.GONE
        binding.offlineView.visibility = View.VISIBLE
    }

    private fun isOnline(): Boolean {
        val cm = getSystemService(ConnectivityManager::class.java) ?: return false
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
