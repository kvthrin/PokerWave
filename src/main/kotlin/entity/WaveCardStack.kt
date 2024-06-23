package entity


/**
 * Data structure that holds [WaveCard] objects and provides stack-like
 * access to them (with e.g. [putOnTop], [draw]).
 * @param initalSize sets the size for the stack
 */

class WaveCardStack(private val initalSize : Int) {
    private val cards: ArrayDeque<WaveCard> = ArrayDeque(initalSize)

    /**
     * the amount of cards in this stack
     */
    val size: Int get() = cards.size

    /**
     * Returns `true` if the stack is empty, `false` otherwise.
     */
    val empty: Boolean get() = cards.isEmpty()

    /**
     * Draws [amount] cards from this stack.
     * @param amount the number of cards to draw; defaults to 1 if omitted.
     * @throws IllegalArgumentException if not enough cards on stack to draw the desired amount.
     */
    fun draw(amount: Int = 1): List<WaveCard> {
        require(amount in 1..cards.size) { "can't draw $amount cards from $cards" }
        return List(amount) { cards.removeFirst() }
    }

    /**
     * returns the top card from the stack *without removing* it from the stack.
     * Use [draw] if you want the card also to be removed.
     */
    fun peek() : WaveCard = cards.first()


        /**
     * provides a view of the full stack contents without changing it. Use [draw]
     * for actually drawing cards from this stack.
     */
    fun peekAll(): List<WaveCard> = cards.toList()


    /**
     * puts a given list of cards on top of this card stack, so that
     * the last element of the passed parameter [cards] will be on top of
     * the stack afterwards.
     */
    fun putOnTop(cards: List<WaveCard>) {
        cards.forEach(this.cards::addFirst)
    }

    /**
     * puts the given card on top of this card stack
     */
    fun putOnTop(card: WaveCard) {
        cards.addFirst(card)
    }

    /**
     * shuffles cards
     */
    fun shuffle() {
        cards.shuffle()
    }


}



