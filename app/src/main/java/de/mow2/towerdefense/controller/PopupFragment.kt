package de.mow2.towerdefense.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import de.mow2.towerdefense.R
import kotlinx.android.synthetic.main.popup_view.*
import kotlinx.android.synthetic.main.popup_view.view.*

/**
 * Dialog Fragment for popup window in mainActivity (about, info, prefs)
 * inflates view onCreate, sets Settings for each popup window
 * gets preferences through childFragment
 * functionality for close button in popup view
 * */

class PopupFragment: DialogFragment() {
    private lateinit var popUpView: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        popUpView = inflater.inflate(R.layout.popup_view, container, false)
        return popUpView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //decide which fragment will be inflated
        when(tag.toString()) {
            "aboutDialog" -> {
                popupFragmentContainer.visibility = View.GONE
                leaveGameBtn.visibility = View.GONE
                pauseGameBtn.visibility = View.GONE
                menuDivider.visibility = View.GONE
                popupText.setText(R.string.about_text)
                popupTitleText.setText(R.string.about_button)
            }
            "infoDialog" -> {
                popupFragmentContainer.visibility = View.GONE
                leaveGameBtn.visibility = View.GONE
                pauseGameBtn.visibility = View.GONE
                menuDivider.visibility = View.GONE
                popupText.setText(R.string.info_text)
                popupTitleText.setText(R.string.info_button)
            }
            "settingsDialog" -> {
                popupTitleText.setText(R.string.preference_button)
                leaveGameBtn.visibility = View.GONE
                pauseGameBtn.visibility = View.GONE
                popupText.visibility = View.GONE
                menuDivider.visibility = View.GONE
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.popupFragmentContainer, SettingsFragment())
                    .commit()
            }
            "menuDialog" -> {
                popupTitleText.setText(R.string.preference_button)
                popupText.visibility = View.GONE
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.popupFragmentContainer, SettingsFragment())
                    .commit()
            }
        }

        popUpView.closeBtn.setOnClickListener{
            dismiss()
        }
    }
}