package org.application

import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.SwingUtilities

class PanelCoordinator(private val frame: JFrame) {
    private val mainPanel = MainPanel(this)
    private val potsPanel = PotsPanel(this)

    fun showMainPanel() {
        switchPanel(mainPanel)
    }

    fun showPotsPanel() {
        switchPanel(potsPanel)
    }

    fun switchPanel(panel: BasePanel) {
        SwingUtilities.invokeLater {
            frame.contentPane.removeAll()
            frame.contentPane.add(panel, BorderLayout.CENTER)
            frame.revalidate()
            frame.repaint()
            panel.updateUIContent()
        }
    }
}