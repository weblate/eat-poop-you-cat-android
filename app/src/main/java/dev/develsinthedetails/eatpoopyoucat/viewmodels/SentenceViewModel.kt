package dev.develsinthedetails.eatpoopyoucat.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.develsinthedetails.eatpoopyoucat.SharedPref
import dev.develsinthedetails.eatpoopyoucat.data.AppRepository
import dev.develsinthedetails.eatpoopyoucat.data.Entry
import dev.develsinthedetails.eatpoopyoucat.utilities.ID
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SentenceViewModel @Inject constructor(
    state: SavedStateHandle,
    private val repository: AppRepository,
) : ViewModel() {
    private val playerId = SharedPref.playerId()
    var isError: Boolean by mutableStateOf(false)
        private set
    var isLoading: Boolean by mutableStateOf(false)
        private set

    private val previousEntryId: String = checkNotNull(state.get<String>(ID))
    val previousEntry: LiveData<Entry> = repository.getEntry(previousEntryId).asLiveData()

    val entryId = UUID.randomUUID().toString()

    var sentence: String by mutableStateOf("")
        private set

    fun updateSentence(it: String) {
        sentence = it
    }

    fun sentenceIsNotBlank(): Boolean {
        isError = sentence.isBlank()
        return !isError
    }

    fun saveEntry(nextTo: (String) -> Unit) {
        isLoading = true
        val entry = previousEntry.value!!
        val isNewGame = entry.sequence == 0
        if (isNewGame) {
            val newEntry = entry.copy(
                localPlayerName = SharedPref.read(SharedPref.NICKNAME, null),
                sentence = sentence,
                drawing = null
            )
            viewModelScope.launch {
                repository.updateEntry(newEntry)
                nextTo.invoke(entry.id.toString())
                isLoading = false
            }
        } else {
            val newEntry = entry.copy(
                id = UUID.fromString(entryId),
                localPlayerName = SharedPref.read(SharedPref.NICKNAME, null),
                sentence = sentence,
                drawing = null,
                sequence = entry.sequence.inc(),
                playerId = playerId
            )
            viewModelScope.launch {
                repository.createEntry(newEntry)
                nextTo.invoke(entryId)
                isLoading = false
            }
        }
        SharedPref.write(SharedPref.NICKNAME, null)
    }

    fun deleteGame() {
        viewModelScope.launch {
            previousEntry.value?.let { repository.deleteGame(it.gameId) }
        }
    }
}
