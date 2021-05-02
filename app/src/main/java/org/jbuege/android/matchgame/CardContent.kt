package org.jbuege.android.matchgame

/**
 * Represents the contents which can be found on the face-up side of a card.
 *
 * Used to initialize or reset a card's view model, such as when starting a new game.
 */
data class CardContent(
    val textContent: String = "",
    val matchId: String
)
