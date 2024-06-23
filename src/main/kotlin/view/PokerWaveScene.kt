package view

import entity.Action
import entity.WaveCard
import entity.WaveCardStack
import service.CardImageLoader
import service.RootService
import tools.aqua.bgw.animation.FlipAnimation
import tools.aqua.bgw.animation.MovementAnimation
import tools.aqua.bgw.animation.ParallelAnimation
import tools.aqua.bgw.components.container.CardStack
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.BidirectionalMap
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color

/**
 * [PokerWaveScene] class represents the main scene of the PokerWave game.
 */
class PokerWaveScene(private val rootService: RootService) :
    BoardGameScene(1920, 1080, background = ImageVisual("BackgroundTable.png")), Refreshable {

    //Lists with booleans to keep track if a card got selected for switching phase.
    // True = Selected card index equals the "true" index
    private var selectedOpenCard = mutableListOf(false, false, false)
    private var selectedMiddleCard = mutableListOf(false, false, false)

    //variable to save whether right / left player cards have been already turned for 90 degrees
    // else they will turn with each new game triggered by the ending screen

    private var areTurned = false

    // displays the current round
    private var currentRoundLabel =
        Label(20, 20, 400, 70, text = "Current Round : 1", font = Font(size = 40, color = Color.WHITE))
    // displays the current player
    private var currentPlayerLabel = Label(10, 65, 400, 70, font = Font(size = 40, color = Color.WHITE))

    // Maps WaveCard to their corresponding CardView
    private val cardMap: BidirectionalMap<WaveCard, CardView> = BidirectionalMap()

    //Linear Layouts to hold players cards
    private var currentPlayerOpenHand = LinearLayout<CardView>(
        height = 220,
        width = 400,
        posX = 760,
        posY = 750,
        spacing = -30,
        alignment = Alignment.CENTER
    )
    private var currentPlayerHiddenHand = LinearLayout<CardView>(
        height = 220,
        width = 800,
        posX = 660,
        posY = 750,
        spacing = -30,
        alignment = Alignment.CENTER_RIGHT
    ).apply {
        // Making cards flip on mouse pressed
        onMousePressed = { forEach { cardView -> playAnimation(FlipAnimation(cardView,
            cardView.backVisual, cardView.frontVisual)) } }
        onMouseReleased = { forEach { cardView -> playAnimation(FlipAnimation(cardView,
            cardView.frontVisual, cardView.backVisual)) } }
    }

    //2nd player
    private var rightPlayerOpenHand = LinearLayout<CardView>(
        height = 220,
        width = 400,
        posX = 1500,
        posY = 430,
        spacing = -30,
        alignment = Alignment.CENTER
    )
    //3rd player
    private var oppositePlayerOpenHand = LinearLayout<CardView>(
        height = 220,
        width = 400,
        posX = 760,
        posY = 50,
        spacing = -30,
        alignment = Alignment.CENTER
    )
        set(value) {
            // Ensuring opposite player can only be set if there's 3 players
            if (rootService.currentGame!!.players.size == 3) field = value
        }

    // 4th player
    private var leftPlayerOpenHand = LinearLayout<CardView>(
        height = 220,
        width = 400,
        posX = 150,
        posY = 430,
        spacing = -30,
        alignment = Alignment.CENTER
    )
        set(value) {
            // Ensuring leftPlayer can only be set when there's 4 players
            if (rootService.currentGame!!.players.size == 4) field = value
        }


    //middle cards
    private var middleCards = LinearLayout<CardView>(
        height = 220,
        width = 800,
        posX = 560,
        posY = 400,
        spacing = -30,
        alignment = Alignment.CENTER
    )

    // discardStack
    private var discardStack =
        CardStack<CardView>(height = 220, width = 200, posX = 550, posY = 400, alignment = Alignment.CENTER)

    //drawStack
    private var drawStack =
        CardStack<CardView>(height = 220, width = 200, posX = 1150, posY = 400, alignment = Alignment.CENTER)

    // Button to swap cards in switching Phase
    private val swapAllCardsButton = Button(
        width = 300,
        height = 60,
        posX = 1500,
        posY = 830,
        text = "SWAP ALL",
        visual = ColorVisual.BLACK,
        font = Font(size = 30, color = Color.WHITE)
    ).apply {
        isVisible = false
        onMouseClicked = {
            rootService.playerActionService.switchAll()
            swapButton.isVisible = false
            isVisible = false
        }
    }

    // Button to only swap 1 card in the switching Phase
    private val swapButton : Button = Button(
        width = 300,
        height = 60,
        posX = 1500,
        posY = 760,
        text = "SWAP",
        visual = ColorVisual.BLACK,
        font = Font(size = 30, color = Color.WHITE)
    ).apply {
        isVisible = false; this.onMouseClicked = {
        rootService.currentGame?.let {
            if (selectedOpenCard.contains(true) && selectedMiddleCard.contains(true)) {rootService.playerActionService.switch(
                selectedOpenCard.indexOf(true),
                selectedMiddleCard.indexOf(true))
                swapAllCardsButton.isVisible = false
                isVisible = false }
        }
    }
    }

    // Button to pass / end the turn
    private val passTurnButton = Button(
        width = 300,
        height = 60,
        posX = 1500,
        posY = 900,
        text = "END TURN",
        visual = ColorVisual.BLACK,
        font = Font(size = 30, color = Color.WHITE)
    ).apply {
        isVisible = false; onMouseClicked = {
        rootService.currentGame?.let { rootService.playerActionService.passTurn() }
    }
    }

    // Button to shift the middle cards to the left in the shifting phase
    private val leftShiftButton =
        Button(posX = 800, posY = 630, visual = ImageVisual("Right.png"), width = 75, height = 75).apply {
            onMouseClicked = {
                rootService.currentGame?.let { game ->
                    if (game.currentAction == Action.SHIFT) {
                        rootService.playerActionService.shift(true)
                    }
                }
            }
        }

    // Button to shift the middle cards to the right in the shifting phase
    private val rightShiftButton =
        Button(posX = 1050, posY = 630, visual = ImageVisual("Left.png"), width = 75, height = 75).apply {
            onMouseClicked = {
                rootService.currentGame?.let { game ->
                    if (game.currentAction == Action.SHIFT) {
                        rootService.playerActionService.shift(false)
                    }
                }
            }
        }

    //Button that can be pressed to open help screen
    private val helpButton = Button(posX = 1780, posY = 15, visual = ImageVisual("helpIcon.png"),
        width = 75, height = 75).apply {
        onMousePressed = { helpScreen.isVisible = true}; onMouseReleased = {helpScreen.isVisible = false}
    }

    private val helpScreen = Label(posX = 0, posY = 0, height= 1080, width = 1920,
        visual = ImageVisual("helpScreen.png")).apply { isVisible = false }


    init {    // Adding all components to the scene
        addComponents(
            currentPlayerHiddenHand,
            currentPlayerOpenHand,
            leftPlayerOpenHand,
            rightPlayerOpenHand,
            oppositePlayerOpenHand,
            middleCards,
            discardStack,
            drawStack,
            swapAllCardsButton,
            swapButton,
            rightShiftButton,
            leftShiftButton,
            passTurnButton,
            currentRoundLabel,
            currentPlayerLabel,
            helpScreen,
            helpButton
        )
    }

    // Method to override components after a new game was started
    override fun refreshAfterStartNewGame() {
        val game = rootService.currentGame
        checkNotNull(game) { "No started game found." }
        val cardImageLoader = CardImageLoader()

        // Ensuring Buttons for switching phase are not visible
        passTurnButton.isVisible = false
        swapButton.isVisible = false
        swapAllCardsButton.isVisible = false

        // Ensuring Button for shift phase are visible
        leftShiftButton.isVisible = true
        rightShiftButton.isVisible = true

        // Initializing the cards linearlayouts with the corresponding cardviews of the players cards
        initializeStackView(game.discardStack, discardStack, cardImageLoader)
        initializeStackView(game.drawStack, drawStack, cardImageLoader)
        initializeListView(game.middleList, middleCards, cardImageLoader, true)
        initializeListView(game.currentPlayer.openHand, currentPlayerOpenHand, cardImageLoader, true)
        initializeListView(game.currentPlayer.hiddenHand, currentPlayerHiddenHand, cardImageLoader, false)
        initializeListView(
            game.players[1].openHand, rightPlayerOpenHand,
            cardImageLoader, true
        )
        // rotating cards from the players that are on the left/right

        if(!areTurned){
            rightPlayerOpenHand.rotate(-90)
            leftPlayerOpenHand.rotate(90)
            areTurned = true
        }
        if (game.players.size >= 3) { // Only initializing cards for the 3rd player if he exists
            initializeListView(
                game.players[2].openHand, oppositePlayerOpenHand,
                cardImageLoader, true
            )
        }
        if (game.players.size >= 4) {// Only initializing cards for the 4th player if he exists
            initializeListView(
                game.players[3].openHand, leftPlayerOpenHand,
                cardImageLoader, true
            )
        }
        discardStack.clear()
        currentPlayerLabel.apply { text = game.currentPlayer.name + " ♥" } // Marking start player

    }

    // Method to update the discardStack and middleCards after cards were shifted
    override fun refreshAfterShift(left: Boolean) {
        val game = rootService.currentGame
        checkNotNull(game) { "No game found." }
        val imageLoader = CardImageLoader()
        if (left) {
            moveCardView(middleCards.elementAt(0), discardStack)
        } else {
            moveCardView(middleCards.elementAt(2), discardStack)
        }
        middleCards.forEach{playAnimation(FlipAnimation(it, it.frontVisual, it.backVisual,500))}
        initializeListView(game.middleList, middleCards, imageLoader, false)
        middleCards.forEach{playAnimation(FlipAnimation(it, it.backVisual, it.frontVisual, 500))}
        rightShiftButton.isVisible = false
        leftShiftButton.isVisible = false

        // Calls helper method that makes the currentPlayers cards & middle cards selectable
        prepareForSwitchingPhase()
    }

    // Method to update middleCards and currentPlayers openHand cards after switching
    override fun refreshAfterSwitch() {
        val cardImageLoader = CardImageLoader()
        val game = rootService.currentGame
        checkNotNull(game) { "No game found." }
        middleCards.forEach{playAnimation(FlipAnimation(it, it.frontVisual, it.backVisual,500))}
        currentPlayerOpenHand.forEach{playAnimation(FlipAnimation(it, it.frontVisual, it.backVisual,500))}
        initializeListView(game.middleList, middleCards, cardImageLoader, false)
        initializeListView(game.currentPlayer.openHand, currentPlayerOpenHand, cardImageLoader, false)
        middleCards.forEach{playAnimation(FlipAnimation(it, it.backVisual, it.frontVisual,500))}
        currentPlayerOpenHand.forEach{playAnimation(FlipAnimation(it, it.backVisual, it.frontVisual,500))}
    }


    // Updates all players cards to simulate the rotation of cards
    override fun refreshAfterTurn() {
        val game = rootService.currentGame
        checkNotNull(game) { "No game found." }
        val cardImageLoader = CardImageLoader()

        currentPlayerOpenHand.forEach{playAnimation(FlipAnimation(it, it.frontVisual, it.backVisual,500))}
        rightPlayerOpenHand.forEach{playAnimation(FlipAnimation(it, it.frontVisual, it.backVisual,500))}

        initializeListView(game.currentPlayer.openHand, currentPlayerOpenHand, cardImageLoader, false)
        initializeListView(game.currentPlayer.hiddenHand, currentPlayerHiddenHand, cardImageLoader, false)
        initializeListView(
            game.players[(game.players.indexOf(game.currentPlayer) + 1) % game.players.size].openHand,
            rightPlayerOpenHand,
            cardImageLoader,
            false
        )
        if (game.players.size >= 3) {
            oppositePlayerOpenHand.forEach{playAnimation(FlipAnimation(it, it.frontVisual, it.backVisual,500))}

            initializeListView(
                game.players[(game.players.indexOf(game.currentPlayer) + 2) % game.players.size].openHand,
                oppositePlayerOpenHand,
                cardImageLoader,
                false
            )

            oppositePlayerOpenHand.forEach{playAnimation(FlipAnimation(it, it.backVisual, it.frontVisual,500))}
        }
        if (game.players.size >= 4) {
            leftPlayerOpenHand.forEach{playAnimation(FlipAnimation(it, it.frontVisual, it.backVisual,500))}

            initializeListView(
                game.players[(game.players.indexOf(game.currentPlayer) + 3) % game.players.size].openHand,
                leftPlayerOpenHand,
                cardImageLoader,
                false
            )
            leftPlayerOpenHand.forEach{playAnimation(FlipAnimation(it, it.backVisual, it.frontVisual,500))}
        }
        currentPlayerOpenHand.forEach{playAnimation(FlipAnimation(it, it.backVisual, it.frontVisual,500))}
        rightPlayerOpenHand.forEach{playAnimation(FlipAnimation(it, it.backVisual, it.frontVisual,500))}

        // Also has to update the middleList, else it leads to a bug where selected middleCards stay up after the turn
        initializeListView(game.middleList, middleCards, cardImageLoader, true)

        // Updating helper variables
        selectedOpenCard.fill(false)
        selectedMiddleCard.fill(false)

        // Updating Button visibility
        passTurnButton.isVisible = false
        swapButton.isVisible = false
        swapAllCardsButton.isVisible = false
        leftShiftButton.isVisible = true
        rightShiftButton.isVisible = true

        // Updating the currentRoundLabel
        currentRoundLabel.apply {
            val currentRound = (game.currentTurn - 1) / game.players.size + 1; text = "Current Round : $currentRound"
        }
        // Updating the currentPlayer Label
        currentPlayerLabel.apply {
            text = if (game.currentPlayer != game.players[0]) game.currentPlayer.name else {
                game.currentPlayer.name + " ♥"
            }
        }
    }

    // Helper methods to initialize the drawStack with cardsViews
    private fun initializeStackView(
        stack: WaveCardStack,
        stackView: CardStack<CardView>,
        cardImageLoader: CardImageLoader
    ) {
        stackView.clear()
        stack.peekAll().reversed().forEach { card ->
            val cardView = CardView(
                height = 200,
                width = 130,
                front = ImageVisual(cardImageLoader.frontImageFor(card.suit, card.value)),
                back = ImageVisual(cardImageLoader.backImage)
            )
            stackView.add(cardView)
            cardMap.add(card to cardView)
        }
    }

    //Helper methods initialize all used lists like the players hands with the cardviews
    private fun initializeListView(
        list: List<WaveCard>,
        listView: LinearLayout<CardView>,
        cardImageLoader: CardImageLoader,
        flip: Boolean
    ) {
        listView.clear()
        list.forEach { waveCard ->
            val cardView = CardView(
                height = 200,
                width = 130,
                front = ImageVisual(cardImageLoader.frontImageFor(waveCard.suit, waveCard.value)),
                back = ImageVisual(cardImageLoader.backImage)
            )
            if (flip) cardView.flip()
            listView.add(cardView)
            cardMap.add(waveCard to cardView)
        }
    }

    // Moves a cardview to a different stack
    private fun moveCardView(cardView: CardView, toStack: CardStack<CardView>?) {
        cardView.removeFromParent()
        toStack?.add(cardView)
    }

    //Helper method that makes the given cards selectable, selecting cards update the Boolean lists
    private fun makeCardsSelectable(cards: LinearLayout<CardView>, selected: MutableList<Boolean>) {
        for (i in 0 until cards.components.size) {
            cards.elementAt(i).apply {
                onMouseClicked = {
                    val game = checkNotNull(rootService.currentGame)
                    if (game.currentAction == Action.SWITCH) { // ensuring right action phase

                        // If one card is already selected, deselect it and select the clicked one
                        if (selected.contains(true) && selected.indexOf(true) != i) {
                            playAnimation(
                                ParallelAnimation(
                                    // resetting the previous selected card position
                                    MovementAnimation(cards.elementAt(selected.indexOf(true)), 0, 0),
                                    MovementAnimation(cards.elementAt(i), 0, -50)
                                )
                            )
                            selected[selected.indexOf(true)] = false // deselecting the previous selected card
                            selected[i] = true
                        } else if (selected[i]) { // if the same card is selected again, it gets deselected
                            playAnimation(MovementAnimation(cards.elementAt(i), 0, 0))
                            selected[i] = false
                        } else { // first card to be selected
                            playAnimation(MovementAnimation(cards.elementAt(i), 0, -50))
                            selected[i] = true
                        }
                    }
                }
            }
        }
    }

    // helper method to call the cardsSelectable method on middleCards and the players hands. Also sets button visibilities
    private fun prepareForSwitchingPhase() {

        makeCardsSelectable(currentPlayerOpenHand, selectedOpenCard)
        makeCardsSelectable(middleCards, selectedMiddleCard)
        passTurnButton.isVisible = true
        swapButton.isVisible = true
        swapAllCardsButton.isVisible = true
    }


}

