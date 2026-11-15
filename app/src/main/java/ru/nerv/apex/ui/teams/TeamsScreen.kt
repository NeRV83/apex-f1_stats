package ru.nerv.apex.ui.teams

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.nerv.apex.R
import ru.nerv.apex.data.local.entity.TeamEntity
import androidx.core.graphics.toColorInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(
    contentPadding: PaddingValues,
    viewModel: TeamsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(stringResource(R.string.teams)) },
                windowInsets = WindowInsets(0)
            )

            when (val s = state) {
                is TeamsUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is TeamsUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val errorText = if (s.isNetworkError) {
                            stringResource(R.string.error_no_team_data)
                        } else {
                            stringResource(R.string.unknown_error)
                        }
                        Text(text = errorText)
                    }
                }

                is TeamsUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(s.teams, key = { it.id }) { team ->
                            TeamCard(team)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamCard(team: TeamEntity) {
    val teamColor = try {
        if (team.primaryColor != null) Color(team.primaryColor.toColorInt()) else Color.Gray
    } catch (e: Exception) {
        Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    .height(72.dp)
                    .background(teamColor)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = team.name.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${team.countryFlag ?: ""} ${team.nationality ?: ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF949498)
                )
            }
        }
    }
}