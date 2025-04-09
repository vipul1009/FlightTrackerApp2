package com.example.flighttracker.ui

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.flighttracker.databinding.ActivityWebviewBinding

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra("FLIGHT_URL")

        // Set up the toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Flight Details"
        supportActionBar?.subtitle = url?.substringAfterLast("/")

        // Show progress bar
        binding.progressBar.visibility = View.VISIBLE

        binding.webView.apply {
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    binding.progressBar.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    // Hide progress bar when page loads
                    binding.progressBar.visibility = View.GONE
                }
            }
            loadUrl(url ?: "https://www.flightaware.com")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}