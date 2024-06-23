package view

import entity.*

/**
 * Interface for components that can be refreshed during different phases of the game.
 */
interface Refreshable {

    /**
     * Called to refresh components after starting a new game.
     */
    fun refreshAfterStartNewGame() {}

    /**
     * Called to refresh components after shifting middleCards in a specified direction.
     * @param left True if shifting left, false if shifting right.
     */
    fun refreshAfterShift(left: Boolean) {}

    /**
     * Called to refresh components after switching cards.
     */
    fun refreshAfterSwitch() {}

    /**
     * Called to refresh components after the game ends.
     * @param players Map containing scores and corresponding lists of WavePlayers.
     */
    fun refreshAfterGameEnd (players: Map<Score, List<WavePlayer>>) {}

    /**
     * Called to refresh components after a players turn in the game.
     */
    fun refreshAfterTurn() {}
}