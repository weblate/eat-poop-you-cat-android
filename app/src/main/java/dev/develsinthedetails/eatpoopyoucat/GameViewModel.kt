package dev.develsinthedetails.eatpoopyoucat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.develsinthedetails.eatpoopyoucat.data.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.develsinthedetails.eatpoopyoucat.data.Game
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameRepository: GameRepository
): ViewModel() {

    fun addGame() {
        Log.e("yo","yoyo")
        viewModelScope.launch {
            var id = UUID.randomUUID()
            val game = Game(id, null, null)
            var result = gameRepository.createGame(game)
            var games = gameRepository.allGames
            Log.wtf("oof", "addGame: {games}")
        }
    }
}