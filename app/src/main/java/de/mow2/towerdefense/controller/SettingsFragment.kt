package de.mow2.towerdefense.controller

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import de.mow2.towerdefense.R

class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

}