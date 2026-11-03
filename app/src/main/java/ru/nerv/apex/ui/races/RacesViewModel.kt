package ru.nerv.apex.ui.races

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import ru.nerv.apex.data.repository.RaceRepository
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RacesViewModel @Inject constructor(
    private val repository: RaceRepository
) : ViewModel() {

    private val _selectedYear = MutableStateFlow(2026)
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    val availableYears = (2026 downTo 2020).toList()

    val uiState: StateFlow<RacesUiState> = _selectedYear
        .flatMapLatest { year ->
            val networkState = flow {
                emit(RacesUiState.Loading)
                val result = runCatching { repository.refreshRaces(year) }
                if (result.isFailure) {
                    emit(RacesUiState.Error("Ошибка сети: ${result.exceptionOrNull()?.message}"))
                } else {
                    emit(RacesUiState.Loading)
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = RacesUiState.Loading
            )

            val dbFlow = repository.observeRaces(year)

            combine(networkState, dbFlow) { networkState, races ->
                when {
                    races.isNotEmpty() -> RacesUiState.Success(selectedYear = year, races = races)
                    networkState is RacesUiState.Error -> networkState
                    else -> RacesUiState.Loading
                }
            }
        }
        .catch { e ->
            emit(RacesUiState.Error(e.message ?: "Неизвестная ошибка"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RacesUiState.Loading
        )

    fun onYearSelected(year: Int) {
        _selectedYear.value = year
    }
}