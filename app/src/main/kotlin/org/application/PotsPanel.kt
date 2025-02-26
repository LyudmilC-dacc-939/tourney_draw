package org.application

import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.*

class PotsPanel(
    teamsInput: String,
    private val resultArea: JTextArea,
    private val mainFrame: MainPanel
) : JPanel(BorderLayout()) {

    private val potsFields = mutableListOf<JTextArea>()
    private val numPotsField = JTextField().apply {
        border = BorderFactory.createTitledBorder("Number of Pots (2-8)")
    }

    init {
        // Top panel for number of pots
        val topPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(numPotsField)
            add(JButton("Create Pots").apply {
                addActionListener {
                    val numPots = numPotsField.text.toIntOrNull()
                    if (numPots == null || numPots < 2 || numPots > 8) {
                        JOptionPane.showMessageDialog(this@PotsPanel, "Number of pots must be between 2 and 8.")
                        return@addActionListener
                    }
                    createPotsFields(numPots)
                }
            })
        }

        // Center panel for pots input
        val potsPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
        }

        // Bottom panel for draw and return buttons
        val bottomPanel = JPanel().apply {
            layout = GridLayout(1, 2, 10, 10)
            add(JButton("Draw!").apply {
                addActionListener {
                    // Perform drawing in a background thread to avoid freezing
                    Thread {
                        val pots = potsFields.map { it.text.lines().map { it.trim() }.filter { it.isNotEmpty() } }
                        if (pots.any { it.size < 2 }) {
                            SwingUtilities.invokeLater {
                                JOptionPane.showMessageDialog(this@PotsPanel, "Each pot must have at least 2 teams.")
                            }
                            return@Thread
                        }
                        val result = drawGroupsFromPots(pots)
                        SwingUtilities.invokeLater {
                            resultArea.text = result
                        }
                    }.start()
                }
            })
            add(JButton("Return to Main").apply {
                addActionListener {
                    // Switch back to the main panel
                    SwingUtilities.invokeLater {
                        mainFrame.contentPane.removeAll()
                        mainFrame.add(mainFrame.buttonPanel, BorderLayout.NORTH)
                        mainFrame.add(mainFrame.inputPanel, BorderLayout.CENTER)
                        mainFrame.revalidate()
                        mainFrame.repaint()
                    }
                }
            })
        }

        // Add components to the panel
        add(topPanel, BorderLayout.NORTH)
        add(JScrollPane(potsPanel), BorderLayout.CENTER)
        add(bottomPanel, BorderLayout.SOUTH)
    }

    private fun createPotsFields(numPots: Int) {
        val potsPanel = (getComponent(1) as JScrollPane).viewport.view as JPanel
        potsPanel.removeAll()
        potsFields.clear()

        for (i in 1..numPots) {
            val potField = JTextArea(5, 30).apply {
                lineWrap = true
                border = BorderFactory.createTitledBorder("Pot $i")
            }
            potsFields.add(potField)
            potsPanel.add(potField)
            potsPanel.add(Box.createVerticalStrut(10))
        }

        potsPanel.revalidate()
        potsPanel.repaint()
    }

    private fun drawGroupsFromPots(pots: List<List<String>>): String {
        val groups = mutableListOf<MutableList<String>>()
        val maxGroupSize = pots.maxOf { it.size }

        for (i in 0 until maxGroupSize) {
            pots.forEachIndexed { potIndex, pot ->
                if (i < pot.size) {
                    if (groups.size <= potIndex) {
                        groups.add(mutableListOf())
                    }
                    groups[potIndex].add(pot[i])
                }
            }
        }

        return groups.joinToString("\n") { "Group: ${it.joinToString(", ")}" }
    }
}