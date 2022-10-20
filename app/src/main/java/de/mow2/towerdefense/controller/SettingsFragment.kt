package de.mow2.towerdefense.controller

import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceFragmentCompat
import de.mow2.towerdefense.R

class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val musicCheck = findPreference<CheckBoxPreference>("music_pref")!!
        musicCheck.setOnPreferenceChangeListener{ _, newValue ->
            if(newValue as Boolean){
                SoundManager.resumeMusic()
            } else {
                SoundManager.pauseMusic()
            }
            true
        }
    }
}