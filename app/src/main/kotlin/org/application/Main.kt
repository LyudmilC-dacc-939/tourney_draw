package org.application

import javax.swing.SwingUtilities


fun main() {
    SwingUtilities.invokeLater {
        MainPanel().isVisible = true
    }
}
