package de.mow2.towerdefense.controller

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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
        //val tutorialArray = resources.getStringArray(R.array.arrayTutorial)

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
                binding.tutorialBtn.setText(R.string.tutorialTitel)
                binding.tutorialBtn.setOnClickListener{
                    dismiss()
                    (activity as GameActivity).showTutorial()
                }
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.popupFragmentContainer, SettingsFragment())
                    .commit()
            }
            "tutorialDialog" -> {
                binding.popupFragmentContainer.visibility = View.GONE
                binding.pauseGameBtn.visibility = View.GONE
                dialog?.window?.also {window ->
                    window.attributes?.also {attributes->
                        attributes.dimAmount = 0f
                        window.attributes = attributes
                    }
                }
                binding.popupTitleText.setText(R.string.tutorialTitel)
                binding.popupText.setText(R.string.tutorialWelcome)
                context?.let { ContextCompat.getColor(it, R.color.middle_brown) }
                    ?.let { binding.menuDivider.setBackgroundColor(it) }

                binding.tutorialBtn.setOnClickListener {
                    when (binding.popupText.text) {
                        getString(R.string.tutorialWelcome) -> {
                            binding.popupText.setText(R.string.tutorialMenu)
                            (activity as GameActivity).highlight("bottomGui")
                        }
                        getString(R.string.tutorialMenu) -> {
                            binding.popupText.setText(R.string.tutorialMenuLeft)
                            (activity as GameActivity).highlight("bottomLeft")
                        }
                        getString(R.string.tutorialMenuLeft) -> {
                            binding.popupText.setText(R.string.tutorialMenuRight)
                            (activity as GameActivity).highlight("bottomRight")
                        }
                        getString(R.string.tutorialMenuRight) -> {
                            binding.popupText.setText(R.string.tutorialMenuMiddle)
                            (activity as GameActivity).highlight("bottomMiddle")
                        }
                        getString(R.string.tutorialMenuMiddle) -> {
                            binding.popupText.setText(R.string.tutorialGUI)
                            (activity as GameActivity).highlight("topGui")
                        }
                        getString(R.string.tutorialGUI) -> {
                            binding.popupText.setText(R.string.tutorialGUILeft)
                            (activity as GameActivity).highlight("topGuiLeft")
                        }
                        getString(R.string.tutorialGUILeft) -> {
                            binding.popupText.setText(R.string.tutorialGUIRight)
                            (activity as GameActivity).highlight("topGuiRight")
                        }
                        getString(R.string.tutorialGUIRight) -> {
                            binding.popupText.setText(R.string.tutorialGUILeftBar)
                            (activity as GameActivity).highlight("topGuiLeftBar")
                        }
                        getString(R.string.tutorialGUILeftBar) -> {
                            binding.popupText.setText(R.string.tutorialGUIRightBar)
                            (activity as GameActivity).highlight("topGuiRightBar")
                        }
                        getString(R.string.tutorialGUIRightBar) -> {
                            binding.popupText.setText(R.string.tutorialMenuButton)
                            (activity as GameActivity).highlight("topGuiMenu")
                            binding.tutorialBtn.setText(R.string.close_button)
                            binding.tutorialBtn.setOnClickListener {
                                (activity as GameActivity).highlight("endTutorial")
                                dismiss()
                            }
                        }
                    }
                }
            }
        }

        binding.closeBtn.setOnClickListener{
            (activity as GameActivity).highlight("endTutorial")
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}