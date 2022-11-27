package de.mow2.towerdefense.controller

import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import de.mow2.towerdefense.R
import de.mow2.towerdefense.model.core.GameManager

/**
 * class SettingsFragment sets layout.xml for preferences
 * sets onPreferenceChangeListener for musicSettings and soundSettings for checkbox functionality
 * */
class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // background music // MediaPlayer
        findPreference<CheckBoxPreference>("music_pref")?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) {
                SoundManager.resumeMusic()
            } else {
                SoundManager.pauseMusic()
            }
            true
        }

        // sound sprites / SoundPool
        findPreference<CheckBoxPreference>("sound_pref")?.setOnPreferenceChangeListener { _, newValue ->
            if (!(newValue as Boolean)) {
                SoundManager.soundPool.release()
            } else {
                SoundManager.playSounds()
                context?.let { SoundManager.loadSounds(it) }
            }
            true
        }
    }
}