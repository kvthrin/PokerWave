package service

import entity.Action

/**
 * Service layer class that provides the logic for the  possible actions a player
 * can take in PokerWave: shift, switch, switchAll, passTurn and endTurn.
 */

class PlayerActionService(private val rootService: RootService) : AbstractRefreshingService() {
    /**
     * Function for player to either shift the middle cards to the left or right
     * @param left decides whether cards are shiftet to the left (true) or right (false)
     * @throws IllegalStateException if there is no game exisiting or the game is not in the right Action Phase
     */
    fun shift(left: Boolean) {
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet!" } //checks if a game exists
        check(game.currentAction == Action.SHIFT) { "Not in the right Action Phase!" }

        if (left){ // shifting cards to the left
            game.discardStack.putOnTop(game.middleList[0]) // puts card on discard stack
            game.middleList[0] = game.middleList[1] // swapping
            game.middleList[1] = game.middleList[2] // swapping
            game.middleList[2] = game.drawStack.draw()[0] // draws card from middlelist and puts on right position
        }
        else{ // shifting cars to the right
            game.discardStack.putOnTop(game.middleList[0]) // puts card on discard stack
            game.middleList[2] = game.middleList[1]
            game.middleList[1] = game.middleList[0]
            game.middleList[0] = game.drawStack.draw()[0]

        }
        game.currentAction = Action.SWITCH
        onAllRefreshables { refreshAfterShift(left) }
    }

    /**
     * Switches one Card from the MiddleStack with one card from the players' cards
     * To achieve this, the middleCards are temporarily stored in cardHolder
     * @param middleCardIndex position of card to be swapped from the MiddleStack
     * @param openHandIndex position of card to be swapped from the player
     * @throws IllegalStateException if game does not exist or is in the wrong Action state
     * or the Indexes for the Cards are invalid
     */
    fun switch(middleCardIndex: Int, openHandIndex: Int) {
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet!" }

        check(game.currentAction == Action.SWITCH) { "Not in the right Action Phase!" }
        check(openHandIndex in 0..2) { "Invalid Index for OpenHandCards" }
        check(middleCardIndex in 0..2) { "Invalid Index for middleCardIndex" }

        val cardHolder = game.middleList[middleCardIndex]
        game.middleList[middleCardIndex] = game.currentPlayer.openHand[openHandIndex]
        game.currentPlayer.openHand[openHandIndex] = cardHolder

        game.currentAction = Action.WAIT
        onAllRefreshables { refreshAfterSwitch() }
    }

    /**
     * Switches all cards from the middle with the players cards
     * To achieve this the players openHandCards are temporarily stored in cardsHolder
     * @throws IllegalStateException if there is no existing game, the current action phase is wrong
     */
    fun switchAll() {
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet!" }
        check(game.currentAction == Action.SWITCH) { "Not in the right Action Phase!" }

        val cardsHolder = game.currentPlayer.openHand
        game.currentPlayer.openHand = game.middleList
        game.middleList = cardsHolder

        game.currentAction = Action.WAIT
        onAllRefreshables { refreshAfterSwitch() }
    }

    /**
     * Function to pass on Swapping Cards
     * To achieve this, the Action is changed to WAIT (Demanden by endTurn function) and then endTurn() is called
     * @throws IllegalStateException if there is no existing game or the currentAction is wrong
     */
    fun passTurn() {
        val game = rootService.currentGame
        //has to be SWITCH since we automatically get into this action after shifting cards
        checkNotNull(game) { "No game started yet!" }
        game.currentAction = Action.WAIT
        check(game.currentAction == Action.SWITCH || game.currentAction == Action.WAIT) { "Not in the right Action Phase!" }
        endTurn()

    }

    /**
     * Player ends his turn
     * To achieve this the currentAction is set to WAIT and then checked whether it can be continued to the next turn
     * or if its the last turn, endGame() is called
     * @throws IllegalStateException if there is no existing game or the game is not in the correct action phase
     */
    fun endTurn() {
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet!" }
        check(game.currentAction == Action.WAIT) { "Not in the right Action Phase!" }

        if (rootService.gameService.lastTurnFinished()) {
            rootService.gameService.endGame()
        } else {
            game.currentTurn++
            game.currentPlayer =  game.players[(game.currentTurn-1) % game.players.size]
            game.currentAction = Action.SHIFT
            onAllRefreshables { refreshAfterTurn() }
        }

    }
}