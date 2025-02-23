import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlin.random.Random

class TournamentDrawApp : Application() {

    private val teams = mutableListOf<String>()
    private val players = mutableListOf<String>()

    override fun start(primaryStage: Stage) {
        primaryStage.title = "Football Tournament Drawing"

        // UI elements
        val textAreaTeams = TextArea().apply { promptText = "Enter teams, one per line" }
        val textAreaPlayers = TextArea().apply { promptText = "Enter players, one per line (for role-play draw)" }
        val inputGroupSize = TextField().apply { promptText = "Teams per group" }
        val inputNumPots = TextField().apply { promptText = "Number of Pots" }
        val inputTeamsPerPot = TextField().apply { promptText = "Teams per pot" }
        val resultArea = TextArea().apply { isEditable = false }

        val buttonKnockout = Button("Draw Knockout").apply {
            setOnAction {
                val teamsList = textAreaTeams.text.lines().map { it.trim() }.filter { it.isNotEmpty() }
                if (teamsList.size < 2 || teamsList.size % 2 != 0) {
                    resultArea.text = "Number of teams must be even and at least 2."
                } else {
                    val shuffledTeams = teamsList.shuffled(Random(System.currentTimeMillis()))
                    val matches = shuffledTeams.chunked(2) { "${it[0]} vs ${it[1]}" }
                    resultArea.text = matches.joinToString("\n")
                }
            }
        }

        val buttonGroups = Button("Draw Groups").apply {
            setOnAction {
                val teamsList = textAreaTeams.text.lines().map { it.trim() }.filter { it.isNotEmpty() }
                val groupSize = inputGroupSize.text.toIntOrNull()
                if (teamsList.isEmpty() || groupSize == null || groupSize < 2) {
                    resultArea.text = "Invalid input. Provide teams and a valid group size."
                } else {
                    val shuffledTeams = teamsList.shuffled(Random(System.currentTimeMillis()))
                    val groups = shuffledTeams.chunked(groupSize)
                    resultArea.text = groups.joinToString("\n\n") { it.joinToString(", ") }
                }
            }
        }

        val strictModeCheckbox = CheckBox("Strict Mode (One team per pot per group)").apply {
            isSelected = false
        }

        val buttonGroupsByPots = Button("Draw Groups by Pots").apply {
            setOnAction {
                val teamsList = textAreaTeams.text.lines().map { it.trim() }.filter { it.isNotEmpty() }
                val numPots = inputNumPots.text.toIntOrNull()
                val teamsPerPot = inputTeamsPerPot.text.toIntOrNull()

                if (numPots == null || teamsPerPot == null || teamsList.size != numPots * teamsPerPot) {
                    resultArea.text = "Invalid input. Ensure total teams = numPots × teamsPerPot."
                    return@setOnAction
                }

                val pots = List(numPots) { i ->
                    teamsList.subList(i * teamsPerPot, (i + 1) * teamsPerPot).shuffled(Random(System.currentTimeMillis())).toMutableList()
                }

                val numGroups = teamsPerPot
                val groups = List(numGroups) { mutableListOf<String>() }

                if (strictModeCheckbox.isSelected) {

                    val shuffledPots = pots.shuffled(Random(System.currentTimeMillis()))

                    groups.forEach { it.clear() }

                    for (pot in shuffledPots) {

                        pot.shuffle(Random(System.currentTimeMillis()))

                        for (i in pot.indices) {
                            groups[i].add(pot[i])
                        }
                    }
                } else {
                    // Non-Strict Mode: Randomly distribute teams from pots into groups
                    val shuffledTeams = teamsList.shuffled(Random(System.currentTimeMillis()))

                    //Reset groups
                    groups.forEach { it.clear() }

                    for (i in shuffledTeams.indices) {
                        groups[i % numGroups].add(shuffledTeams[i]) // Add teams in a round-robin fashion
                    }
                }

                resultArea.text = groups.shuffled(Random(System.currentTimeMillis())) // Shuffle the groups to randomize the order
                    .joinToString("\n") { "Group: ${it.joinToString(", ")}" }
            }
        }


        val buttonRolePlay = Button("Assign Teams to Players").apply {
            setOnAction {
                // Logic to handle role-play draw
                val teamsList = textAreaTeams.text.lines().map { it.trim() }.filter { it.isNotEmpty() }
                val playersList = textAreaPlayers.text.lines().map { it.trim() }.filter { it.isNotEmpty() }
                if (teamsList.size != playersList.size) {
                    resultArea.text = "Number of teams and players must be equal."
                } else {
                    val shuffledTeams = teamsList.shuffled(Random(System.currentTimeMillis()))
                    val assignments = playersList.zip(shuffledTeams) { player, team -> "$player -> $team" }
                    resultArea.text = assignments.joinToString("\n")
                }
            }
        }

        val layout = VBox(
            10.0,
            textAreaTeams,
            textAreaPlayers,
            inputGroupSize,
            inputNumPots,
            inputTeamsPerPot,
            strictModeCheckbox,
            buttonKnockout,
            buttonGroups,
            buttonGroupsByPots,
            buttonRolePlay,
            resultArea
        ).apply {
            padding = Insets(10.0)
        }


        // Setting the scene
        primaryStage.scene = Scene(layout, 400.0, 600.0)
        primaryStage.show()
    }
}

fun main() {
    Application.launch(TournamentDrawApp::class.java)
}
