package com.example.laba2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users ORDER BY id ASC")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE login = :login LIMIT 1")
    suspend fun getUserByLogin(login: String): User?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUsersCount(): Int

    @Query("UPDATE users SET isAdmin = 1 WHERE id = :userId")
    suspend fun makeAdmin(userId: Int)

    @Query("SELECT * FROM users WHERE isAdmin = 1")
    suspend fun getAdmins(): List<User>
}