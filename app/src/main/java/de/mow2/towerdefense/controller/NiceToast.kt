package de.mow2.towerdefense.controller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.IntDef
import de.mow2.towerdefense.R


class NiceToast(context: Context?) : Toast(context) {

    @IntDef(MONEY, WAVE)
    annotation class LayoutType

    @IntDef(LENGTH_SHORT)

    annotation class Duration
    companion object {
        const val WAVE = 1
        const val MONEY = 2
        const val LENGTH_SHORT = Toast.LENGTH_SHORT

        fun makeText(
            context: Context,
            message: CharSequence?,
            @Duration duration: Int,
            @LayoutType type: Int,
        ): Toast {
            val toast = Toast(context)
            toast.duration = duration
            val layout: View = LayoutInflater.from(context).inflate(R.layout.toast, null, false)

            val toastText = layout.findViewById<TextView>(R.id.toast_text)
            val toastLayout = layout.findViewById<LinearLayout>(R.id.toast_type)
            val img = layout.findViewById<ImageView>(R.id.toast_icon)

            toastText.text = message
            when (type) {
                WAVE -> {
                    toastLayout.setBackgroundResource(R.drawable.wave_toast_shape)
                    img.setImageResource(R.drawable.time)
                }
                MONEY -> {
                    toastLayout.setBackgroundResource(R.drawable.warning_toast_shape)
                    img.setImageResource(R.drawable.coins)
                }
            }
            toast.setView(layout)
            return toast
        }
    }
}