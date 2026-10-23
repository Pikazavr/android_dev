
package com.example.laba2

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val preferences = context.getSharedPreferences(
        "user_data",
        Context.MODE_PRIVATE
    )
    val isDarkTheme = preferences.getBoolean("night_mode", false)

    val colors = if (isDarkTheme) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
