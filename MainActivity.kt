package com.mishaal.camrylauncher

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt

class MainActivity : Activity() {

    // الرياض افتراضيًا — غيّر الإحداثيات لمدينتك
    private val lat = 24.71
    private val lon = 46.68

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var tempView: TextView

    private val tempTask = object : Runnable {
        override fun run() {
            fetchTemperature()
            handler.postDelayed(this, 15 * 60 * 1000L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tempView = findViewById(R.id.temp)

        findViewById<View>(R.id.btnMaps).setOnClickListener {
            launch(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=")))
        }
        findViewById<View>(R.id.btnMusic).setOnClickListener {
            launch(Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_MUSIC))
        }
        findViewById<View>(R.id.btnPhone).setOnClickListener {
            launch(Intent(Intent.ACTION_DIAL))
        }
        findViewById<View>(R.id.btnApps).setOnClickListener {
            startActivity(Intent(this, AppsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
        handler.post(tempTask)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(tempTask)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    private fun hideSystemBars() {
        window.insetsController?.hide(WindowInsets.Type.systemBars())
    }

    private fun launch(intent: Intent) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, R.string.not_found, Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchTemperature() {
        Thread {
            try {
                val url = URL("https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m")
                val c = url.openConnection() as HttpURLConnection
                c.connectTimeout = 8000; c.readTimeout = 8000
                val body = c.inputStream.bufferedReader().use { it.readText() }
                val t = JSONObject(body).getJSONObject("current").getDouble("temperature_2m")
                handler.post { tempView.text = "${t.roundToInt()}°C" }
            } catch (_: Exception) {
                // يبقى آخر عرض
            }
        }.start()
    }
}
