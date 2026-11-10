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
import kotlinx.coroutines.launch
import ru.nerv.apex.data.repository.RaceRepository
import ru.nerv.apex.data.settings.SettingsRepository
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RacesViewModel @Inject constructor(
    private val repository: RaceRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _selectedYear = MutableStateFlow(2026)
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val availableYears = (2026 downTo 2020).toList()

    init {
        viewModelScope.launch {
            settingsRepository.defaultYearFlow.collect { defaultYear ->
                if (_selectedYear.value != defaultYear && _selectedYear.value == 2026) {
                    _selectedYear.value = defaultYear
                }
            }
        }
    }

    val uiState: StateFlow<RacesUiState> = _selectedYear
        .flatMapLatest { year ->
            val networkState = flow {
                emit(RacesUiState.Loading)
                val result = runCatching { repository.refreshRaces(year) }
                if (result.isFailure) {
                    emit(RacesUiState.Error(result.exceptionOrNull()?.message ?: "Network error"))
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = RacesUiState.Loading
            )

            combine(
                networkState,
                repository.observeRaces(year),
                _isRefreshing
            ) { netState, races, refreshing ->
                when {
                    races.isNotEmpty() -> RacesUiState.Success(
                        selectedYear = year,
                        races = races,
                        isRefreshing = refreshing
                    )

                    netState is RacesUiState.Error -> netState
                    else -> RacesUiState.Loading
                }
            }
        }
        .catch { e -> emit(RacesUiState.Error(e.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RacesUiState.Loading
        )

    fun onYearSelected(year: Int) {
        _selectedYear.value = year
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            runCatching { repository.refreshRaces(_selectedYear.value) }
            _isRefreshing.value = false
        }
    }
}