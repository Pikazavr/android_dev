
package com.example.laba2

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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import android.graphics.BitmapFactory
import kotlinx.coroutines.launch

class RegistrationActivity : AppCompatActivity() {

    private lateinit var userRepository: UserRepository

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

                onAvatarSelected?.invoke(uri.toString())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userRepository = UserRepository(
            DatabaseProvider.getDatabase(this)
        )

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegistrationScreen(
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
                                    this@RegistrationActivity,
                                    "Заполните логин, пароль и ФИО",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                lifecycleScope.launch {
                                    try {
                                        val user = User(
                                            login = login.trim(),
                                            fullName = fullName.trim(),
                                            birthDate = birthDate.trim(),
                                            gender = gender,
                                            avatarUri = avatarUri,
                                            password = password
                                        )

                                        val registered =
                                            userRepository.registerUser(user)

                                        if (!registered) {
                                            Toast.makeText(
                                                this@RegistrationActivity,
                                                "Такой логин уже зарегистрирован",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            // Получаем только что созданного пользователя
                                            val registeredUser =
                                                userRepository.loginUser(
                                                    login.trim(),
                                                    password
                                                )

                                            if (registeredUser != null) {
                                                // Сохраняем текущую авторизованную сессию
                                                getSharedPreferences(
                                                    "user_data",
                                                    MODE_PRIVATE
                                                ).edit()
                                                    .putInt(
                                                        "current_user_id",
                                                        registeredUser.id
                                                    )
                                                    .putString(
                                                        "current_login",
                                                        registeredUser.login
                                                    )
                                                    .putBoolean(
                                                        "is_logged_in",
                                                        true
                                                    )
                                                    .apply()

                                                Toast.makeText(
                                                    this@RegistrationActivity,
                                                    if (registeredUser.isAdmin) {
                                                        "Регистрация успешна! Вы администратор."
                                                    } else {
                                                        "Регистрация успешна!"
                                                    },
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                startActivity(
                                                    Intent(
                                                        this@RegistrationActivity,
                                                        MenuActivity::class.java
                                                    )
                                                )
                                                finish()
                                            } else {
                                                Toast.makeText(
                                                    this@RegistrationActivity,
                                                    "Пользователь создан, но не удалось выполнить вход",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Log.e(
                                            "RegistrationActivity",
                                            "Ошибка регистрации",
                                            e
                                        )

                                        Toast.makeText(
                                            this@RegistrationActivity,
                                            "Ошибка регистрации: ${e.localizedMessage}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
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
    onChooseAvatar: (((String) -> Unit) -> Unit),
    onRegister: (
        String, String, String, String, String, String?
    ) -> Unit,
    onBack: () -> Unit
) {
    var login by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var fullName by rememberSaveable { mutableStateOf("") }
    var birthDate by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var avatarUri by rememberSaveable { mutableStateOf<String?>(null) }

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
                onChooseAvatar { uri ->
                    avatarUri = uri
                }
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
