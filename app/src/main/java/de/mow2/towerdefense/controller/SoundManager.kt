package de.mow2.towerdefense.controller

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.preference.PreferenceManager


object SoundManager {
    lateinit var mediaPlayer: MediaPlayer
    var musicSetting: Boolean = true
    lateinit var soundPool: SoundPool

    // SoundPool for Soundbites
    fun playSounds() {
        var audioattributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioattributes)
            .build()
    }

    // Music player for background music
    // init function
    fun initMediaPlayer(context: Context, song: Int) {
        mediaPlayer = MediaPlayer.create(context, song)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    // preference function for checkbox functionality
    fun loadPreferences(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        musicSetting = preferences.getBoolean("music_pref", true)
    }

    // function to pauses music
    fun pauseMusic() {
        if(mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    // function to resume music
    fun resumeMusic() {
        if(!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }
}