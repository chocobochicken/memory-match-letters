package org.jbuege.android.matchgame

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

/**
 * Models collection of views representing a single card.
 */
class CardViewModel(
    content: String
) {

    /**
     * Content to display in text overlay.
     */
    // TODO - Rename to textContent
    val content: LiveData<String> by this::_content
    private val _content = MutableLiveData(content)

    /**
     * Indicates if card is turned face-up.
     */
    val isFaceUp: LiveData<Boolean> by this::_isFaceUp
    private val _isFaceUp: MutableLiveData<Boolean> = MutableLiveData(false)

    /**
     * Indicates if text content should be visible.
     */
    val isContentVisible = Transformations.map(isFaceUp) { faceUp ->
        if (faceUp) View.VISIBLE else View.INVISIBLE
    }

    /**
     * Indicates if card has been successfully paired with its match.
     */
    val isMatched: LiveData<Boolean> by this::_matched
    private val _matched = MutableLiveData(false)

    /**
     * Indicates if card is currently interactive.
     */
    val isEnabled: LiveData<Boolean> by this::_isEnabled
    private val _isEnabled = MutableLiveData(true)

    val isAnimated: LiveData<Boolean> by this::_isAnimated
    private val _isAnimated = MutableLiveData(false)

    /**
     * The resource to display in the card background.
     */
    val backgroundId: LiveData<Int> by this::_backgroundId
    private val _backgroundId = MutableLiveData<Int>(BACK_BACKGROUND)

    fun flipAnimate() {
        _isEnabled.value = false
        _isAnimated.value = true
    }

    fun animationCompleted() {
        _isAnimated.value = false
        if (_isFaceUp.value == false) _isEnabled.value = true
    }

    /**
     * Flip the card from face-up to face-down, or vice versa.
     */
    fun flip() {
        when (_isFaceUp.value) {
            true -> turnFaceDown()
            else -> turnFaceUp()
        }
    }

    /**
     * Turns the card to the face-up position.
     */
    private fun turnFaceUp() {
        _isFaceUp.value = true
        _backgroundId.value = R.drawable.ic_card_yellow_background
    }

    /**
     * Turns the card to the face-down position.
     */
    private fun turnFaceDown() {
        _isFaceUp.value = false
        _backgroundId.value = BACK_BACKGROUND
    }

    /**
     * Confirms the card has been successfully paired with its match.
     */
    fun flagMatched() {
        _matched.value = true
        _backgroundId.value = R.drawable.ic_card_pink_background
    }

    private fun reset() {
        _isFaceUp.value = false
        _matched.value = false
        _backgroundId.value = BACK_BACKGROUND
        _isEnabled.value = true
        _isAnimated.value = false
    }

    /**
     * Re-initialize the card view for a new game, associating the new face-up content.
     */
    fun reset(cardContent: CardContent) {
        reset()
        _content.value = cardContent.textContent
    }

    override fun toString(): String {
        val faceUp = if (isFaceUp.value == true) "faceUp" else "faceDown"
        val contentVisible = if (isContentVisible.value == View.VISIBLE) "contentVisible" else "contentInvisible"
        val matched = if (isMatched.value == true) "matched" else "unmatched"
        val enabled = if (isEnabled.value == true) "enabled" else "disabled"
        val animated = if (isAnimated.value == true) "animated" else ""
        return "{content=${content.value}, $faceUp $contentVisible $matched $enabled $animated bgId=${backgroundId.value}}"
    }

    companion object {
        private const val BACK_BACKGROUND = R.drawable.match_card_back

        /**
         * Construct a new card view matching the provided face-up content.
         */
        fun fromContent(cardContent: CardContent): CardViewModel {
            return CardViewModel(cardContent.textContent)
        }
    }

}

