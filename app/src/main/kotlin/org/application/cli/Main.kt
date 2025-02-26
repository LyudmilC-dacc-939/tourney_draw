import java.io.File
import kotlin.random.Random

fun main() {
    println("Welcome to the Football Tournament Drawing Program!")

    while (true) {
        val teams = mutableListOf<String>()

        println("\nChoose an option to add teams:")
        println("1. Load from a text file (one team per line)")
        println("2. Load from a CSV file (comma-separated)")
        println("3. Enter teams manually")
        println("4. Role-Playing Draw (Assign teams to players)")
        println("5. Exit program")
        print("Enter your choice (1/2/3/4/5): ")
        val choice = readLine()

        when (choice) {
            "1" -> {
                print("Enter the path of the text file: ")
                val filePath = readLine()
                if (filePath != null) {
                    try {
                        teams.addAll(File(filePath).readLines().map { it.trim() }.filter { it.isNotEmpty() })
                        println("Teams loaded successfully!")
                    } catch (e: Exception) {
                        println("Error reading the file: ${e.message}")
                    }
                }
            }
            "2" -> {
                print("Enter the path of the CSV file: ")
                val filePath = readLine()
                if (filePath != null) {
                    try {
                        val csvContent = File(filePath).readText()
                        teams.addAll(csvContent.split(",").map { it.trim() }.filter { it.isNotEmpty() })
                        println("Teams loaded successfully!")
                    } catch (e: Exception) {
                        println("Error reading the file: ${e.message}")
                    }
                }
            }
            "3" -> {
                println("Enter teams one by one. Type 'done' when finished:")
                while (true) {
                    print("Enter team name: ")
                    val team = readLine()
                    if (team != null && team.lowercase() == "done") break
                    if (team != null && team.isNotEmpty()) teams.add(team)
                }
            }
            "4" -> {
                rolePlayingDraw()
                continue
            }
            "5" -> {
                println("Exiting program. Goodbye!")
                return
            }
            else -> {
                println("Invalid choice! Please try again.")
                continue
            }
        }

        if (teams.isEmpty()) {
            println("No teams were added. Returning to the main menu.")
            continue
        }

        while (true) {
            println("\nChoose a drawing type:")
            println("1. Knockout Phase")
            println("2. Group Stage (Standard)")
            println("3. Group Stage with Pots")
            println("4. Return to Main Menu")
            print("Enter your choice (1/2/3/4): ")
            val drawChoice = readLine()

            when (drawChoice) {
                "1" -> {
                    if (teams.size < 2 || teams.size % 2 != 0) {
                        println("The number of teams must be even and at least 2 for a knockout phase.")
                        continue
                    }

                    do {
                        teams.shuffle(Random(System.currentTimeMillis()))
                        println("\nRandomized Knockout Phase Matches:")
                        for (i in teams.indices step 2) {
                            println("${teams[i]} vs ${teams[i + 1]}")
                        }

                        print("\nDo you want to repeat the draw? (yes/no): ")
                    } while (readLine()?.lowercase() == "yes")

                    println("Final draw confirmed. Good luck!")
                }

                "2" -> {
                    println("\nStandard Group Stage Drawing:")
                    print("Enter the number of teams per group (3, 4, 5, or 6): ")
                    val groupSize = readLine()?.toIntOrNull()
                    if (groupSize == null || groupSize !in 3..6) {
                        println("Invalid group size! Please choose 3, 4, 5, or 6.")
                        continue
                    }

                    teams.shuffle(Random(System.currentTimeMillis()))
                    val totalGroups = teams.size / groupSize
                    val leftoverTeams = teams.size % groupSize
                    var groupNumber = 1

                    println("\nRandomized Groups:")
                    for (i in 0 until totalGroups) {
                        val group = teams.subList(i * groupSize, (i + 1) * groupSize)
                        println("Group $groupNumber: ${group.joinToString(", ")}")
                        groupNumber++
                    }

                    if (leftoverTeams > 0) {
                        val remainingTeams = teams.takeLast(leftoverTeams)
                        println("Group $groupNumber (smaller): ${remainingTeams.joinToString(", ")}")
                    }

                    println("\nGroup drawing complete.")
                }

                "3" -> {
                    println("\nGroup Stage Drawing with Pots:")
                    print("Enter the number of pots: ")
                    val numPots = readLine()?.toIntOrNull()
                    if (numPots == null || numPots < 1) {
                        println("Invalid number of pots! Please enter a valid number.")
                        continue
                    }

                    print("Enter the number of teams per pot: ")
                    val teamsPerPot = readLine()?.toIntOrNull()
                    if (teamsPerPot == null || teamsPerPot < 1) {
                        println("Invalid number of teams per pot! Please enter a valid number.")
                        continue
                    }

                    val totalTeams = numPots * teamsPerPot
                    if (teams.size != totalTeams) {
                        println("Error: You need exactly $totalTeams teams, but you provided ${teams.size} teams.")
                        continue
                    }

                    val pots = mutableListOf<MutableList<String>>()
                    for (i in 0 until numPots) {
                        pots.add(mutableListOf())
                    }

                    println("\nEnter the teams for each pot:")
                    for (i in 0 until numPots) {
                        println("\nPot ${i + 1}:")
                        for (j in 0 until teamsPerPot) {
                            print("Enter team ${(j + 1)} for Pot ${i + 1}: ")
                            val team = readLine()
                            if (team != null && team.isNotEmpty()) {
                                pots[i].add(team)
                            }
                        }
                    }

                    print("\nEnter the number of groups: ")
                    val numGroups = readLine()?.toIntOrNull()
                    if (numGroups == null || numGroups < 1 || numGroups > teamsPerPot) {
                        println("Invalid number of groups!")
                        continue
                    }

                    val groups = Array(numGroups) { mutableListOf<String>() }

                    for (pot in pots) {
                        pot.shuffle(Random(System.currentTimeMillis()))
                        for ((index, team) in pot.withIndex()) {
                            groups[index % numGroups].add(team)
                        }
                    }

                    println("\nRandomized Groups:")
                    for ((index, group) in groups.withIndex()) {
                        println("Group ${index + 1}: ${group.joinToString(", ")}")
                    }

                    println("\nGroup drawing with pots complete.")
                }

                "4" -> {
                    println("Returning to the main menu...\n")
                    break
                }

                else -> {
                    println("Invalid choice! Please enter 1, 2, 3, or 4.")
                }
            }
        }
    }
}

fun rolePlayingDraw() {
    val teams = mutableListOf<String>()
    val players = mutableListOf<String>()

    print("Enter the number of teams (and players): ")
    val numTeams = readLine()?.toIntOrNull()
    if (numTeams == null || numTeams < 1) {
        println("Invalid number of teams!")
        return
    }

    println("\nEnter $numTeams teams:")
    repeat(numTeams) {
        print("Enter team: ")
        val team = readLine()
        if (!team.isNullOrEmpty()) teams.add(team)
    }

    println("\nEnter $numTeams players:")
    repeat(numTeams) {
        print("Enter player: ")
        val player = readLine()
        if (!player.isNullOrEmpty()) players.add(player)
    }

    teams.shuffle()
    println("\nAssigned Teams:")
    for (i in teams.indices) {
        println("${players[i]} -> ${teams[i]}")
    }
}
