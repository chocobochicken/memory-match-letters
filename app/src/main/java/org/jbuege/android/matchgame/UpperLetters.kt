package org.jbuege.android.matchgame

/**
 * Deck of upper case letters, matched to an identical letter.
 */
class UpperLetters : IDeck {

    override fun randomPairs(pairs: Int): List<CardContent> {
        val selectedContent = ('A'..'Z')
            .map { it.toString() }
            .shuffled()
            .subList(0, pairs)

        val selectedPairs = mutableListOf<String>()
        selectedPairs.addAll(selectedContent)
        selectedPairs.addAll(selectedContent)

        return selectedPairs
            .map { letter -> CardContent(textContent = letter, matchId = letter) }
            .shuffled()
    }

}