package org.jbuege.android.matchgame

/**
 * Represents an entire themed deck of cards, formed of matching pairs.
 */
interface IDeck {

    /**
     * Randomly select the specified number of matching pairs from the deck.
     */
    fun randomPairs(pairs: Int): List<CardContent>
}