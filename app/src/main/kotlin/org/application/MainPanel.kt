package org.application

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*
import kotlin.random.Random

class MainPanel(private val coordinator: PanelCoordinator) : BasePanel() {
    private val textAreaTeams = JTextArea(5, 30).apply {
        lineWrap = true
        border = BorderFactory.createTitledBorder("Enter teams (one per line)")
    }
    private val scrollTeams = JScrollPane(textAreaTeams).apply {
        preferredSize = Dimension(450, 150)
    }

    private val textAreaPlayers = JTextArea(5, 30).apply {
        lineWrap = true
        border = BorderFactory.createTitledBorder("Enter players (one per line)")
    }
    private val scrollPlayers = JScrollPane(textAreaPlayers).apply {
        preferredSize = Dimension(450, 150)
    }

    private val resultArea = JTextArea(10, 30).apply {
        isEditable = false
        lineWrap = true
        border = BorderFactory.createTitledBorder("Results")
    }
    private val scrollResults = JScrollPane(resultArea).apply {
        preferredSize = Dimension(450, 200)
    }

    private val buttonPanel = JPanel().apply {
        layout = GridLayout(1, 5, 10, 10)
        add(JButton("Draw Knockout").apply {
            addActionListener {
                val teamsList = textAreaTeams.text.lines().map { it.trim() }.filter { it.isNotEmpty() }
                if (teamsList.size < 2 || teamsList.size % 2 != 0) {
                    resultArea.text = "Number of teams must be even and at least 2."
                } else {
                    val shuffledTeams = teamsList.shuffled(Random(System.currentTimeMillis()))
                    resultArea.text = shuffledTeams.chunked(2) { "${it[0]} vs ${it[1]}" }
                        .joinToString("\n")
                }
            }
        })
        add(JButton("Draw Groups").apply {
            addActionListener {
                val teamsList = textAreaTeams.text.lines().map { it.trim() }.filter { it.isNotEmpty() }
                val groupSize = JOptionPane.showInputDialog(this@MainPanel, "Enter group size:").toIntOrNull()
                if (teamsList.isEmpty() || groupSize == null || groupSize < 2) {
                    resultArea.text = "Invalid input. Provide teams and a valid group size."
                } else {
                    val shuffledTeams = teamsList.shuffled(Random(System.currentTimeMillis()))
                    resultArea.text = shuffledTeams.chunked(groupSize)
                        .joinToString("\n\n") { it.joinToString(", ") }
                }
            }
        })
        add(JButton("Draw Groups by Pots").apply {
            addActionListener {
                coordinator.showPotsPanel()
            }
        })
        add(JButton("Assign Teams").apply {
            addActionListener {
                val teamsList = textAreaTeams.text.lines().map { it.trim() }.filter { it.isNotEmpty() }
                val playersList = textAreaPlayers.text.lines().map { it.trim() }.filter { it.isNotEmpty() }
                if (teamsList.size != playersList.size) {
                    resultArea.text = "Number of teams and players must be equal."
                } else {
                    val shuffledTeams = teamsList.shuffled(Random(System.currentTimeMillis()))
                    resultArea.text = playersList.zip(shuffledTeams) { player, team -> "$player -> $team" }
                        .joinToString("\n")
                }
            }
        })
    }

    init {
        add(buttonPanel, BorderLayout.NORTH)
        add(JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(scrollTeams)
            add(Box.createVerticalStrut(10))
            add(scrollPlayers)
            add(Box.createVerticalStrut(10))
            add(scrollResults)
        }, BorderLayout.CENTER)
    }

    override fun updateUIContent() {}
}