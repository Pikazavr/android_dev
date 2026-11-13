package com.example.laba2

import android.os.Handler
import android.os.Looper
import android.os.Message
import kotlinx.coroutines.runBlocking

data class DatabaseTaskResult(
    val result: Any? = null,
    val error: String? = null
)

class DatabaseThreadTask(
    private val handler: Handler,
    private val delayMillis: Long = 3000L
) {

    fun execute(
        requestCode: Int,
        task: suspend () -> Any?
    ) {
        Thread {
            try {
                // Имитация задержки обращения к базе данных
                Thread.sleep(delayMillis)

                // Выполнение suspend-запроса в рабочем потоке
                val result = runBlocking {
                    task()
                }

                sendResult(
                    requestCode,
                    DatabaseTaskResult(result = result)
                )
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()

                sendResult(
                    requestCode,
                    DatabaseTaskResult(error = "Операция прервана")
                )
            } catch (e: Exception) {
                sendResult(
                    requestCode,
                    DatabaseTaskResult(
                        error = e.message ?: "Ошибка обращения к базе данных"
                    )
                )
            }
        }.apply {
            name = "DatabaseWorker-$requestCode"
            start()
        }
    }

    private fun sendResult(
        requestCode: Int,
        result: DatabaseTaskResult
    ) {
        val message: Message = handler.obtainMessage(
            requestCode,
            result
        )

        message.sendToTarget()
    }

    companion object {
        fun createMainHandle2r(
            callback: (Message) -> Unit
        ): Handler {
            return Handler(Looper.getMainLooper()) { message ->
                callback(message)
                true
            }
        }
    }
}