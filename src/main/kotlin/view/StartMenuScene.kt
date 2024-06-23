package view


import service.RootService
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color

/**
 * [MenuScene] that is used for starting a new game. It is displayed directly at program start or reached
 * when "new game" is clicked in GameFinishedMenuScene. After providing the names of both players,
 * [startButton] can be pressed. There is also a [quitButton] to end the program.
 */

class StartMenuScene(private val rootService: RootService): MenuScene(995, 1080,
    background = ImageVisual("menuBackground.png")), Refreshable {


        private val headlineLabel = Label(
            width = 300, height = 50, posX = 350, posY = 200,
            text = "Start New Game",
            font = Font(size = 40, color = Color.WHITE)
        )

        // Labels to hold the players names

        private val p1Label = Label(
            width = 200, height = 35,
            posX = 150, posY = 300,
            text = "Player 1:",
            font = Font(size = 30, color = Color.WHITE)
        )

        private val p2Label = Label(
            width = 200, height = 35,
            posX = 150, posY = 370,
            text = "Player 2:",
            font = Font(size = 30, color = Color.WHITE)
        )

        private val p3Label = Label(
            width = 200, height = 35,
            posX = 150, posY = 440,
            text = "Player 3:",
            font = Font(size = 30, color = Color.WHITE)
        ).apply { isVisible = false }

        private val p4Label = Label(
            width = 200, height = 35,
            posX = 150, posY = 510,
            text = "Player 4:",
            font = Font(size = 30, color = Color.WHITE)
        ).apply { isVisible = false }

        // Textfields for the players to enter optionally their names

        private val p1Input: TextField = TextField(
            width = 300, height = 42,
            posX = 350, posY = 299,
            text = "Player1",
            font = Font(size = 26, color = Color.WHITE),
            visual = ColorVisual(0,0,0,130)
        ).apply {
            onKeyTyped = {
                startButton.isDisabled = this.text.isBlank() || p2Input.text.isBlank()
            }
        }

        private val p2Input: TextField = TextField(
            width = 300, height = 42,
            posX = 350, posY = 369,
            text = "Player2",
            font = Font(size = 26, color = Color.WHITE),
            visual = ColorVisual(0,0,0,130)
        ).apply {
            onKeyTyped = {
                startButton.isDisabled = p1Input.text.isBlank() || this.text.isBlank()
            }
        }

        private val p3Input: TextField = TextField(
            width = 300, height = 42,
            posX = 350, posY = 439,
            text = "Player3",
            font = Font(size = 26, color = Color.WHITE),
            visual = ColorVisual(0,0,0,130)
        ).apply {isVisible = false
            onKeyTyped = {
                startButton.isDisabled = p1Input.text.isBlank() || this.text.isBlank()
            }
        }

        private val p4Input: TextField = TextField(
            width = 300, height = 42,
            posX = 350, posY = 509,
            text = "Player4",
            font = Font(size = 26, color = Color.WHITE),
            visual = ColorVisual(0,0,0,130)
        ).apply { isVisible= false
            onKeyTyped = {
                startButton.isDisabled = p1Input.text.isBlank() || this.text.isBlank()
            }
        }

        val quitButton = Button(
            width = 219, height = 94,
            posX = 400, posY = 940,
            visual = ImageVisual("quit.png")
        )


        private val startButton = Button(
            width = 219, height = 94,
            posX = 400, posY = 840,
            visual = ImageVisual("start.png")
        ).apply {
            onMouseClicked = { val playerList = mutableListOf(p1Input.text.trim(), p2Input.text.trim())
                if(p3Label.isVisible){playerList.add(p3Input.text.trim())}; if(p4Label.isVisible){
                    playerList.add(p4Input.text.trim())
                }
                rootService.gameService.startNewGame(roundLabel.text.toInt(), playerList)

            }
        }

        // Labels to let the choose the round amount
        private val roundText = Label(width = 150, height = 80, posX = 320, posY = 650, text = "Rounds:",
            font = Font(size = 30, color = Color.WHITE))
        private val roundLabel = Label(width = 75, height = 80, posX = 480, posY = 650, text = "2",
            font = Font(size = 50, color = Color.WHITE), visual = ColorVisual(0,0,0,130))

        // Button to increase round number
        private val upButton = Button(width = 150, height = 100, posX = 445, posY = 570,
            visual = ImageVisual("arrowUp.png")).apply {
             onMouseClicked = {val higherNumber = roundLabel.text.toInt()+1
                 if(higherNumber <= 7) {roundLabel.text = "$higherNumber"}}
        }

        // Button to decrease round number
        private val downButton = Button(width = 150, height = 100, posX = 442, posY = 715,
            visual = ImageVisual("arrowDown.png")).apply {
             onMouseClicked = {val lowerNumber = roundLabel.text.toInt()-1
                 if(lowerNumber >= 2) {roundLabel.text = "$lowerNumber"}}
        }

        // Adding Player amount, reveals the Label and the Input field for the name
        private val addPlayerButton : Button = Button (width = 50, height = 50, posX = 650, posY = 290,
            visual = ImageVisual("plus.png")).apply { onMouseClicked = {
            if(p3Label.isVisible && p3Input.isVisible){
                p4Label.isVisible = true; p4Input.isVisible = true ; this.isVisible = false }else{
                p3Input.isVisible = true ; p3Label.isVisible = true ; removePlayerButton.isVisible = true
            }
        } }

        // Removes the last added player, will turn Name label and Input field invisible
        private val removePlayerButton =  Button (width = 50, height = 50, posX = 690, posY = 290,
            visual = ImageVisual("minus.png")).apply {isVisible = false; onMouseClicked =
            {if(p4Input.isVisible){
                p4Input.isVisible = false ; p4Label.isVisible = false; addPlayerButton.isVisible = true
            } else{
                p3Input.isVisible =false; p3Label.isVisible = false; this.isVisible = false
            }
            }
        }

        init {
            addComponents(
                headlineLabel,
                p1Label, p1Input,
                p2Label, p2Input,
                p3Label, p4Label, p3Input,
                p4Input, addPlayerButton, roundText,
                startButton, quitButton, roundLabel, upButton, downButton, removePlayerButton
            )
        }

}

