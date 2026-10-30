package com.example.laba2

import androidx.room.withTransaction

class UserRepository(
    private val database: AppDatabase
) {

    private val userDao = database.userDao()

    // Регистрация пользователя
    // Первый зарегистрированный пользователь становится администратором
    suspend fun registerUser(user: User): Boolean {
        return database.withTransaction {

            val existingUser = userDao.getUserByLogin(user.login)

            if (existingUser != null) {
                return@withTransaction false
            }

            val isFirstUser = userDao.getUsersCount() == 0

            val newUser = user.copy(
                isAdmin = isFirstUser
            )

            val result = userDao.insertUser(newUser)

            result != -1L
        }
    }

    // Авторизация пользователя
    suspend fun loginUser(login: String, password: String): User? {
        val user = userDao.getUserByLogin(login)

        return if (user != null && user.password == password) {
            user
        } else {
            null
        }
    }

    // Получение всех пользователей
    suspend fun getUsers(): List<User> {
        return userDao.getAllUsers()
    }

    // Получение пользователя по ID
    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }

    // Назначение пользователя администратором
    suspend fun makeAdmin(userId: Int) {
        userDao.makeAdmin(userId)
    }

    // Получение списка администраторов
    suspend fun getAdmins(): List<User> {
        return userDao.getAdmins()
    }
}