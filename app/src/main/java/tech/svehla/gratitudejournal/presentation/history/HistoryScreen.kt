package tech.svehla.gratitudejournal.presentation.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.svehla.gratitudejournal.domain.model.JournalEntry
import tech.svehla.gratitudejournal.presentation.ui.ErrorScreen
import tech.svehla.gratitudejournal.presentation.ui.LoadingScreen
import java.time.LocalDate

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    selectEntry: (String) -> Unit
) {

    val state: HistoryScreenState by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    if (state.entries != null) {
        val items = state.entries!!
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
    } else if (state.isLoading) {
        LoadingScreen()
    } else {
        ErrorScreen(error = state.error, retry = { viewModel.loadHistory() })
    }
}

@Composable
fun HistoryEmptyScreen(selectEntry: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No entries yet. Write your first journal.",
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val todayDateStr = LocalDate.now().toString()
            selectEntry(todayDateStr)
        }) {
            Text(text = "Let's do it!")
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { selectEntry(entry.date) },
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = entry.firstNote,
                style = MaterialTheme.typography.body1
            )
            Text(
                modifier = Modifier.padding(top = 16.dp).align(Alignment.End),
                text = entry.formattedDate,
                style = MaterialTheme.typography.body2
            )
        }
    }

}