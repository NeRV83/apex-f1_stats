package ru.nerv.apex.ui.races

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.nerv.apex.data.local.entity.RaceEntity
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RacesScreen(
    onRaceClick: (roundId: String) -> Unit,
    viewModel: RacesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val currentYear by viewModel.selectedYear.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Apex — F1 Stats") },
                actions = {
                    YearDropdown(
                        selectedYear = currentYear,
                        availableYears = viewModel.availableYears,
                        onYearSelected = viewModel::onYearSelected
                    )
                }
            )
        }
    ) { innerPadding ->
        // Получаем стейт для Pull-to-Refresh
        val isRefreshing = (state as? RacesUiState.Success)?.isRefreshing == true

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val s = state) {
                is RacesUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is RacesUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Ошибка: ${s.message}")
                    }
                }
                is RacesUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(s.races, key = { it.roundId }) { race ->
                            RaceCard(
                                race = race,
                                onClick = { onRaceClick(race.roundId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun YearDropdown(
    selectedYear: Int,
    availableYears: List<Int>,
    onYearSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(
                text = "$selectedYear",
                style = MaterialTheme.typography.titleMedium
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Выбрать год"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(120.dp)
        ) {
            availableYears.forEach { year ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "$year",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        onYearSelected(year)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun RaceCard(race: RaceEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = race.raceName ?: "Гран-При",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "${race.country ?: ""} · ${race.circuitName}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Этап ${race.roundNumber ?: "—"} · ${race.raceLocalTimestamp ?: "дата уточняется"}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}