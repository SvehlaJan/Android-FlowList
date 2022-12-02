package tech.svehla.gratitudejournal.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.svehla.gratitudejournal.domain.model.ErrorReason

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(
    error: ErrorReason?,
    retry: () -> Unit
) {
    val errorMessage = when (error) {
        is ErrorReason.Network -> "Network error"
        is ErrorReason.NotFound -> "Not found"
        is ErrorReason.AccessDenied -> "Access denied"
        is ErrorReason.ServiceUnavailable -> "Service unavailable"
        else -> "Unknown error"
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.h6
        )
        Button(
            onClick = retry,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Retry",
                style = MaterialTheme.typography.button
            )
        }
    }
}