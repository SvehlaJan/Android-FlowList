package tech.svehla.gratitudejournal.presentation.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.svehla.gratitudejournal.common.Resource
import tech.svehla.gratitudejournal.domain.model.JournalEntry
import tech.svehla.gratitudejournal.presentation.ui.ErrorScreen
import tech.svehla.gratitudejournal.presentation.ui.LoadingScreen
import java.time.LocalDate

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    selectEntry: (String) -> Unit
) {

    val state: Resource<List<JournalEntry>> by viewModel.state
    val listState = rememberLazyListState()

    when (state) {
        is Resource.Success -> {
            val items = state.data!!
            if (items.isEmpty()) {
                HistoryEmptyScreen(
                    selectEntry = selectEntry
                )
            } else {
                HistoryListScreen(
                    listState = listState,
                    items = items,
                    selectEntry = selectEntry
                )
            }
        }
        is Resource.Loading -> {
            LoadingScreen()
        }
        else -> {
            ErrorScreen(message = state.message, retry = { viewModel.refresh() })
        }
    }
}

@Composable
fun HistoryEmptyScreen(selectEntry: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "No entries yet",
        )
        Button(onClick = {
            val todayDateStr = LocalDate.now().toString()
            selectEntry(todayDateStr)
        }) {
            Text(text = "Add entry")
        }
    }
}

@Composable
fun HistoryListScreen(
    listState: LazyListState,
    items: List<JournalEntry>,
    selectEntry: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
    ) {
        items(items) { item ->
            HistoryEntry(
                entry = item,
                selectEntry = selectEntry
            )
        }
    }
}

@Composable
fun HistoryEntry(entry: JournalEntry, selectEntry: (String) -> Unit) {
    Column(modifier = Modifier
        .padding(16.dp)
        .clickable { selectEntry(entry.date) }) {
        Text(text = entry.date)
    }
}