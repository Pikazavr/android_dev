package com.example.laba2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val login: String,
    val fullName: String,
    val birthDate: String,
    val gender: String,
    val avatarUri: String?,

    val password: String = "",
    val isAdmin: Boolean = false
)