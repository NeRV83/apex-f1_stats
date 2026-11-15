package ru.nerv.apex.ui.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import ru.nerv.apex.data.local.entity.TeamEntity
import ru.nerv.apex.data.repository.RaceRepository
import javax.inject.Inject

@HiltViewModel
class TeamsViewModel @Inject constructor(
    private val repository: RaceRepository
) : ViewModel() {

    val uiState: StateFlow<TeamsUiState> = flow {
        emit(TeamsUiState.Loading)
        val result = runCatching { repository.refreshTeams() }

        repository.observeTeams().collect { teams ->
            if (teams.isNotEmpty()) {
                emit(TeamsUiState.Success(teams))
            } else {
                emit(TeamsUiState.Error(isNetworkError = result.isFailure))
            }
        }
    }
        .catch { e -> emit(TeamsUiState.Error(isNetworkError = true)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TeamsUiState.Loading)
}

sealed interface TeamsUiState {
    data object Loading : TeamsUiState
    data class Success(val teams: List<TeamEntity>) : TeamsUiState
    data class Error(val isNetworkError: Boolean) : TeamsUiState
}