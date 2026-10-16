package com.example.laba2

import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("user_data", MODE_PRIVATE)
        val nightMode = prefs.getBoolean("night_mode", false)

        AppCompatDelegate.setDefaultNightMode(
            if (nightMode) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val group = findViewById<RadioGroup>(R.id.themeRadioGroup)
        val dayRadioId = R.id.dayRadio
        val nightRadioId = R.id.nightRadio

        group.setOnCheckedChangeListener(null)
        group.check(if (nightMode) nightRadioId else dayRadioId)

        group.setOnCheckedChangeListener { _, checkedId ->
            val useNight = when (checkedId) {
                nightRadioId -> true
                dayRadioId -> false
                else -> return@setOnCheckedChangeListener
            }

            prefs.edit()
                .putBoolean("night_mode", useNight)
                .apply()

            AppCompatDelegate.setDefaultNightMode(
                if (useNight) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }

        findViewById<Button>(R.id.settingsBackButton).setOnClickListener {
            finish()
        }
    }
}