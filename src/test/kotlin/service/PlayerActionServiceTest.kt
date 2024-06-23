package service

import entity.Action
import entity.CardSuit
import entity.CardValue
import entity.WaveCard
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import view.Refreshable
import kotlin.test.assertFails

/**
 * Tests for all possible player Actions
 */
internal class PlayerActionServiceTest {

    /**
     * Sets up the game by creating a rootservice object which knows the currentGame
     */
    private fun setUpGame(vararg refreshables: Refreshable): RootService {
        val mc = RootService()
        mc.gameService.startNewGame(2, listOf("Bob", "Bobby"))
        refreshables.forEach { mc.addRefreshable(it) }
        return mc
    }

    /**
     * Tests for the player action shift
     */
    @Test
    fun shift() {
        val testRefreshable = TestRefreshable()
        val mc = setUpGame(testRefreshable)
        val currentGame = mc.currentGame
        checkNotNull(currentGame)

        /**
         * Taking the first card from the stack and replacing it with a choosen card,
         * replacing the middleStack with 3 choosen cards
         */
        currentGame.drawStack.draw(1)
        val testCard = WaveCard(CardSuit.DIAMONDS, CardValue.JACK)
        currentGame.drawStack.putOnTop(testCard)
        currentGame.middleList = mutableListOf(
            WaveCard(CardSuit.HEARTS, CardValue.THREE),
            WaveCard(CardSuit.DIAMONDS, CardValue.ACE),
            WaveCard(CardSuit.SPADES, CardValue.ACE)
        )
        currentGame.currentAction = Action.SHIFT // setting the right Action so that shift Action is valid

        /**
         * Testing for a shift to the left
         */
        mc.playerActionService.shift(true)

        /**
         * Testing whether the right Refreshable is called
         */
        assertTrue(testRefreshable.refreshAfterShiftCalled)
        testRefreshable.reset()

        /**
         * testing whether the card from the drawnstack is in the middleStack now
         * and at the valid position when shifitng left
         * checking all cards shifted accordingly
         * and that the right Action after shifting is called
         * and that the testCard is not on the top of the drawStack anymore
         */
        assertEquals(testCard, currentGame.middleList[2])
        assertEquals(mutableListOf(WaveCard(CardSuit.DIAMONDS, CardValue.ACE),
            WaveCard(CardSuit.SPADES, CardValue.ACE), testCard), currentGame.middleList)
        assertTrue(currentGame.currentAction == Action.SWITCH)
        assertFalse(testCard == currentGame.drawStack.draw(1)[0])

        /**
         * Same tests for shifting right
         */
        currentGame.middleList = mutableListOf(
            WaveCard(CardSuit.HEARTS, CardValue.THREE),
            WaveCard(CardSuit.DIAMONDS, CardValue.ACE),
            WaveCard(CardSuit.SPADES, CardValue.ACE)
        )
        currentGame.drawStack.putOnTop(testCard)
        currentGame.currentAction = Action.SHIFT
        mc.playerActionService.shift(false)

        assertTrue(testRefreshable.refreshAfterShiftCalled)

        assertEquals(testCard, currentGame.middleList[0])
        assertEquals(mutableListOf(testCard, WaveCard(CardSuit.HEARTS, CardValue.THREE),
            WaveCard(CardSuit.DIAMONDS, CardValue.ACE)), currentGame.middleList)
        assertTrue(currentGame.currentAction == Action.SWITCH)
        assertFalse(testCard == currentGame.drawStack.draw(1)[0])

        /**
         * testing that in the wrong Action phase, the shifting is not possible in either left or right
         */
        currentGame.currentAction = Action.DONE
        assertFails { mc.playerActionService.shift(true) }
        assertFails { mc.playerActionService.shift(false) }


    }

    /**
     * Tests for player Action switch
     */
    @Test
    fun switch() {
        val testRefreshable = TestRefreshable()
        val mc = setUpGame(testRefreshable)
        val currentGame = mc.currentGame
        checkNotNull(currentGame)

        /***
         * Creating a choosen hand for the players openHand, hiddenHand and choosen cards for the middleStack
         */
        currentGame.players[0].openHand = (mutableListOf(
            WaveCard(CardSuit.HEARTS, CardValue.ACE),
            WaveCard(CardSuit.DIAMONDS, CardValue.ACE),
            WaveCard(CardSuit.SPADES, CardValue.ACE)
        ))
        currentGame.players[0].hiddenHand =
            (mutableListOf(WaveCard(CardSuit.HEARTS, CardValue.TEN), WaveCard(CardSuit.DIAMONDS, CardValue.TWO)))
        currentGame.middleList = mutableListOf(
            WaveCard(CardSuit.HEARTS, CardValue.THREE),
            WaveCard(CardSuit.DIAMONDS, CardValue.ACE),
            WaveCard(CardSuit.SPADES, CardValue.ACE)
        )
        currentGame.currentAction = Action.SWITCH // setting right Action so shifting is valid
        mc.playerActionService.switch(0, 0)

        /**
         * Checking that the previous middleStack card is in the players openHand now, at the right position
         * Checking that the previous players openHand card is in the middleStack now, at the right positon
         * Checking that the right Action after switching is set
         * Checking that the right Refreshable after switching is called
         */
        assertEquals(WaveCard(CardSuit.HEARTS, CardValue.THREE), currentGame.players[0].openHand[0])
        assertEquals(WaveCard(CardSuit.HEARTS, CardValue.ACE), currentGame.middleList[0])
        assertTrue(currentGame.currentAction == Action.WAIT)
        assertTrue(testRefreshable.refreshAfterSwitchCalled)

        /**
         * Checking that switching with invalid position indices is not possible
         * Checking that switching in the wrong action phase is invalid
         */
        assertFails { mc.playerActionService.switch(10, 10) }
        currentGame.currentAction = Action.DONE
        assertFails { mc.playerActionService.switch(0, 0) }
    }

