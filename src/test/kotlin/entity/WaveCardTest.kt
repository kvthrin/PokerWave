package entity

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.test.assertFailsWith

internal class WaveCardTest {

    @Test // Tested ob richtige Werte der Karte hinzugefügt werden
    fun createCards() {
        val suit = CardSuit.CLUBS
        val value = CardValue.FIVE
        val card = WaveCard(suit, value)
        assertEquals(suit, card.suit)
        assertEquals(value, card.value)
    }
}
