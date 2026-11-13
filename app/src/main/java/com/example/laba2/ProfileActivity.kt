
package com.example.laba2

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private var profileUser by mutableStateOf<User?>(null)
    private var canMakeAdmin by mutableStateOf(false)

    private var currentUserId = -1
    private var targetUserId = -1

    private var cameraPhotoUri: Uri? = null

    // Выбор изображения из галереи
    private val avatarPicker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    Log.e("ProfileActivity", "Не удалось сохранить доступ к изображению", e)
                }

                saveAvatar(uri)
            }
        }

    // Съёмка фотографии камерой
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                cameraPhotoUri?.let { uri ->
                    saveAvatar(uri)
                }
            } else {
                Toast.makeText(
                    this,
                    "Съёмка отменена",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences("user_data", MODE_PRIVATE)
        currentUserId = preferences.getInt("current_user_id", -1)

        targetUserId = intent.getIntExtra("user_id", currentUserId)

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val user = profileUser

                    if (user == null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Загрузка профиля...")
                        }
                    } else {
                        ProfileScreen(
                            user = user,
                            canMakeAdmin = canMakeAdmin,
                            canEditAvatar = targetUserId == currentUserId,
                            onMakeAdmin = { makeAdmin() },
                            onChooseAvatar = {
                                avatarPicker.launch(arrayOf("image/*"))
                            },
                            onTakePhoto = { takePhoto() },
                            onBack = { finish() }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    // Создание файла и запуск камеры
    private fun takePhoto() {
        try {
            val avatarDirectory = File(filesDir, "avatars")

            if (!avatarDirectory.exists()) {
                avatarDirectory.mkdirs()
            }

            val photoFile = File(
                avatarDirectory,
                "avatar_${System.currentTimeMillis()}.jpg"
            )

            cameraPhotoUri = FileProvider.getUriForFile(
                this,
                "$packageName.fileprovider",
                photoFile
            )

            cameraPhotoUri?.let { uri ->
                cameraLauncher.launch(uri)
            }
        } catch (e: Exception) {
            Log.e("ProfileActivity", "Ошибка запуска камеры", e)

            Toast.makeText(
                this,
                "Не удалось открыть камеру",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Сохранение URI нового аватара в Room
    private fun saveAvatar(uri: Uri) {
        lifecycleScope.launch {
            try {
                val database = DatabaseProvider.getDatabase(this@ProfileActivity)

                database.userDao().updateAvatar(
                    targetUserId,
                    uri.toString()
                )

                profileUser = profileUser?.copy(
                    avatarUri = uri.toString()
                )

                Toast.makeText(
                    this@ProfileActivity,
                    "Аватар успешно обновлён",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Log.e("ProfileActivity", "Ошибка сохранения аватара", e)

                Toast.makeText(
                    this@ProfileActivity,
                    "Не удалось сохранить аватар",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun loadProfile() {
        if (targetUserId == -1 || currentUserId == -1) {
            Toast.makeText(
                this,
                "Не удалось определить пользователя",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                val database = DatabaseProvider.getDatabase(this@ProfileActivity)
                val dao = database.userDao()

                val targetUser = dao.getUserById(targetUserId)
                val currentUser = dao.getUserById(currentUserId)

                profileUser = targetUser

                canMakeAdmin =
                    currentUser?.isAdmin == true &&
                            targetUser != null &&
                            targetUser.id != currentUserId &&
                            !targetUser.isAdmin

                if (targetUser == null) {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Пользователь не найден",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Ошибка загрузки профиля", e)

                Toast.makeText(
                    this@ProfileActivity,
                    "Ошибка загрузки профиля",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun makeAdmin() {
        lifecycleScope.launch {
            try {
                val database = DatabaseProvider.getDatabase(this@ProfileActivity)

                val currentUser = database.userDao().getUserById(currentUserId)

                if (currentUser?.isAdmin != true) {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Недостаточно прав",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                database.userDao().makeAdmin(targetUserId)

                Toast.makeText(
                    this@ProfileActivity,
                    "Пользователь назначен администратором",
                    Toast.LENGTH_SHORT
                ).show()

                loadProfile()

            } catch (e: Exception) {
                Toast.makeText(
                    this@ProfileActivity,
                    "Не удалось изменить права пользователя",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

@Composable
fun ProfileScreen(
    user: User,
    canMakeAdmin: Boolean,
    canEditAvatar: Boolean,
    onMakeAdmin: () -> Unit,
    onChooseAvatar: () -> Unit,
    onTakePhoto: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val bitmap = remember(user.avatarUri) {
        try {
            user.avatarUri?.let { uriString ->
                context.contentResolver.openInputStream(
                    Uri.parse(uriString)
                )?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Профиль пользователя",
            fontSize = 26.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Аватар пользователя",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Text("👤", fontSize = 64.sp)
        }

        if (canEditAvatar) {
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onChooseAvatar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Выбрать из галереи")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onTakePhoto,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сделать фото камерой")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "ФИО: ${user.fullName}",
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Логин: ${user.login}",
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Дата рождения: ${user.birthDate}",
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Пол: ${user.gender}",
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (user.isAdmin) {
                "Роль: Администратор"
            } else {
                "Роль: Пользователь"
            },
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (canMakeAdmin) {
            Button(
                onClick = onMakeAdmin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сделать администратором")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Назад")
        }
    }
}
