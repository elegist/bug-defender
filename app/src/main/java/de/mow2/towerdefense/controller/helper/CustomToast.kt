package de.mow2.towerdefense.controller.helper

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import com.google.android.material.snackbar.Snackbar
import de.mow2.towerdefense.R
import de.mow2.towerdefense.model.core.GameManager

class CustomToast(val context: Context, inflater: LayoutInflater, val parent: ConstraintLayout) {

    @SuppressLint("InflateParams")
    private val snackBarLayout: View = inflater.inflate(R.layout.toast, null)
    private val snackBar = Snackbar.make(parent, "", Snackbar.LENGTH_SHORT)
    private val snackbarLayout = snackBar.view as Snackbar.SnackbarLayout
    private val text = snackBarLayout.findViewById<TextView>(R.id.toast_text)
    private val image = snackBarLayout.findViewById<ImageView>(R.id.toast_icon)
    private val layout = snackBarLayout.findViewById<LinearLayout>(R.id.toast_type)
    private val params = snackBar.view.layoutParams as ViewGroup.MarginLayoutParams
    private val display = parent.resources.displayMetrics
    private val height = display.heightPixels
    private val width = display.widthPixels

    fun setUpSnackbar(type: String) {
        snackBar.view.setBackgroundResource(R.color.transparent)
        snackBar.animationMode = Snackbar.ANIMATION_MODE_FADE
        snackbarLayout.addView(snackBarLayout, 0)

        when(type){
            "wave" -> {
                text.text = buildString {
                    append(context.getString(R.string.wave))
                    append(" ")
                    append(GameManager.gameLevel+1)
                }
                image.setImageResource(R.drawable.time)
                params.setMargins(width/4, height/2, width/4, height/2)
                layout.setBackgroundResource(R.drawable.wave_toast_shape)
            }
            "money" -> {
                text.setText(R.string.moneyWarning)
                image.setImageResource(R.drawable.coins)
                params.setMargins(width/5, height/2, width/5, height/2)
                layout.setBackgroundResource(R.drawable.warning_toast_shape)
            }
        }
        snackBar.view.layoutParams = params
        snackBar.show()
    }
}
