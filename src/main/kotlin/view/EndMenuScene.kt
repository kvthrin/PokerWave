package view

import entity.Score
import entity.WaveCard
import entity.WavePlayer
import service.CardImageLoader
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color

/**
 * Scene displayed at the end of the game, showing scores, player names, and cards.
 */
class EndMenuScene : BoardGameScene(1920, 1080, background = ImageVisual("endMenu.png")), Refreshable {

    // Labels to display players names
    private val firstName = Label(width = 900, height = 35, posX = 70, posY = 240, font = Font(40, color = Color.WHITE),
        alignment = Alignment.CENTER_LEFT)
    private val secondName = Label(width = 900, height = 35, posX = 70, posY = 450, font = Font(40, color = Color.WHITE),
        alignment = Alignment.CENTER_LEFT)
    private val thirdName = Label(width = 900, height = 35, posX = 70, posY = 650, font = Font(40, color = Color.WHITE),
        alignment = Alignment.CENTER_LEFT)
    private val forthName = Label(width = 900, height = 35, posX = 70, posY = 850, font = Font(40, color = Color.WHITE),
        alignment = Alignment.CENTER_LEFT)


    //Layouts to display players cards
    private val firstCards = LinearLayout<CardView>(
        height = 170, width = 750, posX = 660, posY = 160, spacing = 50, alignment = Alignment.CENTER_RIGHT
    )
    private val secondCards = LinearLayout<CardView>(
        height = 170, width = 750, posX = 660, posY = 370, spacing = 50, alignment = Alignment.CENTER_RIGHT
    )
    private val thirdCards = LinearLayout<CardView>(
        height = 170, width = 750, posX = 660, posY = 580, spacing = 50, alignment = Alignment.CENTER_RIGHT
    )
    private val forthCards = LinearLayout<CardView>(
        height = 170, width = 750, posX = 660, posY = 790, spacing = 50, alignment = Alignment.CENTER_RIGHT
    )

    // Button to quiet the game
    val quitButton = Button(
        width = 300, height = 60, posX = 1500, posY = 900, text = "Quit", font = Font(size = 30, color = Color.WHITE)
    ).apply {
        visual = ColorVisual.BLACK
    }

    // Button to start a new game
    val newGameButton = Button(
        width = 300,
        height = 60,
        posX = 1500,
        posY = 800,
        text = "New Game",
        font = Font(size = 30, color = Color.WHITE)
    ).apply {
        visual = ColorVisual(241,84,103,100)
    }

    init { // Add all components to the scene
        addComponents(
            newGameButton,
            quitButton,
            firstName,
            firstCards,
            secondName,
            secondCards,
            thirdName,
            thirdCards,
            forthName,
            forthCards
        )
    }
    // Implement the refresh method to update the scene after the game ends -> Setting the Names,Score, Cards
    override fun refreshAfterGameEnd(players: Map<Score, List<WavePlayer>>) {
        val cardImageLoader = CardImageLoader()
        var count = 1 // Count variable to keep track which nameLabel / cards have to be updated

        for ((score, wavePlayers) in players) { //  Iterating through the pairs
            for (wavePlayer in wavePlayers) { // Iterating through the list
                when (count) {
                    1 -> {
                        // Update the first name label and initialize the corresponding card list view
                        firstName.apply { text = "${wavePlayer.name}: $score" }
                        initializeListView(wavePlayer.openHand + wavePlayer.hiddenHand, firstCards, cardImageLoader)
                    }
                    2 -> {
                        // Update the second name label and initialize the corresponding card list view
                        secondName.apply { text = "${wavePlayer.name}: $score" }
                        initializeListView(wavePlayer.openHand + wavePlayer.hiddenHand, secondCards, cardImageLoader)
                    }
                    3 -> {
                        // Update the third name label and initialize the corresponding card list view
                        thirdName.apply { text = "${wavePlayer.name}: $score" }
                        initializeListView(wavePlayer.openHand + wavePlayer.hiddenHand, thirdCards, cardImageLoader)
                    }
                    4 -> {
                        // Update the forth name label and initialize the corresponding card list view
                        forthName.apply { text = "${wavePlayer.name}: $score" }
                        initializeListView(wavePlayer.openHand + wavePlayer.hiddenHand, forthCards, cardImageLoader)
                    }
                }
                count++
            }
        }
    }

    // Helper method to initialize card views in a linear layout
    private fun initializeListView(
        list: List<WaveCard>,
        listView: LinearLayout<CardView>,
        cardImageLoader: CardImageLoader,
    ) {
        listView.clear()
        list.forEach { waveCard ->
            val cardView = CardView(
                height = 200,
                width = 130,
                front = ImageVisual(cardImageLoader.frontImageFor(waveCard.suit, waveCard.value)),
                back = ImageVisual(cardImageLoader.backImage)
            )
            cardView.flip()
            listView.add(cardView)
        }
    }

}