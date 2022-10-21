package de.mow2.towerdefense.controller

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.preference.PreferenceManager

object SoundManager {
    lateinit var mediaPlayer: MediaPlayer
    lateinit var soundPool: SoundPool
    var musicSetting: Boolean = true
    var soundSetting: Boolean = true

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

    fun loadSounds(){
        soundPool.load("res/raw/hit_04.ogg", 1)
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
        val musicPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val soundPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        musicSetting = musicPreferences.getBoolean("music_pref", true)
        soundSetting = soundPreferences.getBoolean("sound_pref", true)
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