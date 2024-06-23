package view

import entity.Score
import entity.WavePlayer
import service.RootService
import tools.aqua.bgw.core.BoardGameApplication

/**
 * Application class for the PokerWave game.
 */
class PokerWaveApplication : BoardGameApplication("PokerWave"), Refreshable {

    // Create an instance of RootService to manage game logic and data.
    private val rootService = RootService()

    // Create an instance of EndMenuScene
    private val endMenuScene = EndMenuScene().apply {
        newGameButton.onMouseClicked = {
            this@PokerWaveApplication.showMenuScene(startMenuScene) // Show the start menu scene on new game Button clicked
        }
        quitButton.onMouseClicked = {
            exit()   // Exit the application after game end
        }
    }

    // Create an instance of StartMenuScene and configure event handlers.
    private val startMenuScene = StartMenuScene(rootService).apply {
        quitButton.onMouseClicked = {
            exit() // Exit the application from the start menu.
        }
    }
    // Create an instance of PokerWaveScene
    private val pokerWaveScene = PokerWaveScene(rootService)

    init {
        rootService.addRefreshables(// Add refreshable components to the RootService.
            this,
            endMenuScene,
            pokerWaveScene,
            startMenuScene
        )
        // Show the PokerWaveScene when the application starts so the background is not empty
        this.showGameScene(pokerWaveScene)
        // Show the StartMenuScene with a delay of 0 milliseconds.
        this.showMenuScene(startMenuScene)

    }

    // Implement the Refreshable interface method for post-start game refresh.
    override fun refreshAfterStartNewGame() { //Hide the start menu and show the actual game
        this.hideMenuScene()
        this.showGameScene(pokerWaveScene)
    }

    // Implement the Refreshable interface method for post-game end refresh.
    override fun refreshAfterGameEnd(players: Map<Score, List<WavePlayer>>) {
        this.showGameScene(endMenuScene)
    }

}