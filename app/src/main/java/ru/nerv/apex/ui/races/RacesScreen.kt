package ru.nerv.apex.ui.races

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.nerv.apex.R
import ru.nerv.apex.data.local.entity.RaceEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RacesScreen(
    onRaceClick: (roundId: String) -> Unit,
    contentPadding: PaddingValues,
    viewModel: RacesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val currentYear by viewModel.selectedYear.collectAsStateWithLifecycle()

    var expandedRaceId by rememberSaveable { mutableStateOf<String?>(null) }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    YearDropdown(
                        selectedYear = currentYear,
                        availableYears = viewModel.availableYears,
                        onYearSelected = viewModel::onYearSelected
                    )
                },
                windowInsets = WindowInsets(0)
            )

            val isRefreshing = (state as? RacesUiState.Success)?.isRefreshing == true

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = viewModel::refresh,
                modifier = Modifier.fillMaxSize()
            ) {
                when (val s = state) {
                    is RacesUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    is RacesUiState.Error -> {
                        val errorText = if (s.isNetworkError) stringResource(R.string.error_network) else stringResource(
                            R.string.unknown_error
                        )
                        Text(text = errorText)
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
                                    isExpanded = expandedRaceId == race.roundId,
                                    onExpandClick = {
                                        expandedRaceId =
                                            if (expandedRaceId == race.roundId) null else race.roundId
                                    },
                                    onResultsClick = { onRaceClick(race.roundId) },
                                    onLoadLaps = { viewModel.loadLapsForRace(race) }
                                )
                            }
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
                contentDescription = null
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
private fun RaceCard(
    race: RaceEntity,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    onResultsClick: () -> Unit,
    onLoadLaps: () -> Unit
) {
    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            onLoadLaps()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpandClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(if (isExpanded) 240.dp else 100.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.round, (race.roundNumber ?: 0) + 1),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = race.raceName?.uppercase() ?: stringResource(R.string.grand_prix_default),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${race.countryFlag ?: ""} ${race.country ?: ""} · ${race.circuitName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF949498)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatRaceDate(race.raceLocalTimestamp),
                    style = MaterialTheme.typography.bodySmall
                )

                AnimatedVisibility(visible = isExpanded) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(16.dp))

                        val trackRes = getTrackImage(race.circuitName)
                        if (trackRes != null) {
                            Image(
                                painter = painterResource(id = trackRes),
                                contentDescription = stringResource(R.string.track_layout),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .padding(horizontal = 32.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            InfoItem(
                                label = stringResource(R.string.laps),
                                value = race.scheduledLaps?.toString() ?: stringResource(R.string.is_loading)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onResultsClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                stringResource(R.string.view_results).uppercase(),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.expand),
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF949498))
    }
}