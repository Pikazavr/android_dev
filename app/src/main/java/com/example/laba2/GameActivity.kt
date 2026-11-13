
package com.example.laba2

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class GameActivity : AppCompatActivity() {

    private fun vibratePhone() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE)
                        as VibratorManager
                manager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (vibrator.hasVibrator()) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        300,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TicTacToeScreen(
                        onLose = { vibratePhone() },
                        onBack = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun TicTacToeScreen(
    onLose: () -> Unit,
    onBack: () -> Unit
) {
    var board by remember {
        mutableStateOf(List(9) { "" })
    }

    var currentPlayer by remember {
        mutableStateOf("X")
    }

    var winner by remember {
        mutableStateOf<String?>(null)
    }

    var isDraw by remember {
        mutableStateOf(false)
    }

    val winningCombinations = listOf(
        listOf(0, 1, 2),
        listOf(3, 4, 5),
        listOf(6, 7, 8),
        listOf(0, 3, 6),
        listOf(1, 4, 7),
        listOf(2, 5, 8),
        listOf(0, 4, 8),
        listOf(2, 4, 6)
    )

    fun checkWinner(): String? {
        for (combination in winningCombinations) {
            val first = board[combination[0]]

            if (
                first.isNotEmpty() &&
                first == board[combination[1]] &&
                first == board[combination[2]]
            ) {
                return first
            }
        }

        return null
    }

    fun makeMove(index: Int) {
        if (board[index].isNotEmpty() || winner != null || isDraw) {
            return
        }

        board = board.toMutableList().also {
            it[index] = currentPlayer
        }

        val result = checkWinner()

        if (result != null) {
            winner = result

            // Вибрация при проигрыше второго игрока
            onLose()
        } else if (board.all { it.isNotEmpty() }) {
            isDraw = true
        } else {
            currentPlayer = if (currentPlayer == "X") "O" else "X"
        }
    }

    fun restartGame() {
        board = List(9) { "" }
        currentPlayer = "X"
        winner = null
        isDraw = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Крестики-нолики",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        when {
            winner != null -> {
                Text(
                    text = "Победил игрок $winner!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }

            isDraw -> {
                Text(
                    text = "Ничья!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            else -> {
                Text(
                    text = "Ход игрока: $currentPlayer",
                    fontSize = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        for (row in 0..2) {
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                for (column in 0..2) {
                    val index = row * 3 + column

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(95.dp)
                            .background(
                                color = Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                makeMove(index)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = board[index],
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (board[index] == "X") {
                                Color(0xFF1565C0)
                            } else {
                                Color(0xFFC62828)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { restartGame() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Новая игра")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Вернуться в меню")
        }
    }
}
