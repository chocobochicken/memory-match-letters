package org.jbuege.android.matchgame

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private var eventGameWon = false

    private val _youWinVisibility = MutableLiveData(View.GONE)
    val youWinVisibility: LiveData<Int> by this::_youWinVisibility

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

        // Ignore if already selected or matched
        if (card.isFaceUp.value == true) {
            Log.d(TAG, "onCardSelected: ($row, $col) already faceup.")
            return
        }

        Log.d(TAG, "onCardSelected: turning faceUp")
        card.turnFaceUp()

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
                    + "gameCompleted=$eventGameWon"
        )
    }

    private fun onMatchSuccessful() {
        firstCardViewModel?.flagMatched()
        firstCardViewModel = null
        secondCardViewModel?.flagMatched()
        secondCardViewModel = null

        matchedPairs++
        if (matchedPairs >= totalPairs) {
            onGameCompleted()
        }
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
        firstCardViewModel?.turnFaceDown()
        firstCardViewModel = null
        secondCardViewModel?.turnFaceDown()
        secondCardViewModel = null
        jobMismatchDisplay = null
    }

    private fun onGameCompleted() {
        eventGameWon = true
        _youWinVisibility.value = View.VISIBLE
    }

    fun onGameRestart() {
        val randomPairs = UpperLetters().randomPairs(totalPairs)
        resetGrid(randomPairs)

        matchedPairs = 0
        _youWinVisibility.value = View.INVISIBLE
        eventGameWon = false
    }

    private fun resetGrid(newCards: List<CardContent>) {
        val cards = newCards.toMutableList()
        for (row in grid) {
            for (col in row) {
                col.reset(cards.removeFirst())
            }
        }
    }

    companion object {
        private const val TAG = "GameFragmentViewModel"
    }

}
