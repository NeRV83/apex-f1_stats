package ru.nerv.apex.ui.races

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import ru.nerv.apex.data.local.entity.ResultEntity
import ru.nerv.apex.data.repository.RaceRepository
import javax.inject.Inject

@HiltViewModel
class RaceDetailViewModel @Inject constructor(
    private val repository: RaceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val roundId: String = savedStateHandle.get<String>("roundId") ?: ""

    val uiState: StateFlow<RaceDetailUiState> = flow {
        emit(RaceDetailUiState.Loading)

        val networkResult = runCatching { repository.refreshResults(roundId) }

        repository.observeResults(roundId).collect { results ->
            if (results.isNotEmpty()) {
                emit(RaceDetailUiState.Success(results))
            } else if (networkResult.isFailure) {
                emit(
                    RaceDetailUiState.Error(
                        networkResult.exceptionOrNull()?.message ?: "Network error"
                    )
                )
            } else {
                emit(RaceDetailUiState.Empty)
            }
        }
    }
        .catch { e -> emit(RaceDetailUiState.Error(e.message ?: "Loading error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RaceDetailUiState.Loading
        )
}

sealed interface RaceDetailUiState {
    data object Loading : RaceDetailUiState
    data class Success(val results: List<ResultEntity>) : RaceDetailUiState
    data class Error(val message: String) : RaceDetailUiState
    data object Empty : RaceDetailUiState
}