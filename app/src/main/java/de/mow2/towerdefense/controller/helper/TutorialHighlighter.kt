package de.mow2.towerdefense.controller.helper

import android.widget.Button
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children

class TutorialHighlighter (
    private val healthBar: ConstraintLayout,
    private val progressBar: ConstraintLayout,
    private val time: ConstraintLayout,
    private val coins: ConstraintLayout,
    private val bottomGui: ConstraintLayout,
    private val menuBtn: Button,
    private val deleteBtn: ImageButton,
    private val upgradeBtn: ImageButton,
    private val buildBtn: ImageButton
    ){

    /**
     * highlight Elements in Tutorial
     * @param element defines which Element should be shown
     */
    fun highlightElement(element: String) {
        bottomGui.children.forEach { it.alpha = 0.2F }
        time.children.forEach { it.alpha = 0.2F }
        coins.children.forEach { it.alpha = 0.2F }
        healthBar.children.forEach { it.alpha = 0.2F }
        progressBar.children.forEach { it.alpha = 0.2F }
        menuBtn.alpha = 0.2F
        when(element) {
            "bottomGui" -> {
                bottomGui.children.forEach { it.alpha = 1F }
            }
            "bottomLeft" -> {
                deleteBtn.alpha = 1F
            }
            "bottomRight" -> {
                upgradeBtn.alpha = 1F
            }
            "bottomMiddle" -> {
                buildBtn.alpha = 1F
            }
            "topGui" -> {
                time.children.forEach { it.alpha = 1F }
                coins.children.forEach { it.alpha = 1F }
                healthBar.children.forEach { it.alpha = 1F }
                progressBar.children.forEach { it.alpha = 1F }
                menuBtn.alpha = 1F
            }
            "topGuiLeft" -> {
                time.children.forEach { it.alpha = 1F }
            }
            "topGuiRight" -> {
                coins.children.forEach { it.alpha = 1F }
            }
            "topGuiLeftBar" -> {
                healthBar.children.forEach { it.alpha = 1F }
            }
            "topGuiRightBar" -> {
                progressBar.children.forEach { it.alpha = 1F }
            }
            "topGuiMenu" -> {
                menuBtn.alpha = 1F
            }
            "endTutorial" -> {
                bottomGui.children.forEach { it.alpha = 1F }
                time.children.forEach { it.alpha = 1F }
                coins.children.forEach { it.alpha = 1F }
                healthBar.children.forEach { it.alpha = 1F }
                progressBar.children.forEach { it.alpha = 1F }
                menuBtn.alpha = 1F
            }
        }
    }
}