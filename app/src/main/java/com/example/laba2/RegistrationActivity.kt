
package com.example.laba2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.BitmapFactory

class RegistrationActivity : AppCompatActivity() {

    private val preferences by lazy {
        getSharedPreferences("user_profile", MODE_PRIVATE)
    }

    private var selectedAvatarUri by mutableStateOf<String?>(null)
    private var onAvatarSelected: ((String) -> Unit)? = null

    private val avatarPicker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    Log.e(
                        "RegistrationActivity",
                        "Не удалось сохранить доступ к аватару",
                        e
                    )
                }

                selectedAvatarUri = uri.toString()
                preferences.edit()
                    .putString("avatarUri", uri.toString())
                    .apply()

                onAvatarSelected?.invoke(uri.toString())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedAvatarUri = preferences.getString("avatarUri", null)

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegistrationScreen(
                        savedLogin = preferences.getString("login", "") ?: "",
                        savedFullName = preferences.getString("fullName", "") ?: "",
                        savedBirthDate = preferences.getString("birthDate", "") ?: "",
                        savedGender = preferences.getString("gender", "") ?: "",
                        savedAvatarUri = selectedAvatarUri,
                        onChooseAvatar = { callback ->
                            onAvatarSelected = callback
                            avatarPicker.launch(arrayOf("image/*"))
                        },
                        onRegister = { login, password, fullName, birthDate, gender, avatarUri ->
                            if (
                                login.isBlank() ||
                                password.isBlank() ||
                                fullName.isBlank()
                            ) {
                                Toast.makeText(
                                    this,
                                    R.string.fill_registration_fields,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val userStorage = UserStorage(this)

                                val user = User(
                                    login = login.trim(),
                                    fullName = fullName.trim(),
                                    birthDate = birthDate.trim(),
                                    gender = gender,
                                    avatarUri = avatarUri
                                )

                                if (!userStorage.addUser(user)) {
                                    Toast.makeText(
                                        this,
                                        "Такой логин уже зарегистрирован",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    preferences.edit()
                                        .putString("login", login.trim())
                                        .putString("password", password)
                                        .putString("fullName", fullName.trim())
                                        .putString("birthDate", birthDate.trim())
                                        .putString("gender", gender)
                                        .putString("avatarUri", avatarUri)
                                        .apply()

                                    Toast.makeText(
                                        this,
                                        R.string.register_success,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    startActivity(
                                        Intent(
                                            this@RegistrationActivity,
                                            MenuActivity::class.java
                                        ).putExtra("login", login.trim())
                                    )
                                    finish()
                                }
                            }
                        },
                        onBack = { finish() }
                    )
                }
            }
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

@Composable
fun RegistrationScreen(
    savedLogin: String,
    savedFullName: String,
    savedBirthDate: String,
    savedGender: String,
    savedAvatarUri: String?,
    onChooseAvatar: (((String) -> Unit) -> Unit),
    onRegister: (
        String, String, String, String, String, String?
    ) -> Unit,
    onBack: () -> Unit
) {
    var login by rememberSaveable { mutableStateOf(savedLogin) }
    var password by rememberSaveable { mutableStateOf("") }
    var fullName by rememberSaveable { mutableStateOf(savedFullName) }
    var birthDate by rememberSaveable { mutableStateOf(savedBirthDate) }
    var gender by rememberSaveable { mutableStateOf(savedGender) }
    var avatarUri by remember { mutableStateOf(savedAvatarUri) }

    val context = LocalContext.current

    val avatarBitmap = remember(avatarUri) {
        try {
            avatarUri?.let { uri ->
                context.contentResolver.openInputStream(Uri.parse(uri))?.use {
                    BitmapFactory.decodeStream(it)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Регистрация", fontSize = 26.sp)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Логин") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("ФИО") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = birthDate,
            onValueChange = { birthDate = it },
            label = { Text("Дата рождения") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Например, 01.01.2000") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Пол", fontSize = 18.sp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = gender == "Мужской",
                onClick = { gender = "Мужской" }
            )
            Text("Мужской")

            Spacer(modifier = Modifier.size(16.dp))

            RadioButton(
                selected = gender == "Женский",
                onClick = { gender = "Женский" }
            )
            Text("Женский")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Аватар", fontSize = 18.sp)

        if (avatarBitmap != null) {
            Image(
                bitmap = avatarBitmap.asImageBitmap(),
                contentDescription = "Выбранный аватар",
                modifier = Modifier.size(120.dp)
            )
        } else {
            Text("Аватар не выбран")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                onChooseAvatar { uri -> avatarUri = uri }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Выбрать аватар")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                onRegister(
                    login,
                    password,
                    fullName,
                    birthDate,
                    gender.ifBlank { "Не указан" },
                    avatarUri
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Зарегистрироваться")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Назад")
        }
    }
}
