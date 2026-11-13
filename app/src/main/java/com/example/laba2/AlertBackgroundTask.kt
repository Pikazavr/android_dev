
package com.example.laba2

import android.os.Handler
import android.os.Message

class AlertBackgroundTask(
    private val handler: Handler,
    private val delayMillis: Long = 5000L
) {
    @Volatile
    private var isRunning = false

    private var workerThread: Thread? = null

    fun start() {
        if (isRunning) return

        isRunning = true

        workerThread = Thread {
            while (isRunning) {
                try {
                    Thread.sleep(delayMillis)

                    if (isRunning) {
                        val message: Message =
                            handler.obtainMessage(2)

                        message.sendToTarget()
                    }
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    break
                }
            }
        }.apply {
            name = "AlertBackgroundThread"
            start()
        }
    }

    fun stop() {
        isRunning = false
        workerThread?.interrupt()
        workerThread = null
    }
}
