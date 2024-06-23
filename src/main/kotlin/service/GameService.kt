package service

import entity.*

/**
 * Service layer class that provides the logic for actions not directly
 * related to a single player.
 */

class GameService(private val rootService: RootService) : AbstractRefreshingService() {

    /**
     * Initializes a new game
     * First a new Pokerwave entity is created with the according rounds and player names.
     * After that an initial Drawstack is created that holds 52 cards.
     * After creating players and giving them cards from the initial deck
     * and giving 3 cards to the middle stack, it is set as the games drawstack
     * @param rounds declares the amount of rounds the game will have
     * @param names List with Names of players
     * @throws IllegalArgumentException when initializing with wrong parameters.
     * The values itself are checked for validation when initializing a Pokerwave Object.
     *
     */
    fun startNewGame(rounds: Int, names: List<String>) {
        val initDrawStack = WaveCardStack(52)
        fillCardStack(initDrawStack)
        // shuffling since the first player will be the start player,
        // thus a random start player is promised
        val game = PokerWave(rounds, createWavePlayers(names, initDrawStack).shuffled())
        game.middleList = initDrawStack.draw(3).toMutableList()
        game.drawStack = initDrawStack
        game.currentTurn = 1
        rootService.currentGame = game // adds game to rootService
        onAllRefreshables { refreshAfterStartNewGame() }
    }

    /**
     * Fills the given stack with a full card game and shuffles them
     * To fill the stack a 2 - for loop is used that creates a card for each [CardSuit] a [CardValue].
     * @param stack is the given stack thatll be filled
     * @throws IllegalArgumentException when given stack is no [WaveCardStack]
     */
     fun fillCardStack(stack: WaveCardStack) {
        val deck = mutableListOf<WaveCard>()
        for (suit in CardSuit.values()) {
            for (value in CardValue.values()) {
                deck.add(WaveCard(suit, value))
            }
        }
        stack.putOnTop(deck)  // puts the list of cards on the stack
        stack.shuffle()
    }

    /**
     * For each given player name a [WavePlayer] is created
     * @param players holds the players names
     * @param cardStack holds the drawStack players are getting their initial cards from
     * @return List with corresponding WavePlayers
     * @throws IllegalArgumentException when initializing with invalid objects
     */
    private fun createWavePlayers(players: List<String>, cardStack: WaveCardStack): List<WavePlayer> {
        val resultList = mutableListOf<WavePlayer>()
        for (player in players) {
            val newPlayer = WavePlayer(player)
            newPlayer.openHand = cardStack.draw(3).toMutableList() // toMutableList since draw returns a normal List
            newPlayer.hiddenHand =
                cardStack.draw(2) // here toMutableList is not necessary since the hiddenHand cards won't change
            resultList.add(newPlayer) // adds each player to the resulting WavePlayer List
        }
        return resultList.toList()
    }


    /***
     * Basic Function to end the game and get to the EndMenuScreen
     * Checks if the last turn of the game is finished and calculates the games highscore. Calls the refreshable with
     * @throws IllegalStateException if method gets called before the game's last turn is finished or there is no game
     */
    fun endGame() {
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet" }
        check(lastTurnFinished()) { "Current game not finished yet" }
        check(game.currentAction == Action.WAIT)
        game.currentAction = Action.DONE
        val highscore = calculateWinner()
        onAllRefreshables { refreshAfterGameEnd(highscore) }

    }

    /**
     * Function that groups scores to the corresponding players
     * @return Map that has scores as keys and matching players as values
     * @throws IllegalStateException if there is no game currently or the game is not in the right action
     */
     fun calculateWinner(): Map<Score, List<WavePlayer>> {
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet" }
        check(game.currentAction == Action.DONE)
        val result: Map<Score, List<WavePlayer>> = game.players.groupBy { it.calculateScore() }
        return result.toSortedMap(reverseOrder())
    }

    /**
     * Checks if the last turn is done
     * To achieve this, it checks if the current turn is the last one
     * It then checks if the current [Action] is [Action.WAIT] to ensure the last player finished his actions
     * @return true/false if the last turn finished
     * @throws IllegalStateException if there is no game
     */
     fun lastTurnFinished(): Boolean {
        val game = rootService.currentGame
        checkNotNull(game) { "No game currently running." }
        return game.currentTurn == game.players.size * game.rounds && game.currentAction == Action.WAIT
    }



    }
