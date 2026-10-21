
package com.example.laba2

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ProfileActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val profileName = findViewById<TextView>(R.id.profileName)
        val profileEmail = findViewById<TextView>(R.id.profileEmail)
        val profilePhone = findViewById<TextView>(R.id.profilePhone)
        val profileBackButton = findViewById<Button>(R.id.profileBackButton)

        val preferences = getSharedPreferences(
            "user_profile",
            MODE_PRIVATE
        )

        profileName.text = "ФИО: ${
            preferences.getString("fullName", "Не указано")
        }"

        profileEmail.text = "Логин: ${
            preferences.getString("login", "Не указан")
        }"

        profilePhone.text = "Дата рождения: ${
            preferences.getString("birthDate", "Не указана")
        }"

        profileBackButton.setOnClickListener {
            finish()
        }
    }
}
