package de.mow2.towerdefense.controller

import android.content.Context
import android.media.MediaPlayer
import androidx.preference.PreferenceManager


object SoundManager {
    lateinit var mediaPlayer: MediaPlayer
    var musicSetting: Boolean = true
   /* //tryout sound pool
    var soundPool: SoundPool.Builder? = null
    val soundId = 1

    //
    fun soundPool() {
        soundPool = SoundPool.Builder()
    }*/

    // 1. start music
    fun initMediaPlayer(context: Context, song: Int) {
        mediaPlayer = MediaPlayer.create(context, song)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    fun loadPreferences(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        musicSetting = preferences.getBoolean("music_pref", true)
    }

    // 2. pauses music
    fun pauseMusic() {
        if(mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    // 3 resumes music
    fun resumeMusic() {
        if(!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }
}