package tech.svehla.gratitudejournal.presentation.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.svehla.gratitudejournal.domain.model.JournalEntry
import tech.svehla.gratitudejournal.common.Resource
import tech.svehla.gratitudejournal.common.Status
import tech.svehla.gratitudejournal.presentation.ui.ErrorScreen
import tech.svehla.gratitudejournal.presentation.ui.LoadingScreen
import tech.svehla.gratitudejournal.presentation.ui.components.NumberPicker
import java.time.LocalDate

@Composable
fun DetailScreen(
    date: String,
    viewModel: DetailViewModel,
    onBackPressed: () -> Unit
) {
    LaunchedEffect(key1 = date) {
        viewModel.loadDetail(date)
    }

    val entry: Resource<JournalEntry> by viewModel.journalEntry.collectAsState(initial = Resource.loading())

    when (entry.status) {
        Status.SUCCESS -> {
            DetailScreenContent(
                entry = entry.data,
                onSaveEntry = {
                    viewModel.saveEntry(it)
                    onBackPressed()
                },
            )
        }
        Status.LOADING -> {
            LoadingScreen()
        }
        else -> {
            ErrorScreen(
                entry.error,
                retry = {
                    viewModel.loadDetail(date)
                }
            )
        }
    }
}

@Composable
fun DetailScreenContent(
    entry: JournalEntry?,
    onSaveEntry: (JournalEntry) -> Unit,
) {
    var date by remember { mutableStateOf(entry?.date ?: LocalDate.now().toString()) }
    var firstNote by remember { mutableStateOf(entry?.firstNote ?: "") }
    var secondNote by remember { mutableStateOf(entry?.secondNote ?: "") }
    var thirdNote by remember { mutableStateOf(entry?.thirdNote ?: "") }
    var dayScore = remember { mutableStateOf(entry?.dayScore ?: 0) }
    var favoriteEntry by remember { mutableStateOf(entry?.favoriteEntry ?: 0) }
    var imageUrl by remember { mutableStateOf(entry?.imageUrl ?: "") }
    var gifUrl by remember { mutableStateOf(entry?.gifUrl ?: "") }

    BackHandler {
        val newEntry = JournalEntry(
            date,
            firstNote,
            secondNote,
            thirdNote,
            imageUrl,
            gifUrl,
            favoriteEntry,
            dayScore.value,
            LocalDate.now().toString()
        )
        onSaveEntry(newEntry)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = date,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            )
            OutlinedTextField(
                label = { Text("First Note") },
                value = firstNote,
                onValueChange = {
                    firstNote = it
                },
            )
            OutlinedTextField(
                label = { Text("Second Note") },
                value = secondNote,
                onValueChange = {
                    secondNote = it
                },
            )
            OutlinedTextField(
                label = { Text("Third Note") },
                value = thirdNote,
                onValueChange = {
                    thirdNote = it
                },
            )
            NumberPicker(
                state = dayScore,
                range = 0..10,
            )
        }
    }
}