package org.jbuege.android.matchgame

import assertk.assertions.hasSize
import org.junit.Test

class UpperLettersTest {

    @Test
    fun getPairs() {
        val randomPairs = UpperLetters().randomPairs(5)
        assertk.assertThat(randomPairs).hasSize(10)
    }
}