    /**
     * Tests for player function switchAll
     */
    @Test
    fun switchAll() {
        val testRefreshable = TestRefreshable()
        val mc = setUpGame(testRefreshable)
        val currentGame = mc.currentGame
        checkNotNull(currentGame)

        /***
         * Creating a chosen hand for the players openHand, hiddenHand and choosen cards for the middleStack
         */
        currentGame.players[0].openHand = (mutableListOf(
            WaveCard(CardSuit.HEARTS, CardValue.ACE),
            WaveCard(CardSuit.DIAMONDS, CardValue.ACE),
            WaveCard(CardSuit.SPADES, CardValue.ACE)
        ))
        currentGame.players[0].hiddenHand =
            (mutableListOf(WaveCard(CardSuit.HEARTS, CardValue.TEN), WaveCard(CardSuit.DIAMONDS, CardValue.TWO)))
        currentGame.middleList = mutableListOf(
            WaveCard(CardSuit.HEARTS, CardValue.THREE),
            WaveCard(CardSuit.DIAMONDS, CardValue.ACE),
            WaveCard(CardSuit.SPADES, CardValue.ACE)
        )
        currentGame.currentAction = Action.SWITCH // setting right Action phase
        mc.playerActionService.switchAll()

        /**
         * Checking that players openHand equals previous middleCards
         * Checking that middleCards equal previous openHand cards from the player
         * Checking that right Action is set after calling switch
         * Checking that right Refreshable was called
         */
        assertEquals(
            mutableListOf(
                WaveCard(CardSuit.HEARTS, CardValue.THREE),
                WaveCard(CardSuit.DIAMONDS, CardValue.ACE),
                WaveCard(CardSuit.SPADES, CardValue.ACE)
            ), currentGame.players[0].openHand
        )
        assertEquals(
            mutableListOf(
                WaveCard(CardSuit.HEARTS, CardValue.ACE),
                WaveCard(CardSuit.DIAMONDS, CardValue.ACE),
                WaveCard(CardSuit.SPADES, CardValue.ACE)
            ), currentGame.middleList
        )
        assertTrue(currentGame.currentAction == Action.WAIT)

        assertTrue(testRefreshable.refreshAfterSwitchCalled)

        /**
         * Checking that in wrong phase, switchAll is invalid
         */
        currentGame.currentAction = Action.DONE
        assertFails { mc.playerActionService.switchAll() }

    }

    /**
     * Tests for player action passturn
     */
    @Test
    fun passTurn() {
        val testRefreshable = TestRefreshable()
        val mc = setUpGame(testRefreshable)
        val currentGame = mc.currentGame
        checkNotNull(currentGame)

        currentGame.currentAction = Action.SWITCH // setting right Action phase
        mc.playerActionService.passTurn()

        /**
         * Checking that after passing right Action is called (Shift, since its the next players turn)
         * Checking right Refreshable is called
         */
        assertTrue(currentGame.currentAction == Action.SHIFT)
        assertTrue(testRefreshable.refreshAfterTurnCalled)

        /**
         * Checking that in wrong phase, passturn is invalid
         */
        currentGame.currentAction = Action.DONE
        assertFails { mc.playerActionService.passTurn() }

    }

    /**
     * Tests for player action end turn
     */
    @Test
    fun endTurn() {
        val testRefreshable = TestRefreshable()
        val mc = setUpGame(testRefreshable)
        val currentGame = mc.currentGame
        checkNotNull(currentGame)
        val currentPlayer = currentGame.currentPlayer
        currentGame.currentAction = Action.WAIT
        mc.playerActionService.endTurn()

        /**
         * Checking that after passing right Action is called (Shift, since its the next players turn)
         * Checking right Refreshable is called
         */
        assertNotEquals(currentPlayer, currentGame.currentPlayer)
        assertTrue(currentGame.currentAction == Action.SHIFT)
        assertTrue(testRefreshable.refreshAfterTurnCalled)

        /**
         * Checking that in wrong phase, passturn is invalid
         */
        currentGame.currentAction = Action.DONE
        assertFails { mc.playerActionService.endTurn() }

    }
}