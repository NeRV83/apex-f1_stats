package ru.nerv.apex.ui.races

import ru.nerv.apex.data.local.entity.RaceEntity

sealed interface RacesUiState {
    data object Loading : RacesUiState
    data class Success(
        val selectedYear: Int,
        val races: List<RaceEntity>,
        val isRefreshing: Boolean = false,
    ) : RacesUiState

    data class Error(val message: String) : RacesUiState
}