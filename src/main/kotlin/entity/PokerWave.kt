package entity

/**
 * Enthält Basics für das Spiel
 * @param rounds speichert wie viele Runden. Es wird gecheckt beim initialisieren ob die Rundenanzahl zwischen 2 und 7 ist.
 * @param playersName speichert eine Liste mit den Spielernahmen
 * @param currentTurn speichert den momentanen Turn.
 * @param currentPlayer speichert den momentanen Player. Kann null sein falls das Spiel gerade erst initalisiert wird
 * @param discardStack speichert abgelegte Karten
 * @param players: speichert Waveplayers, können beim initalisieren Null sein
 * @param drawStack: Speichert Karten die noch gezogen werden können.
 * @param middleList: Speichert die 3 Karten in der Mitte
 * @param currentAction: Speichert welche Action gerade läuft
 */
class PokerWave (val rounds: Int, val players: List<WavePlayer>){
    var currentTurn : Int = 1
        set(value) {
            check(value in 0.. rounds*players.size)
            field = value
        }
    var currentPlayer = players[(currentTurn-1) % players.size]
    var discardStack : WaveCardStack = WaveCardStack(0)
        set(value) {
            check(value.size in 1..52)
            field = value
        }
    var drawStack : WaveCardStack = WaveCardStack(52 - players.size*5-3)
        set(value) {
            check(value.size in 1..(52-players.size*5-3))
            field = value
        }
    var middleList: MutableList<WaveCard> = mutableListOf<WaveCard>()
        set(value) {
            check(value.size == 3)
            field = value
        }
    var currentAction : Action = Action.SHIFT

    init {
        //ensuring of valid numbers
        check(rounds in (2..7))
        check(players.size in (2..4))

    }
}