package org.application

import javax.swing.JFrame
import javax.swing.SwingUtilities


fun main() {
    SwingUtilities.invokeLater {
        val frame = JFrame("Football Tournament Drawing").apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            size = java.awt.Dimension(700, 800)
            isResizable = false
        }
        val coordinator = PanelCoordinator(frame)
        coordinator.switchPanel(MainPanel(coordinator))
        frame.isVisible = true
    }
}
