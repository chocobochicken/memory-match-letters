package org.jbuege.android.matchgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameFragmentViewModelFactory(
    private val rows: Int,
    private val cols: Int,
    private val deck: List<CardContent>) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(GameFragmentViewModel::class.java)) {
            throw IllegalStateException("Unrecognized class '${modelClass.simpleName}'.")
        }
        return GameFragmentViewModel(rows, cols, deck) as T
    }
}