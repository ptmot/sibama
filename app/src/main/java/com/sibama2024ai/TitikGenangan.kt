package com.sibama2024ai

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import java.io.IOException

class TitikGenangan : AppCompatActivity() {
    private lateinit var drawer: DrawerLayout
    private var stream_id: String? = null
    private var str_id_genangan: String? = null
    private lateinit var myWebView: WebView

    private lateinit var progressDialog: AlertDialog
    private val delayMillis: Long = 7000 // Delay dalam milidetik
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_cctv_titik_genangan)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val intent = intent

        str_id_genangan = intent.getStringExtra(konfigurasi.GET_ID_GENANGAN)
        stream_id = intent.getStringExtra(konfigurasi.GET_STREAM_ID)


        //showToast(this, stream_id!!);

        val myWebView: WebView = findViewById(R.id.webview)
        val webSettings: WebSettings = myWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.mediaPlaybackRequiresUserGesture = true // Allow automatic media playback
        webSettings.domStorageEnabled = true

        myWebView.webViewClient = WebViewClient()
        myWebView.webChromeClient = WebChromeClient()

        try {
            myWebView.loadUrl("http://stream.cctv.malangkota.go.id/WebRTCApp/play.html?name=$stream_id")
            showProgressDialog()
        } catch (e: IOException) {
            Toast.makeText(this, "Error Get Stream " + e.message, Toast.LENGTH_LONG).show()
         e.printStackTrace()
        }


    }

    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    private fun showProgressDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.progress_dialog, null)
        progressDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        progressDialog.show()

        // Menggunakan Handler untuk menutup dialog setelah delay
        handler.postDelayed({
            hideProgressDialog()
        }, delayMillis)

    }

    private fun hideProgressDialog() {
        if (::progressDialog.isInitialized && progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

}