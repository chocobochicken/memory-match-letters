package org.jbuege.android.matchgame

import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

typealias Row = List<CardViewModel>
typealias Grid = List<Row>

/**
 * Represents the collection of views for the entire game board.
 */
class GameFragmentViewModel(
    rows: Int,
    cols: Int,
    deck: List<CardContent>) : ViewModel() {

    val grid = createGrid(rows, cols, deck)
    private val totalPairs = rows * cols / 2
    private var matchedPairs = 0

    private var firstCardViewModel: CardViewModel? = null
    private var secondCardViewModel: CardViewModel? = null

    private var jobMismatchDisplay: Job? = null

    val youWinViewModel = YouWinViewModel()

    init {
        Log.d(TAG, "init: initialized")
    }

    private fun createGrid(rows: Int, cols: Int, deck: List<CardContent>): Grid {
        if (deck.size != rows * cols) {
            throw IllegalArgumentException("Deck size '${deck.size}' cannot be split among $rows rows and $cols cols.")
        }

        val workingDeck = deck.toMutableList()
        val newGrid = mutableListOf<Row>()
        for (r in 1..rows) {
            val row = mutableListOf<CardViewModel>()
            for (c in 1..cols) {
                row.add(CardViewModel.fromContent(workingDeck.removeFirst()))
            }
            newGrid.add(row)
        }
        return newGrid
    }

    fun onGameStart() {
        deal()
    }

    fun onCardSelected(row: Int, col: Int) {
        Log.d(TAG, "onCardSelected: started on card ($row, $col)")
        logGameState()

        val card = grid[row][col]

        // If currently displaying mismatch on a delayed timer
        if (jobMismatchDisplay != null) {
            // Let user short-circuit display to pick their next cards
            jobMismatchDisplay?.cancel(CancellationException("User cancellation of mismatch display."))
            onMismatchDisplayCompleted()
            return
        }

        // Ignore if ineligible for selection
        if (card.isEnabled.value != true) {
            Log.d(TAG, "onCardSelected: ($row, $col) disabled.")
            return
        }

        Log.d(TAG, "onCardSelected: turning faceUp")
        card.flipAnimate()

        val first = firstCardViewModel
        if (first == null) {
            // Store first card and end action
            firstCardViewModel = card
        } else {
            // Store second card and process possible match
            secondCardViewModel = card
            if (first.content.value == card.content.value) {
                onMatchSuccessful()
            } else {
                onMatchUnsuccessful()
            }
        }
    }

    private fun logGameState() {
        Log.d(
            TAG,
            "gameState: matchedPairs=$matchedPairs "
                    + "first:${firstCardViewModel?.content?.value} "
                    + "second:${secondCardViewModel?.content?.value} "
        )
    }

    private fun onMatchSuccessful() {
        /*
         * Retain temporary references to these card models for use during the second card's animation.
         * The first and second card model properties will be reset immediately in case the user begins
         * selecting their next cards while this animation completes.
         */
        val first = firstCardViewModel
        val second = secondCardViewModel

        val matchObserver = object : Observer<CardViewModel?> {
            override fun onChanged(t: CardViewModel?) {
                if (t == null) {
                    matchedPairs++
                    if (matchedPairs >= totalPairs) {
                        onGameCompleted()
                    }
                    second?.isMatchAnimated?.removeObserver(this)
                }
            }
        }

        val flipObserver = object : Observer<CardViewModel?> {
            override fun onChanged(t: CardViewModel?) {
                if (t == null) {
                    first?.flagMatched()
                    second?.flagMatched()
                    second?.isFlipAnimated?.removeObserver(this)
                    second?.isMatchAnimated?.observeForever(matchObserver)
                }
            }
        }

        second?.isFlipAnimated?.observeForever(flipObserver)
        firstCardViewModel = null
        secondCardViewModel = null
    }

    private fun onMatchUnsuccessful() {
        Log.d(TAG, "onMatchUnsuccessful: started")
        jobMismatchDisplay = viewModelScope.launch {
            delay(3000)
            if (isActive) {
                onMismatchDisplayCompleted()
            } else {
                Log.d(TAG, "onMatchUnsuccessful: mismatch reset preempted")
            }
        }
    }

    private fun onMismatchDisplayCompleted() {
        Log.d(TAG, "onMismatchDisplayCompleted: resetting selected cards")
        firstCardViewModel?.flipAnimate()
        firstCardViewModel = null
        secondCardViewModel?.flipAnimate()
        secondCardViewModel = null
        jobMismatchDisplay = null
    }

    private fun onGameCompleted() {
        youWinViewModel.animate()
    }

    fun onGameRestart() {
        resetGrid(UpperLetters().randomPairs(totalPairs))
        matchedPairs = 0
        youWinViewModel.hide()
        deal()
    }

    private fun resetGrid(newCards: List<CardContent>) {
        val cards = newCards.toMutableList()
        for (row in grid) {
            for (col in row) {
                col.reset(cards.removeFirst())
            }
        }
    }

    private fun deal() {
        var delay = 0L
        for (row in grid) {
            for (col in row) {
                col.dealAnimate(delay)
                delay += 100L
            }
        }
    }

    companion object {
        private const val TAG = "GameFragmentViewModel"
    }

}
