package dev.yahaveliyahu.hw1.utillities

import android.content.Context
import android.widget.Toast
import dev.yahaveliyahu.hw1.MainActivity


object ToastUtil {
    fun safeToast(context: Context, message: String) {
        if (context is MainActivity && context.isActive) {
            MainActivity.activeToast?.cancel()
            MainActivity.activeToast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            MainActivity.activeToast?.show()
        }
    }
}