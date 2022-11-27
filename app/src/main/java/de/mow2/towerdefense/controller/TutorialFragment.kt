package de.mow2.towerdefense.controller

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.DialogFragment
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceManager
import de.mow2.towerdefense.R
import de.mow2.towerdefense.databinding.TutorialPopupBinding
import de.mow2.towerdefense.model.core.GameManager

class TutorialFragment: DialogFragment() {
    private var _binding: TutorialPopupBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TutorialPopupBinding.inflate(inflater, container, false)
        dialog?.setCancelable(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tutText = binding.tutText
        val tutBtn = binding.goOnBtn
        val tutClose = binding.closeTutBtn

        when (tag.toString()) {
            "tutorialDialog" -> {
                dialog?.window?.also { window ->
                    window.attributes?.also { attributes ->
                        attributes.dimAmount = 0f
                        window.attributes = attributes
                    }
                }

                tutText.setText(R.string.tutorialWelcome)

                tutBtn.setOnClickListener {
                    when (tutText.text) {
                        getString(R.string.tutorialWelcome) -> {
                            tutText.setText(R.string.tutorialMenu)
                            (activity as GameActivity).highlight("bottomGui")
                        }
                        getString(R.string.tutorialMenu) -> {
                            tutText.setText(R.string.tutorialMenuLeft)
                            (activity as GameActivity).highlight("bottomLeft")
                        }
                        getString(R.string.tutorialMenuLeft) -> {
                            tutText.setText(R.string.tutorialMenuRight)
                            (activity as GameActivity).highlight("bottomRight")
                        }
                        getString(R.string.tutorialMenuRight) -> {
                            tutText.setText(R.string.tutorialMenuMiddle)
                            (activity as GameActivity).highlight("bottomMiddle")
                        }
                        getString(R.string.tutorialMenuMiddle) -> {
                            tutText.setText(R.string.tutorialGUI)
                            (activity as GameActivity).highlight("topGui")
                        }
                        getString(R.string.tutorialGUI) -> {
                            tutText.setText(R.string.tutorialGUILeft)
                            (activity as GameActivity).highlight("topGuiLeft")
                        }
                        getString(R.string.tutorialGUILeft) -> {
                            tutText.setText(R.string.tutorialGUIRight)
                            (activity as GameActivity).highlight("topGuiRight")
                        }
                        getString(R.string.tutorialGUIRight) -> {
                            tutText.setText(R.string.tutorialGUILeftBar)
                            (activity as GameActivity).highlight("topGuiLeftBar")
                        }
                        getString(R.string.tutorialGUILeftBar) -> {
                            tutText.setText(R.string.tutorialGUIRightBar)
                            (activity as GameActivity).highlight("topGuiRightBar")
                        }
                        getString(R.string.tutorialGUIRightBar) -> {
                            tutText.setText(R.string.tutorialMenuButton)
                            (activity as GameActivity).highlight("topGuiMenu")
                            tutBtn.setText(R.string.close_button)
                            tutBtn.setOnClickListener {
                                (activity as GameActivity).highlight("endTutorial")
                                (activity as GameActivity).displayTutorial(false)
                                dismiss()
                            }
                        }
                    }
                }
            }
        }

        tutClose.setOnClickListener {
            (activity as GameActivity).highlight("endTutorial")
            (activity as GameActivity).displayTutorial(false)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}