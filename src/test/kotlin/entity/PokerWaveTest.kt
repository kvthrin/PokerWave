package entity

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.assertFails

internal class PokerWaveTest {
    var game = PokerWave(3, listOf(WavePlayer("Bob"), WavePlayer("Bobby")))
    @Test // tests if setting the currenturn to invalid values throws error and if valid values are set correctly
    fun setCurrentTurn() {
        assertFails { game.currentTurn = 90 }
        assertFails { game.currentTurn = -90 }
        val number = 3
        game.currentTurn = number
        assertEquals(number, game.currentTurn)
    }


    @Test
    fun setDiscardStack() { // sets if stacks are correctly set
        val testStack = WaveCardStack(50)
        testStack.putOnTop(WaveCard(CardSuit.DIAMONDS, CardValue.TEN))
        game.discardStack = testStack
        assertEquals(testStack, game.discardStack)

    }



}