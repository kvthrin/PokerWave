package service

import entity.Score
import entity.WavePlayer
import view.Refreshable

/**
 * [Refreshable] implementation that refreshes nothing, but remembers
 * if a refresh method has been called (since last [reset])
 */
class TestRefreshable: Refreshable {
    var refreshAfterStartNewGameCalled: Boolean = false
        private set

    var refreshAfterShiftCalled: Boolean = false
        private set

    var refreshAfterSwitchCalled: Boolean = false
        private set

    var refreshAfterGameEndCalled: Boolean = false
        private set

    var refreshAfterTurnCalled: Boolean = false
        private set

   // Resets all refreshables to false
    fun reset(){
        val refreshAfterStartNewGameCalled = false

        val refreshAfterShiftCalled = false

        val refreshAfterSwitchCalled = false

        val refreshAfterGameEndCalled = false

        val refreshAfterTurnCalled = false
    }

    override fun refreshAfterStartNewGame() { refreshAfterStartNewGameCalled = true}

    override fun refreshAfterShift(left: Boolean) { refreshAfterShiftCalled = true}

    override fun refreshAfterSwitch() { refreshAfterSwitchCalled = true }

    override fun refreshAfterGameEnd (players: Map<Score, List<WavePlayer>>){ refreshAfterGameEndCalled = true}

    override fun refreshAfterTurn() {refreshAfterTurnCalled = true}
}