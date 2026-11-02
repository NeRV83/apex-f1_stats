package ru.nerv.apex

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import ru.nerv.apex.data.repository.RaceRepository
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: RaceRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                LaunchedEffect(Unit) {
                    runCatching { repository.refreshRaces(2026) }
                        .onFailure { Log.e("ApexCheck", "refresh failed", it) }
                        .onSuccess { Log.d("ApexCheck", "refresh ok") }

                    val races = repository.observeRaces(2026).first()
                    Log.d("ApexCheck", "races in db: ${races.size}, first=${races.firstOrNull()?.raceName}")
                }
                ApexApp()
            }
        }
    }
}

@Composable
private fun ApexApp() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Apex — F1 Stats",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}