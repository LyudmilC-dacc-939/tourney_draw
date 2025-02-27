package org.application

import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.*

class PotsPanel(private val coordinator: PanelCoordinator) : BasePanel() {
    private val potsPanel = JPanel().apply { layout = BoxLayout(this, BoxLayout.Y_AXIS) }
    private val potsFields = mutableListOf<JTextArea>()
    private val resultArea = JTextArea(10, 30).apply {
        isEditable = false
        lineWrap = true
        border = BorderFactory.createTitledBorder("Draw Results")
    }
    private val numPotsField = JTextField().apply {
        border = BorderFactory.createTitledBorder("Number of Pots (2-8)")
    }

    init {
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

        val bottomPanel = JPanel().apply {
            layout = GridLayout(1, 2, 10, 10)
            add(JButton("Draw Teams").apply {
                addActionListener {
                    resultArea.text = ""

                    // Function to generate group labels dynamically
                    fun getGroupLabel(index: Int): String {
                        val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        var groupName = ""
                        var tempIndex = index
                        while (tempIndex >= 0) {
                            groupName = letters[tempIndex % 26] + groupName
                            tempIndex = tempIndex / 26 - 1
                        }
                        return "Group $groupName"
                    }

                    // Shuffle each pot to introduce randomness
                    val pots = potsFields.map { it.text.lines().filter { team -> team.isNotEmpty() }.toMutableList().apply { shuffle() } }
                    val groups = mutableListOf<MutableList<String>>()

                    // Draw teams into groups while ensuring randomness
                    while (pots.any { it.isNotEmpty() }) {
                        val group = mutableListOf<String>()
                        for (pot in pots) {
                            if (pot.isNotEmpty()) group.add(pot.removeAt(0))
                        }
                        groups.add(group)
                    }

                    // Generate group labels dynamically
                    val groupLabels = groups.mapIndexed { index, group -> "${getGroupLabel(index)}: ${group.joinToString(", ")}" }

                    resultArea.text = groupLabels.joinToString("\n\n")
                }
            })
            add(JButton("Return to Main Menu").apply {
                addActionListener { coordinator.showMainPanel() }
            })
        }


        add(topPanel, BorderLayout.NORTH)
        add(JScrollPane(resultArea), BorderLayout.CENTER)
        add(bottomPanel, BorderLayout.SOUTH)
        add(potsPanel, BorderLayout.EAST)
    }

    private fun createPotsFields(numPots: Int) {
        potsPanel.removeAll()
        potsFields.clear()
        for (i in 1..numPots) {
            val potField = JTextArea(5, 30).apply {
                lineWrap = true
                border = BorderFactory.createTitledBorder("Pot $i")
            }
            potsFields.add(potField)
            potsPanel.add(JScrollPane(potField))
        }
        potsPanel.revalidate()
        potsPanel.repaint()
    }

    override fun updateUIContent() {}
}
