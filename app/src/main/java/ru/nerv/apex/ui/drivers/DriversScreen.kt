package ru.nerv.apex.ui.drivers

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
import ru.nerv.apex.data.local.entity.DriverEntity
import androidx.core.graphics.toColorInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriversScreen(
    contentPadding: PaddingValues,
    viewModel: DriversViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(stringResource(R.string.drivers)) },
                windowInsets = WindowInsets(0)
            )

            when (val s = state) {
                is DriversUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is DriversUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (s.isNetworkError) stringResource(R.string.error_network)
                            else stringResource(
                                R.string.unknown_error
                            )
                        )
                    }
                }

                is DriversUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(s.drivers, key = { it.id }) { driver ->
                            DriverCard(driver)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DriverCard(driver: DriverEntity) {
    val teamColor = try {
        if (driver.teamColor != null) Color(driver.teamColor.toColorInt()) else Color.Gray
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
                    .height(80.dp)
                    .background(teamColor)
            )

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${driver.position ?: stringResource(R.string.is_loading)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.width(40.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = driver.name.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = driver.teamName ?: stringResource(R.string.unknown_team),
                        style = MaterialTheme.typography.bodySmall,
                        color = teamColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${driver.countryFlag ?: ""} ${driver.nationality ?: ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF949498)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${driver.points?.toInt() ?: 0}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = stringResource(R.string.pts_uppercase),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF949498)
                    )
                }
            }
        }
    }
}