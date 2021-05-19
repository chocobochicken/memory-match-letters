package org.jbuege.android.matchgame

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import androidx.databinding.BindingAdapter

/**
 * Animates flipping a card horizontally to see the "other side."
 */
@BindingAdapter("animateFlipCard", "cardViewModel")
fun animateFlipCard(cardView: View, animated: Boolean?, cardViewModel: CardViewModel) {
    if (animated != true) return

    val direction = if (cardViewModel.isFaceUp.value != true) 1 else -1

    val backOut = ObjectAnimator.ofFloat(cardView, View.ROTATION_Y, 360f * direction, 270f * direction)
    backOut.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            cardViewModel.flip()
        }
    })
    val frontIn = ObjectAnimator.ofFloat(cardView, View.ROTATION_Y, 90f * direction, 0f * direction)
    frontIn.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            cardViewModel.animationCompleted()
        }
    })
    with(AnimatorSet()) {
        playSequentially(backOut, frontIn)
        start()
    }
}

/**
 * Animations scale-out appearance of "You Win" banner.
 */
@BindingAdapter("animateYouWin", "youWinViewModel")
fun animateYouWin(view: View, animated: Boolean?, viewModel: YouWinViewModel) {
    if (animated != true) return

    val animX = ObjectAnimator.ofFloat(view, View.SCALE_X, 0f, 1f)
    val animY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0f, 1f)
    with(AnimatorSet()) {
        playTogether(animX, animY)
        addListener(object  : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
                viewModel.show()
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                viewModel.animationCompleted()
            }
        })
        start()
    }
}