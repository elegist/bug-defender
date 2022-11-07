package de.mow2.towerdefense.controller

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.preference.PreferenceManager
import de.mow2.towerdefense.R

/**
 * class SoundManager
 * for playing background music and in-game Sound Sprites
 * contains MediaPlayer and SoundPool and initializes music & sound settings
 * SoundPool functions: playSounds -> initializes SoundPool with settings
 *                      loadSounds -> load audio streams from R
 *                      variables in enum class with integer for ID to make code readable
 * MediaPlayer functions: initMediaPlayer -> initializes MediaPlayer, starts it with setting for Loop
 *                      pauseMusic & resumeMusic enables to start and stop background music
 * function loadPreferences sets music and soundSettings as Preferences for checkbox
 * */

enum class Sounds(var id: Int){
    HITSOUND(0), PUNCHSOUND(0), GAMEOVER(0)
}

object SoundManager {
    lateinit var mediaPlayer: MediaPlayer
    lateinit var soundPool: SoundPool
    var musicSetting: Boolean = true
    var soundSetting: Boolean = true

    // SoundPool
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

    fun loadSounds(context: Context) {
        Sounds.HITSOUND.id = soundPool.load(context, R.raw.hit_04, 1)
        Sounds.PUNCHSOUND.id = soundPool.load(context, R.raw.punch, 1)
        Sounds.GAMEOVER.id = soundPool.load(context, R.raw.gameover, 1)
    }

    // MediaPlayer
    fun initMediaPlayer(context: Context, song: Int) {
        mediaPlayer = MediaPlayer.create(context, song)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    fun pauseMusic() {
        if(mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    fun resumeMusic() {
        if(!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    // preference function for checkbox functionality
    fun loadPreferences(context: Context) {
        val musicPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val soundPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        musicSetting = musicPreferences.getBoolean("music_pref", true)
        soundSetting = soundPreferences.getBoolean("sound_pref", true)
    }

}