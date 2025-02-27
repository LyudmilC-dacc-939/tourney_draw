package org.application

import java.awt.BorderLayout
import javax.swing.JPanel

abstract class BasePanel : JPanel(BorderLayout()) {
    abstract fun updateUIContent()
}