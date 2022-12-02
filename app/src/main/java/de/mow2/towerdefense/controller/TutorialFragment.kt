package de.mow2.towerdefense.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import de.mow2.towerdefense.R
import de.mow2.towerdefense.databinding.TutorialPopupBinding

/**
 * Fragment for the tutorial
 * decides in when statement which text to show and which element should be highlighted an hidden
 */
class TutorialFragment : DialogFragment() {
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
        val gameActivity = (activity as GameActivity)

        when (tag.toString()) {
            "tutorialDialog" -> {
                dialog?.window?.also { window ->
                    window.attributes?.also { attributes ->
                        attributes.dimAmount = 0f
                        window.attributes = attributes
                    }
                }
                tutText.setText(R.string.tutorialWelcome)
                gameActivity.highlight("tutorial")

                tutBtn.setOnClickListener {
                    when (tutText.text) {
                        getString(R.string.tutorialWelcome) -> {
                            tutText.setText(R.string.tutorialMenu)
                            gameActivity.highlight("bottomGui")

                        }
                        getString(R.string.tutorialMenu) -> {
                            tutText.setText(R.string.tutorialMenuLeft)
                            gameActivity.highlight("deleteBtn")
                        }
                        getString(R.string.tutorialMenuLeft) -> {
                            tutText.setText(R.string.tutorialMenuRight)
                            gameActivity.highlight("upgradeBtn")
                        }
                        getString(R.string.tutorialMenuRight) -> {
                            tutText.setText(R.string.tutorialMenuMiddle)
                            gameActivity.highlight("buildBtn")
                        }
                        getString(R.string.tutorialMenuMiddle) -> {
                            tutText.setText(R.string.tutorialGUI)
                            gameActivity.highlight("topGui")
                        }
                        getString(R.string.tutorialGUI) -> {
                            tutText.setText(R.string.tutorialGUILeft)
                            gameActivity.highlight("time")
                        }
                        getString(R.string.tutorialGUILeft) -> {
                            tutText.setText(R.string.tutorialGUIRight)
                            gameActivity.highlight("coins")
                        }
                        getString(R.string.tutorialGUIRight) -> {
                            tutText.setText(R.string.tutorialGUILeftBar)
                            gameActivity.highlight("healthBar")
                        }
                        getString(R.string.tutorialGUILeftBar) -> {
                            tutText.setText(R.string.tutorialGUIRightBar)
                            gameActivity.highlight("progressBar")
                        }
                        getString(R.string.tutorialGUIRightBar) -> {
                            tutText.setText(R.string.tutorialMenuButton)
                            gameActivity.highlight("menuBtn")
                            tutBtn.setText(R.string.close_button)
                            tutBtn.setOnClickListener {
                                gameActivity.highlight("endTutorial")
                                gameActivity.displayTutorial(false)
                                dismiss()
                            }
                        }
                    }
                }
            }
        }

        tutClose.setOnClickListener {
            gameActivity.highlight("endTutorial")
            gameActivity.displayTutorial(false)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}