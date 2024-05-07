package pro.eliott.trelloviewer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pro.eliott.trelloviewer.presentation.entities.Board
import pro.eliott.trelloviewer.presentation.entities.TaskCard
import pro.eliott.trelloviewer.presentation.trello.TrelloApi

data class MainUiState(
    val boards: List<Board>? = null,
    val cachedBoards: Map<String, List<TaskCard>> = mapOf()
)

class MainViewModel(private val repository: TaskManagementRepository) : ViewModel() {
    // Expose screen UI state
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun fetchBoards(){
        Thread {
            val newBoards = repository.boards()
            _uiState.update { currentState ->
                currentState.copy(
                    boards = newBoards
                )
            }
        }.start()
    }

    fun fetchBoard(id: String) {
        if (_uiState.value.cachedBoards.containsKey(id)) {
            return
        }
        Thread {
            val cards = repository.boardCards(id)
            _uiState.update { currentState ->
                currentState.copy(
                    cachedBoards = currentState.cachedBoards + (id to cards)
                )
            }
        }.start()
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                return MainViewModel(
                    TrelloApi()
                ) as T
            }
        }
    }
}