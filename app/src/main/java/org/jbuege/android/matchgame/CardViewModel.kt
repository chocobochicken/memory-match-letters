package org.jbuege.android.matchgame

import android.view.View
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Models collection of views representing a single card.
 */
class CardViewModel(
    content: String
) : ViewModel() {

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

    val isVisible: LiveData<Int> by this::_isVisible
    private val _isVisible = MutableLiveData(View.VISIBLE)

    val isDealAnimated: LiveData<CardViewModel?> by this::_isDealAnimated
    private val _isDealAnimated = MutableLiveData<CardViewModel?>(null)

    val isFlipAnimated: LiveData<CardViewModel?> by this::_isFlipAnimated
    private val _isFlipAnimated = MutableLiveData<CardViewModel?>(null)

    /**
     * The resource to display in the card background.
     */
    val backgroundId: LiveData<Int> by this::_backgroundId
    private val _backgroundId = MutableLiveData<Int>(BACK_BACKGROUND)

    fun dealAnimate(delay: Long) {
        _isEnabled.value = false
        _isVisible.value = View.INVISIBLE

        viewModelScope.launch {
            delay(delay)
            _isDealAnimated.value = this@CardViewModel
        }
    }

    fun dealAnimationCompleted() {
        _isDealAnimated.value = null
        _isEnabled.value = true
    }

    fun flipAnimate() {
        _isEnabled.value = false
        _isFlipAnimated.value = this
    }

    fun flipAnimationCompleted() {
        _isFlipAnimated.value = null
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
        _isFlipAnimated.value = null
        _isDealAnimated.value = null
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
        val visible = if (isVisible.value == View.VISIBLE) "visible" else "invisible"
        val contentVisible = if (isContentVisible.value == View.VISIBLE) "contentVisible" else "contentInvisible"
        val matched = if (isMatched.value == true) "matched" else "unmatched"
        val enabled = if (isEnabled.value == true) "enabled" else "disabled"
        val dealAnimated = if (isDealAnimated.value != null) "dealAnimated" else ""
        val flipAnimated = if (isFlipAnimated.value != null) "flipAnimated" else ""
        return "{content=${content.value}, $faceUp $visible $contentVisible $matched $enabled $dealAnimated " +
                "$flipAnimated bgId=${backgroundId.value}}"
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

