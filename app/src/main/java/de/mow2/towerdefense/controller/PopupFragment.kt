package de.mow2.towerdefense.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import de.mow2.towerdefense.R
import de.mow2.towerdefense.databinding.PopupViewBinding

/**
 * Dialog Fragment for popup window in mainActivity (about, info, prefs)
 * inflates view onCreate, sets Settings for each popup window
 * gets preferences through childFragment
 * functionality for close button in popup view
 * */

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
                binding.leaveGameBtn.visibility = View.GONE
                binding.pauseGameBtn.visibility = View.GONE
                binding.menuDivider.visibility = View.GONE
                binding.popupText.setText(R.string.about_text)
                binding.popupTitleText.setText(R.string.about_button)
            }
            "infoDialog" -> {
                binding.popupFragmentContainer.visibility = View.GONE
                binding.leaveGameBtn.visibility = View.GONE
                binding.pauseGameBtn.visibility = View.GONE
                binding.menuDivider.visibility = View.GONE
                binding.popupText.setText(R.string.info_text)
                binding.popupTitleText.setText(R.string.info_button)
            }
            "settingsDialog" -> {
                binding.popupTitleText.setText(R.string.preference_button)
                binding.leaveGameBtn.visibility = View.GONE
                binding.pauseGameBtn.visibility = View.GONE
                binding.popupText.visibility = View.GONE
                binding.menuDivider.visibility = View.GONE
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.popupFragmentContainer, SettingsFragment())
                    .commit()
            }
            "menuDialog" -> {
                binding.popupTitleText.setText(R.string.preference_button)
                binding.popupText.visibility = View.GONE
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.popupFragmentContainer, SettingsFragment())
                    .commit()
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