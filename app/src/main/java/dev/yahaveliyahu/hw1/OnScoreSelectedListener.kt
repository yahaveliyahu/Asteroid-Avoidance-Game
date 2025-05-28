package dev.yahaveliyahu.hw1

/*
The role of the interface:
The ScoresFragment calls the interface when the player clicks on a high score
The HighScoresActivity implements the interface, and passes the location to the map
The MapFragment displays the passed location
*/
interface OnScoreSelectedListener {
    fun onScoreSelected(latitude: Double, longitude: Double)
}