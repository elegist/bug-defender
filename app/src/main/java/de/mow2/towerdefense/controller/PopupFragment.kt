package de.mow2.towerdefense.controller

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
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
        val tutorialArray = resources.getStringArray(R.array.arrayTutorial)

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
                binding.popupTitleText.setText(R.string.tutorialTitel)
                binding.popupText.setText(R.string.tutorialWelcome)
                context?.let { ContextCompat.getColor(it, R.color.middle_brown) }
                    ?.let { binding.menuDivider.setBackgroundColor(it) }

                binding.tutorialBtn.setOnClickListener {
                    when (binding.popupText.text) {
                        getString(R.string.tutorialWelcome) -> {
                            binding.popupText.setText(R.string.tutorialMenu)
                        }
                        getString(R.string.tutorialMenu) -> {
                            binding.popupText.setText(R.string.tutorialMenuLeft)
                        }
                        getString(R.string.tutorialMenuLeft) -> {
                            binding.popupText.setText(R.string.tutorialMenuRight)
                        }
                        getString(R.string.tutorialMenuRight) -> {
                            binding.popupText.setText(R.string.tutorialMenuMiddle)
                        }
                        getString(R.string.tutorialMenuMiddle) -> {
                            binding.popupText.setText(R.string.tutorialMenuHold)
                        }
                        getString(R.string.tutorialMenuHold) -> {
                            binding.popupText.setText(R.string.tutorialGUI)
                        }
                        getString(R.string.tutorialGUI) -> {
                            binding.popupText.setText(R.string.tutorialGUILeft)
                        }
                        getString(R.string.tutorialGUILeft) -> {
                            binding.popupText.setText(R.string.tutorialGUIRight)
                        }
                        getString(R.string.tutorialGUIRight) -> {
                            binding.popupText.setText(R.string.tutorialGUILeftBar)
                        }
                        getString(R.string.tutorialGUILeftBar) -> {
                            binding.popupText.setText(R.string.tutorialGUIRightBar)
                        }
                        getString(R.string.tutorialGUIRightBar) -> {
                            binding.popupText.setText(R.string.tutorialMenuButton)
                        }
                        getString(R.string.tutorialMenuButton) -> {
                            binding.popupText.setText(R.string.tutorialMenuButtonInner)
                            binding.tutorialBtn.setText(R.string.close_button)
                            binding.tutorialBtn.setOnClickListener {
                                dismiss()
                            }
                        }
                    }
                }
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