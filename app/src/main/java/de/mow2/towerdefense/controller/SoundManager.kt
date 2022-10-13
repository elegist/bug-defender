package de.mow2.towerdefense.controller

import android.content.Context
import android.media.MediaPlayer
import android.view.View
import de.mow2.towerdefense.R


object SoundManager {
    lateinit var mediaPlayer: MediaPlayer

    // 1. start music
    fun initMediaPlayer(context: Context, song: Int) {
        mediaPlayer = MediaPlayer.create(context, song)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }
}