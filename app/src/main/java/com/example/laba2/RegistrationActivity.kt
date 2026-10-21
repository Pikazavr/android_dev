package com.example.laba2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast

class RegistrationActivity : Activity() {

    private lateinit var avatarImage: ImageView
    private lateinit var preferences: android.content.SharedPreferences

    companion object {
        private const val PICK_AVATAR_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        preferences = getSharedPreferences("user_profile", MODE_PRIVATE)

        val loginInput = findViewById<EditText>(R.id.loginInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val fullNameInput = findViewById<EditText>(R.id.fullNameInput)
        val birthDateInput = findViewById<EditText>(R.id.birthDateInput)
        val genderGroup = findViewById<RadioGroup>(R.id.genderGroup)

        avatarImage = findViewById(R.id.avatarImage)

        val chooseAvatarButton =
            findViewById<Button>(R.id.chooseAvatarButton)
        val registerButton =
            findViewById<Button>(R.id.registerButton)

        loginInput.setText(preferences.getString("login", ""))
        fullNameInput.setText(preferences.getString("fullName", ""))
        birthDateInput.setText(preferences.getString("birthDate", ""))

        when (preferences.getString("gender", "")) {
            "Мужской" -> genderGroup.check(R.id.genderMale)
            "Женский" -> genderGroup.check(R.id.genderFemale)
        }

        preferences.getString("avatarUri", null)?.let { savedUri ->
            avatarImage.setImageURI(Uri.parse(savedUri))
        }

        chooseAvatarButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, PICK_AVATAR_REQUEST)
        }

        registerButton.setOnClickListener {
            val login = loginInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val fullName = fullNameInput.text.toString().trim()
            val birthDate = birthDateInput.text.toString().trim()

            if (login.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                Toast.makeText(
                    this,
                    R.string.fill_registration_fields,
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val gender = when (genderGroup.checkedRadioButtonId) {
                R.id.genderMale -> "Мужской"
                R.id.genderFemale -> "Женский"
                else -> "Не указан"
            }

            val userStorage = UserStorage(this)

            val user = User(
                login = login,
                fullName = fullName,
                birthDate = birthDate,
                gender = gender,
                avatarUri = preferences.getString("avatarUri", null)
            )

            if (!userStorage.addUser(user)) {
                Toast.makeText(
                    this,
                    "Такой логин уже зарегистрирован",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }


            Toast.makeText(
                this,
                R.string.register_success,
                Toast.LENGTH_SHORT
            ).show()

            val menuIntent = Intent(
                this@RegistrationActivity,
                MenuActivity::class.java
            )
            menuIntent.putExtra("login", login)
            startActivity(menuIntent)
            finish()
        }
    }

    @Deprecated("Используется для получения результата выбора изображения")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_AVATAR_REQUEST && resultCode == RESULT_OK) {
            val uri: Uri = data?.data ?: return

            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                Log.e("RegistrationActivity", "Не удалось сохранить доступ к аватару", e)
            }

            avatarImage.setImageURI(uri)

            preferences.edit()
                .putString("avatarUri", uri.toString())
                .apply()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("RegistrationActivity", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("RegistrationActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("RegistrationActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("RegistrationActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("RegistrationActivity", "onDestroy")
    }
}