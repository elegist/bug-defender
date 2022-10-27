package de.mow2.towerdefense.controller

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import de.mow2.towerdefense.R
import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes

@SuppressLint("ViewConstructor")
class BuildButton(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, type: TowerTypes, level: Int) : AppCompatImageButton(context, attributeSet, defStyleAttr) {

    init {
        when(type) {
            TowerTypes.BLOCK -> {
                when(level) {
                    0 -> {this.setImageResource(R.drawable.tower_block)}
                    1 -> {this.setImageResource(R.drawable.tower_block1)}
                    else -> {}
                }
            }
            TowerTypes.SLOW -> {
                when(level) {
                    0 -> {this.setImageResource(R.drawable.tower_slow)}
                    1 -> {this.setImageResource(R.drawable.tower_slow1)}
                    else -> {}
                }
            }
            TowerTypes.AOE -> {
                when(level) {
                    0 -> {this.setImageResource(R.drawable.tower_aoe)}
                    1 -> {this.setImageResource(R.drawable.tower_aoe1)}
                    else -> {}
                }
            }
        }
    }
}