package entity

/**
 * Klasse die sich die Hand der Spieler merkt und eine Methode zur Score-Berechnung anbietet
 * @param name speichert den Namen des Spielers, darf nicht "" sein
 * @param openHand speichert die offenen Karten des Spielers,
 * dabei wird beim setten direkt gecheckt ob die Liste eine größe von 3 hat
 * @param hiddenHand speichert die verdeckten Karten des Spielers,
 * dabei wird beim setten direkt gecheckt ob die Liste eine größe von 2 hat
 */
class WavePlayer(val name: String) {
    var openHand: MutableList<WaveCard> = mutableListOf<WaveCard>()
        set(value) {
            require( value.size == 3) {"openHand must require exact 3 elements"}
            field = value
        }

    var hiddenHand: List<WaveCard> = mutableListOf<WaveCard>()
        set(value) {
            require(value.size == 2) {"closedHand must require exact 2 elements"}
            field = value
        }
    init {
        require(name.isNotBlank()) {"Name cannot be blank"}
    }


    /** Erstellt eine Liste mit dem Inhalt von OpenHand und HiddenHand
     * @return MutableList welche den Inhalt aus beiden Händen enthält
     * @param return enthält die fertige Liste
     * */
    private fun appendHands(): MutableList<WaveCard> {
        val result = openHand.toMutableList()
        //require(result.size == 5) {"Not five cards!"}
        for (i in 0 until hiddenHand.size) {
            result.add(hiddenHand[i])
        }
        return result
    }

    /**
     * Rechnet aus welche Art von Pokerhand vorhanden ist
     * Dafür werden weitere unter Methoden abgerufen die auf bestimmte Hände testen und wenn diese true zurückgeben
     * wird das entsprechende Score enum zurückgegeben
     * @return Score enum mit entsprechender Pokerhand-Art
     * @param fullHand enthält alle 5 Karten des Spielers
      */
    fun calculateScore(): Score {
        val fullHand = appendHands()
        if (checkForRoyalFlush(fullHand)) {
            return Score.ROYAL_FLUSH
        } else if (checkForStraightFlush(fullHand)) {
            return Score.STRAIGHT_FLUSH
        } else if (checkForFours(fullHand)) {
            return Score.FOR_OF_A_KIND
        } else if (checkForFullHouses(fullHand)) {
            return Score.FULL_HOUSE
        } else if (checkForFlush(fullHand)) {
            return Score.FLUSH
        } else if (checkForStraight(fullHand)) {
            return Score.STRAIGHT
        } else if (checkForThrees(fullHand)) {
            return Score.THREE_OF_A_KIND
        } else if (checkForTwoPairs(fullHand)) {
            return Score.TWO_PAIR
        } else if (checkForOnePair(fullHand)) {
            return Score.ONE_PAIR
        } else {
            return Score.HIGH_CARD
        }
    }

    /**
     * Die 5 Karten werden auf ihre suits gemapped, so das man eine Liste hat mit allen vorhandenen suits.
     * Danach wird toSet aufgerufen um Duplikate zu entfernen. Haben alle Karten das gleiche Suit
     * sollte demensprechend die Size = 1 sein
     * @return boolean ob es ein Flush ist
     */
    private fun checkForFlush(cards: MutableList<WaveCard>): Boolean {
        return cards.map { it.suit }
            .toSet().size == 1
    }

    /**
     * Sortiert die Fünf Karten und rechnet dann aus ob der ordinale Abstand(Die Stelle in der Reihenfolge)
     * zwischen den Werten nur 1 ist bzw ob wenn man den kleinen Ordinalwert von dem höheren Ordinalwert abzieht,
     * das Ergebnis nur 1 sein darf
     * @return bool ob es ein straight ist
     */
    private fun checkForStraight(cards: MutableList<WaveCard>): Boolean {
        cards.sortBy { it.value }
        for (i in 0 until cards.size - 1) {
            //checks if the ordinal position of the value enemus are not consecutive
            if (cards[i + 1].value.ordinal - cards[i].value.ordinal != 1) {
                return false
            }
        }
        return true
    }


    private fun checkForStraightFlush(cards: MutableList<WaveCard>): Boolean {
        return checkForStraight(cards) && checkForFlush(cards)
    }

    private fun checkForRoyalFlush(cards: MutableList<WaveCard>): Boolean {
        cards.sortBy { it.value }
        print(cards[cards.size - 1].value)
        return checkForStraightFlush(cards) && (cards[cards.size - 1].value == CardValue.ACE)

    }

    /**
     * Gruppiert die Karten nach values und zählt dann mit .eachCount wie oft eine Value vorkommt.
     * Checkt dann ob irgendeine Value 4 mal vorkommt
     */
    private fun checkForFours(cards: MutableList<WaveCard>): Boolean {
        val valueCounts = cards.groupingBy { it.value }
            .eachCount()
        return valueCounts.any { it.value == 4 }
    }

    /**
     * Gruppiert Karten nach values, dann wie oft values vorkommen
     * und dann dass es nur zwei verschiedene arten von values gibt
     */
    private fun checkForFullHouses(cards: MutableList<WaveCard>): Boolean {
        val valueCounts = cards.groupingBy { it.value }.eachCount()
        return valueCounts.size == 2
    }

    /**
     * Gruppiert Karten nach values, zählt wie oft jeder Wert vorkommt
     * und guckt dann ob irgendein value 3 mal vorkommt
     */
    private fun checkForThrees(cards: MutableList<WaveCard>): Boolean {
        val valueCounts = cards.groupingBy { it.value }.eachCount()
        return valueCounts.any { it.value == 3 }
    }

    private fun checkForOnePair(cards: MutableList<WaveCard>): Boolean {
        val valueCounts = cards.groupingBy { it.value }.eachCount()
        return valueCounts.any { it.value == 2 }
    }

    /**
     * Gleiches wie davor nur stellt containsvalue sicher,
     * dass man 2 karten von einer value hat, und eine karte von einer anderen value,
     * dementsprechend da size =3 sein muss, gibt es eine weitere value mit 2 karten
     */
    private fun checkForTwoPairs(cards: MutableList<WaveCard>): Boolean {
        val valueCounts = cards.groupingBy { it.value }.eachCount()
        return valueCounts.size == 3 && valueCounts.containsValue(2) && valueCounts.containsValue(1)
    }
}
