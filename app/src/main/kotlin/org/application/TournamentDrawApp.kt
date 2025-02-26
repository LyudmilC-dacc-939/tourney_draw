import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.ActionEvent
import javax.swing.*
import kotlin.random.Random

class TournamentDrawApp : JFrame("Football Tournament Drawing") {

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        size = Dimension(500, 700)
        layout = BorderLayout()

        // Input components
        val textAreaTeams = JTextArea().apply {
            lineWrap = true
            border = BorderFactory.createTitledBorder("Enter teams (one per line)")
        }

        val textAreaPlayers = JTextArea().apply {
            lineWrap = true
            border = BorderFactory.createTitledBorder("Enter players (one per line)")
        }

        val inputGroupSize = JTextField().apply {
            border = BorderFactory.createTitledBorder("Teams per group")
        }

        val inputNumPots = JTextField().apply {
            border = BorderFactory.createTitledBorder("Number of Pots")
        }

        val inputTeamsPerPot = JTextField().apply {
            border = BorderFactory.createTitledBorder("Teams per pot")
        }

        val strictModeCheckbox = JCheckBox("Strict Mode (One team per pot per group)")

        // Result area
        val resultArea = JTextArea().apply {
            isEditable = false
            lineWrap = true
            border = BorderFactory.createTitledBorder("Results")
        }

        // Buttons
        val buttonKnockout = JButton("Draw Knockout").apply {
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
        }

        val buttonGroups = JButton("Draw Groups").apply {
            addActionListener {
                val teamsList = textAreaTeams.text.lines().map { it.trim() }.filter { it.isNotEmpty() }
                val groupSize = inputGroupSize.text.toIntOrNull()
                if (teamsList.isEmpty() || groupSize == null || groupSize < 2) {
                    resultArea.text = "Invalid input. Provide teams and a valid group size."
                } else {
                    val shuffledTeams = teamsList.shuffled(Random(System.currentTimeMillis()))
                    resultArea.text = shuffledTeams.chunked(groupSize)
                        .joinToString("\n\n") { it.joinToString(", ") }
                }
            }
        }

        val buttonGroupsByPots = JButton("Draw Groups by Pots").apply {
            addActionListener {
                val teamsList = textAreaTeams.text.lines().map { it.trim() }.filter { it.isNotEmpty() }
                val numPots = inputNumPots.text.toIntOrNull()
                val teamsPerPot = inputTeamsPerPot.text.toIntOrNull()

                if (numPots == null || teamsPerPot == null || teamsList.size != numPots * teamsPerPot) {
                    resultArea.text = "Invalid input. Ensure total teams = numPots × teamsPerPot."
                    return@addActionListener
                }

                val pots = List(numPots) { i ->
                    teamsList.subList(i * teamsPerPot, (i + 1) * teamsPerPot)
                        .shuffled(Random(System.currentTimeMillis()))
                        .toMutableList()
                }

                val numGroups = teamsPerPot
                val groups = List(numGroups) { mutableListOf<String>() }

                if (strictModeCheckbox.isSelected) {
                    pots.shuffled(Random(System.currentTimeMillis())).forEach { pot ->
                        pot.shuffle(Random(System.currentTimeMillis()))
                        pot.forEachIndexed { i, team -> groups[i].add(team) }
                    }
                } else {
                    teamsList.shuffled(Random(System.currentTimeMillis())).forEachIndexed { i, team ->
                        groups[i % numGroups].add(team)
                    }
                }

                resultArea.text = groups.shuffled(Random(System.currentTimeMillis()))
                    .joinToString("\n") { "Group: ${it.joinToString(", ")}" }
            }
        }

        val buttonRolePlay = JButton("Assign Teams to Players").apply {
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
        }

        // Layout
        val inputPanel = JPanel().apply {
            layout = GridLayout(0, 1, 10, 10)
            add(textAreaTeams)
            add(textAreaPlayers)
            add(inputGroupSize)
            add(inputNumPots)
            add(inputTeamsPerPot)
            add(strictModeCheckbox)
        }

        val buttonPanel = JPanel().apply {
            layout = GridLayout(1, 0, 10, 10)
            add(buttonKnockout)
            add(buttonGroups)
            add(buttonGroupsByPots)
            add(buttonRolePlay)
        }

        add(inputPanel, BorderLayout.NORTH)
        add(JScrollPane(resultArea), BorderLayout.CENTER)
        add(buttonPanel, BorderLayout.SOUTH)
    }
}

fun main() {
    SwingUtilities.invokeLater {
        TournamentDrawApp().isVisible = true
    }
}