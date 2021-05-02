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
    val faceUp: LiveData<Boolean> by this::_faceUp
    private val _faceUp: MutableLiveData<Boolean> = MutableLiveData(false)

    /**
     * Indicates if text content should be visible.
     */
    val contentVisible = Transformations.map(faceUp) { faceUp ->
        if (faceUp) View.VISIBLE else View.INVISIBLE
    }

    /**
     * Indicates if card has been successfully paired with its match.
     */
    val matched: LiveData<Boolean> by this::_matched
    private val _matched = MutableLiveData(false)

    /**
     * The resource to display in the card background.
     */
    val backgroundId: LiveData<Int> by this::_backgroundId
    private val _backgroundId = MutableLiveData<Int>(BACK_BACKGROUND)

    /**
     * Turns the card to the face-up position.
     */
    fun turnFaceUp() {
        _faceUp.value = true
        _backgroundId.value = R.drawable.ic_card_yellow_background
    }

    /**
     * Turns the card to the face-down position.
     */
    fun turnFaceDown() {
        _faceUp.value = false
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
        _faceUp.value = false
        _matched.value = false
        _backgroundId.value = BACK_BACKGROUND
    }

    /**
     * Re-initialize the card view for a new game, associating the new face-up content.
     */
    fun reset(cardContent: CardContent) {
        reset()
        _content.value = cardContent.textContent
    }

    override fun toString(): String {
        val faceUp = if (faceUp.value == true) "faceUp" else "faceDown"
        val contentVisible = if (contentVisible.value == View.VISIBLE) "contentVisible" else "contentInvisible"
        val matched = if (matched.value == true) "matched" else "unmatched"
        return "{content=${content.value}, $faceUp $contentVisible $matched bgId=${backgroundId.value}}"
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

