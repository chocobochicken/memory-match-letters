package org.jbuege.android.matchgame

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.jbuege.android.matchgame.databinding.GameFragmentReusableLayoutBinding

class GameFragment : Fragment() {

    companion object {
        private const val TAG = "GameFragment"
        private const val ROWS = 3
        private const val COLS = 4
        private const val PAIRS = 6
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: started")
        val binding = DataBindingUtil.inflate<GameFragmentReusableLayoutBinding>(
            inflater, R.layout.game_fragment_reusable_layout, container, false)

        val viewModelFactory = GameFragmentViewModelFactory(ROWS, COLS, UpperLetters().randomPairs(PAIRS))
        val viewModel = ViewModelProvider(this, viewModelFactory)
            .get(GameFragmentViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

}
