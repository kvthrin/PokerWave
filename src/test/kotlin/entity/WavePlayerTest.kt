package entity

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.test.assertFails
import kotlin.test.assertFailsWith


internal class WavePlayerTest {
    var player = WavePlayer("Bob")
    val card1 = WaveCard(CardSuit.CLUBS, CardValue.ACE)
    val card2 = WaveCard(CardSuit.CLUBS, CardValue.JACK)
    val card3 = WaveCard(CardSuit.CLUBS, CardValue.QUEEN)
    val card4 = WaveCard(CardSuit.CLUBS, CardValue.KING)
    val card5 = WaveCard(CardSuit.CLUBS, CardValue.TEN)
    val card6 = WaveCard(CardSuit.CLUBS, CardValue.NINE)
    var card7 = WaveCard(CardSuit.HEARTS, CardValue.ACE)
    var card8 = WaveCard(CardSuit.SPADES, CardValue.ACE)
    var card9 = WaveCard(CardSuit.DIAMONDS, CardValue.ACE)
    var card10 = WaveCard(CardSuit.SPADES, CardValue.JACK)
    var card11 = WaveCard(CardSuit.HEARTS, CardValue.TEN)
    var card12 = WaveCard(CardSuit.HEARTS, CardValue.TWO)
    var card13 = WaveCard(CardSuit.DIAMONDS, CardValue.THREE)


    @Test // Tested ob die richtigen Karten dann auch auf der Hand sind bzw ob bei zu vielen Karten ein Error gethrowed wird
    fun setOpenHand() {
        val testList = mutableListOf(card1, card2, card3)
        player.openHand = mutableListOf(card1, card2, card3)
        assertEquals(testList, player.openHand)

        assertFailsWith<java.lang.IllegalArgumentException> {
            player.openHand = mutableListOf(card1, card2, card3, card4)
        }

    }

    @Test // Geiches wie oben aber in der Hidden Hand
    fun setHiddenHand() {
        val testList = mutableListOf(card1, card2)
        player.hiddenHand = mutableListOf(card1, card2)
        assertEquals(testList, player.hiddenHand)

        assertFailsWith<java.lang.IllegalArgumentException> {
            player.hiddenHand = mutableListOf(card1, card2, card3, card4)
        }
    }

    @Test // Testet ob Scores richtig zugeordnet werden
    fun calculateScore() {
        //check for Royal Flush
        player.openHand = mutableListOf(card1, card2, card3)
        player.hiddenHand = mutableListOf(card4, card5)
        assertEquals(Score.ROYAL_FLUSH, player.calculateScore())
        //check for Straight Flush
        player.openHand = mutableListOf(card2, card3, card4)
        player.hiddenHand = mutableListOf(card5, card6)
        assertEquals(Score.STRAIGHT_FLUSH, player.calculateScore())
        //check for four of a kind
        player.openHand = mutableListOf(card1, card6, card7)
        player.hiddenHand = mutableListOf(card8, card9)
        assertEquals(Score.FOR_OF_A_KIND, player.calculateScore())
        //check for full house
        player.openHand = mutableListOf(card1, card7, card8)
        player.hiddenHand = mutableListOf(card2, card10)
        assertEquals(Score.FULL_HOUSE, player.calculateScore())
        //Check for Flush
        player.openHand = mutableListOf(card1, card2, card3)
        player.hiddenHand = mutableListOf(card4, card6)
        assertEquals(Score.FLUSH, player.calculateScore())
        //Check for straight
        player.openHand = mutableListOf(card11, card10, card9)
        player.hiddenHand = mutableListOf(card4, card3)
        assertEquals(Score.STRAIGHT, player.calculateScore())
        //check for three of a kind
        player.openHand = mutableListOf(card1, card7, card8)
        player.hiddenHand = mutableListOf(card10, card11)
        assertEquals(Score.THREE_OF_A_KIND, player.calculateScore())
        //check for two pair
        player.openHand = mutableListOf(card1, card7, card10)
        player.hiddenHand = mutableListOf(card5, card11)
        assertEquals(Score.TWO_PAIR, player.calculateScore())
        //check for one pair
        player.openHand = mutableListOf(card1, card7, card10)
        player.hiddenHand = mutableListOf(card5, card6)
        assertEquals(Score.ONE_PAIR, player.calculateScore())
        //check for highest card
        player.openHand = mutableListOf(card11, card10, card9)
        player.hiddenHand = mutableListOf(card12, card13)
        assertEquals(Score.HIGH_CARD, player.calculateScore())

    }


    @Test // Testet ob richtiger Name auch zugeordnet wird und das bei "" ein Error gethrowed wird
    fun NameTest() {
        val name = "Bobby"
        val player2 = WavePlayer(name)
        assertEquals(name, player2.name)

        assertFails { WavePlayer("") }
    }


}