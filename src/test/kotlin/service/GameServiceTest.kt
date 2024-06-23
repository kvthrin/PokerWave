package service

import entity.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import view.Refreshable
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

/**
 * Tests for methods that the game service provides
 */
internal class GameServiceTest {

    private fun setUpGame(vararg refreshables: Refreshable): RootService {
        val mc = RootService()

        refreshables.forEach { mc.addRefreshable(it) }
        return mc
    }

    /**
     * Tests to make sure creating a game works
     */
    @Test
    fun startNewGame() {
        val testRefreshable = TestRefreshable()
        val mc = setUpGame(testRefreshable)

        val roundsNumber = 4
        val names = listOf("Bob", "Bobby")
        mc.gameService.startNewGame(roundsNumber, names)
        val currentGame = mc.currentGame

        /**
         * Checking the game exists and is not null
         * Checking the right amount of sets and players are set
         */
        assertNotNull(currentGame)
        assertEquals(roundsNumber, currentGame!!.rounds)
        assertTrue(currentGame.players.any { it.name == "Bob" })
        assertTrue(currentGame.players.any { it.name == "Bobby" })
        assertEquals(2, currentGame.players.size)

        /**
         * Checking that the drawStack, middleStack, discardStack have the right amount of cards in it
         */
        assertEquals(52 - 5 * currentGame.players.size - 3, currentGame.drawStack.size)
        assertEquals(3, currentGame.middleList.size)
        assertEquals(0, currentGame.discardStack.size)

        /**
         * Checking the right refreshable got called
         */
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)

        /**
         * Checking that with invalid arugments (too many rounds, not enough players) the method fails
         */
        assertFails { mc.gameService.startNewGame(90, listOf("Bob")) }
    }

    /**
     * Tests to ensure filling a stack with cards works correctly
     */
    @Test
    fun fillCardStack(){
        val testRefreshable = TestRefreshable()
        val mc = setUpGame(testRefreshable)

        val testStack = WaveCardStack(52)
        mc.gameService.fillCardStack(testStack) // filling the test Stack
        val testStackContent = testStack.draw(52) // getting the content of the test stack

        /**
         *  Checking whether the cards have exactly 4 types of suits
         *  Checking whether the cards have exactly 13 types of values
         *  Checking that each suit has exactly 13 cards
         */
        assertEquals(4, testStackContent.groupBy { it.suit }.keys.size)
        assertEquals(13, testStackContent.groupBy { it.value }.keys.size)
        testStackContent.groupBy { it.suit }.values.forEach { suitGroup -> assertEquals(13, suitGroup.size)}
    }

    /**
     * Tests to ensure endGame function works properly as well as the lastTurnFinished method
     */
    @Test
    fun endGame() {
        val testRefreshable = TestRefreshable()
        val mc = setUpGame(testRefreshable)

        mc.gameService.startNewGame(2, listOf("Bob","Bobby") )
        val currentGame = mc.currentGame
        checkNotNull(currentGame)
        currentGame.currentTurn = 4 // setting currentTurn as the last turn of the game
        currentGame.currentAction = Action.WAIT // setting right Action phase

        /**
         *  Checking whether lastTurnfinished function returns true, as it should
         */
        assertTrue(mc.gameService.lastTurnFinished())

        mc.gameService.endGame()

        /**
         * checking whether right Action is set after the method call of endGame and the right Refreshable is called
         */
        assertEquals(Action.DONE, currentGame.currentAction)
        assertTrue(testRefreshable.refreshAfterGameEndCalled)

        /**
         * Checking that the method throws Exceptions when not in the last turn or Action
         */
        currentGame.currentTurn = 1
        assertFailsWith<IllegalStateException> {mc.gameService.endGame()}
        currentGame.currentTurn = 4
        currentGame.currentAction = Action.SHIFT
        assertFailsWith<IllegalStateException> {mc.gameService.endGame()}

    }


    /**
     * Tests to ensure calculating the winner works properöy
     */
    @Test
    fun calculateWinner() {
        val testRefreshable = TestRefreshable()
        val mc = setUpGame(testRefreshable)

        mc.gameService.startNewGame(2, listOf("Bob","Bobby","Bobbo") )
        val currentGame = mc.currentGame
        checkNotNull(currentGame)

        /**
         * Creating hands for the players
         */
        currentGame.players[0].openHand = (mutableListOf(WaveCard(CardSuit.HEARTS,CardValue.ACE),
            WaveCard(CardSuit.DIAMONDS,CardValue.ACE), WaveCard(CardSuit.SPADES,CardValue.ACE)))
        currentGame.players[0].hiddenHand =  (mutableListOf(WaveCard(CardSuit.HEARTS,CardValue.TEN)
            ,WaveCard(CardSuit.DIAMONDS,CardValue.TWO)))
        currentGame.players[1].openHand = (mutableListOf(WaveCard(CardSuit.HEARTS,CardValue.ACE),
            WaveCard(CardSuit.DIAMONDS,CardValue.FIVE), WaveCard(CardSuit.SPADES,CardValue.THREE)))
        currentGame.players[1].hiddenHand =  (mutableListOf(WaveCard(CardSuit.HEARTS,CardValue.TEN),
            WaveCard(CardSuit.DIAMONDS,CardValue.TWO)))
        currentGame.players[2].openHand = (mutableListOf(WaveCard(CardSuit.HEARTS,CardValue.ACE),
            WaveCard(CardSuit.DIAMONDS,CardValue.ACE), WaveCard(CardSuit.SPADES,CardValue.THREE)))
        currentGame.players[2].hiddenHand =  (mutableListOf(WaveCard(CardSuit.HEARTS,CardValue.TEN),
            WaveCard(CardSuit.DIAMONDS,CardValue.TWO)))
        currentGame.currentAction = Action.DONE // setting right Action
        var result = mc.gameService.calculateWinner()

        /**
         * Checking right Scores are returned in the proper order
         */
        assertEquals(result.keys.first(), Score.THREE_OF_A_KIND)
        assertEquals(result.keys.elementAt(1), Score.ONE_PAIR)
        assertEquals(result.keys.elementAt(2),Score.HIGH_CARD)

        /**
         * Checking when two players have the same hand, they get grouped to the same score
         */

        currentGame.players[1].openHand = (mutableListOf(WaveCard(CardSuit.HEARTS,CardValue.ACE),
            WaveCard(CardSuit.DIAMONDS,CardValue.ACE), WaveCard(CardSuit.SPADES,CardValue.ACE)))
        currentGame.players[1].hiddenHand =  (mutableListOf(WaveCard(CardSuit.HEARTS,CardValue.TEN),
            WaveCard(CardSuit.DIAMONDS,CardValue.TWO)))
        result = mc.gameService.calculateWinner()
        assertEquals(2, result.keys.size)

        /**
         * checking that in the wrong Action ( the game did not end yet) the method call fails
         */
        currentGame.currentAction = Action.SHIFT
        assertFailsWith<IllegalStateException> { mc.gameService.calculateWinner() }
    }

}