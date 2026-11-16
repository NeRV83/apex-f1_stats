package ru.nerv.apex.ui.drivers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import ru.nerv.apex.data.local.entity.DriverEntity
import ru.nerv.apex.data.repository.RaceRepository
import ru.nerv.apex.data.settings.SettingsRepository
import javax.inject.Inject

@HiltViewModel
class DriversViewModel @Inject constructor(
    private val repository: RaceRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<DriversUiState> = flow {
        emit(DriversUiState.Loading)

        val year = settingsRepository.defaultYearFlow.first()
        val result = runCatching { repository.refreshDrivers(year) }

        repository.observeDrivers().collect { drivers ->
            if (drivers.isNotEmpty()) {
                emit(DriversUiState.Success(drivers))
            } else {
                emit(DriversUiState.Error(isNetworkError = result.isFailure))
            }
        }
    }
        .catch { emit(DriversUiState.Error(isNetworkError = true)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DriversUiState.Loading)
}

sealed interface DriversUiState {
    data object Loading : DriversUiState
    data class Success(val drivers: List<DriverEntity>) : DriversUiState
    data class Error(val isNetworkError: Boolean) : DriversUiState
}