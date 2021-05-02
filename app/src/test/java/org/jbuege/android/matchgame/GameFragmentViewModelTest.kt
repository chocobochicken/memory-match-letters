package org.jbuege.android.matchgame

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.messageContains
import org.junit.Assert
import org.junit.Test

class GameFragmentViewModelTest {

    @Test
    fun createGrid() {
        val viewModel = GameFragmentViewModel(3, 4, UpperLetters().randomPairs(6))

        with (viewModel.grid) {
            assertThat(size).isEqualTo(3)
            for (row in this) {
                assertThat(row.size).isEqualTo(4)
            }
        }
    }

    @Test
    fun createGrid_invalidPairs() {
        val exception = Assert.assertThrows(IllegalArgumentException::class.java) {
            GameFragmentViewModel(3, 4, UpperLetters().randomPairs(9))
        }
        assertThat(exception).messageContains(18.toString())
        assertThat(exception).messageContains(3.toString())
        assertThat(exception).messageContains(4.toString())
    }

}