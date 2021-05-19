package org.jbuege.android.matchgame

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Represents "You Win" banner displayed at completion of the game.
 */
class YouWinViewModel : ViewModel() {

    private val _visibility = MutableLiveData(View.GONE)
    val visibility: LiveData<Int> by this::_visibility

    private val _animated = MutableLiveData(false)
    val animated: LiveData<Boolean> by this::_animated

    fun hide() {
        _visibility.value = View.GONE
    }

    fun show() {
        _visibility.value = View.VISIBLE
    }

    fun animate() {
        _animated.value = true
    }

    fun animationCompleted() {
        _animated.value = false
    }
}