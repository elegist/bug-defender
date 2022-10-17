package de.mow2.towerdefense.controller

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import de.mow2.towerdefense.R
import kotlinx.android.synthetic.main.popup_view.*
import kotlinx.android.synthetic.main.popup_view.view.*


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
        Log.i("Test", tag.toString())

        //decide which fragment will be inflated
        when(tag.toString()) {
            "aboutDialog" -> {
                popupFragmentContainer.visibility = View.GONE
                popupText.setText(R.string.about_text)
            }
            "infoDialog" -> {
                popupFragmentContainer.visibility = View.GONE
                popupText.setText(R.string.info_text)
            }
            "settingsDialog" -> {
                popupText.setText(R.string.preferences_text)
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.popupFragmentContainer, SettingsFragment())
                    .commit()
            }
        }

        popUpView.buttonPopup.setOnClickListener{
            dismiss()
        }
    }
}