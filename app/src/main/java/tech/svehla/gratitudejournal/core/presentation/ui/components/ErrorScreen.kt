package tech.svehla.gratitudejournal.core.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.svehla.gratitudejournal.core.domain.model.ErrorReason

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    error: ErrorReason?,
    retry: () -> Unit,
) {
    val errorMessage = when (error) {
        is ErrorReason.Network -> "Network error"
        is ErrorReason.NotFound -> "Not found"
        is ErrorReason.AccessDenied -> "Access denied"
        is ErrorReason.ServiceUnavailable -> "Service unavailable"
        is ErrorReason.DataParsing -> "Data parsing error"
        is ErrorReason.Unknown -> "Unknown error"
        null -> "Unknown error"
    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.h6,
        )
        Button(
            modifier = Modifier.padding(16.dp),
            onClick = retry,
        ) {
            Text(
                text = "Retry",
                style = MaterialTheme.typography.button,
            )
        }
    }
}