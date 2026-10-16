
package com.example.laba2

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val prefs = getSharedPreferences("user_data", MODE_PRIVATE)

        val name = prefs.getString("name", "").orEmpty()
        val email = prefs.getString("email", "").orEmpty()
        val phone = prefs.getString("phone", "").orEmpty()

        findViewById<TextView>(R.id.profileName).text =
            "${getString(R.string.name_hint)}: $name"
        findViewById<TextView>(R.id.profileEmail).text =
            "${getString(R.string.email_hint)}: $email"
        findViewById<TextView>(R.id.profilePhone).text =
            "${getString(R.string.phone_hint)}: $phone"

        findViewById<Button>(R.id.profileBackButton).setOnClickListener {
            finish()
        }
    }
}
