package ru.nerv.apex.ui.races

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import ru.nerv.apex.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaceDetailScreen(
    onBackClick: () -> Unit,
    viewModel: RaceDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.race_results)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val s = state) {
                is RaceDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is RaceDetailUiState.Error -> {
                    Text(
                        text = s.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is RaceDetailUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(s.results) { result ->
                            ResultRow(result)
                        }
                    }
                }

                is RaceDetailUiState.Empty -> {
                    Text(
                        text = stringResource(R.string.results_unavailable),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultRow(result: ru.nerv.apex.data.local.entity.ResultEntity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${result.position ?: "-"}",
            modifier = Modifier.width(32.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = result.driverName, style = MaterialTheme.typography.bodyLarge)
            Text(text = result.teamName, style = MaterialTheme.typography.bodySmall)
        }
        Text(
            text = result.time ?: result.status ?: "-",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}