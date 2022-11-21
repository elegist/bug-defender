package de.mow2.towerdefense.controller

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import de.mow2.towerdefense.R
import de.mow2.towerdefense.databinding.PopupViewBinding

/**
 * Dialog Fragment for popup window in mainActivity (about, info, prefs)
 * inflates view onCreate, sets Settings for each popup window
 * gets preferences through childFragment
 * functionality for close button in popup view
 **/
class PopupFragment: DialogFragment() {
    private var _binding: PopupViewBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PopupViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //decide which fragment will be inflated
        when(tag.toString()) {
            "aboutDialog" -> {
                binding.popupFragmentContainer.visibility = View.GONE
                binding.pauseGameBtn.visibility = View.GONE
                binding.menuDivider.visibility = View.GONE
                binding.tutorialBtn.visibility = View.GONE
                binding.popupText.setText(R.string.about_text)
                binding.popupTitleText.setText(R.string.about_button)
            }
            "infoDialog" -> {
                binding.popupFragmentContainer.visibility = View.GONE
                binding.pauseGameBtn.visibility = View.GONE
                binding.menuDivider.visibility = View.GONE
                binding.tutorialBtn.visibility = View.GONE
                binding.popupText.setText(R.string.glossary_text)
                binding.popupTitleText.setText(R.string.glossary_button)
            }
            "settingsDialog" -> {
                binding.popupTitleText.setText(R.string.preference_button)
                binding.pauseGameBtn.visibility = View.GONE
                binding.popupText.visibility = View.GONE
                binding.menuDivider.visibility = View.GONE
                binding.tutorialBtn.visibility = View.GONE
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.popupFragmentContainer, SettingsFragment())
                    .commit()
            }
            "menuDialog" -> {
                binding.popupTitleText.setText(R.string.preference_button)
                binding.popupText.visibility = View.GONE
                binding.tutorialBtn.visibility = View.GONE
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.popupFragmentContainer, SettingsFragment())
                    .commit()
            }
            "tutorialDialog" -> {
                binding.popupFragmentContainer.visibility = View.GONE
                binding.pauseGameBtn.visibility = View.GONE
                context?.let { ContextCompat.getColor(it, R.color.middle_brown) }
                    ?.let { binding.menuDivider.setBackgroundColor(it) }
                binding.popupTitleText.setText(R.string.tutorialTitel)

                val tutorialArray = resources.getStringArray(R.array.arrayTutorial)
            }
        }

        binding.closeBtn.setOnClickListener{
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}