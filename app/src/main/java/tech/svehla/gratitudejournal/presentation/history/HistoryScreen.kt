package tech.svehla.gratitudejournal.presentation.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tech.svehla.gratitudejournal.presentation.model.JournalEntryVO
import tech.svehla.gratitudejournal.presentation.ui.ErrorScreen
import tech.svehla.gratitudejournal.presentation.ui.LoadingScreen
import tech.svehla.gratitudejournal.presentation.ui.util.JournalEntryVODataProvider
import tech.svehla.gratitudejournal.presentation.ui.util.JournalEntryVOListDataProvider
import java.time.LocalDate

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun HistoryScreen(
    selectEntry: (String) -> Unit,
) {
    val viewModel: HistoryViewModel = hiltViewModel()
    val state: HistoryScreenState by viewModel.state.collectAsStateWithLifecycle()

    if (state.entries != null) {
        val items = state.entries!!
        if (items.isEmpty()) {
            HistoryEmptyScreen(
                selectEntry = selectEntry
            )
        } else {
            HistoryListScreen(
                items = items,
                selectEntry = selectEntry
            )
        }
    } else if (state.isLoading) {
        LoadingScreen()
    } else {
        ErrorScreen(error = state.errorReason, retry = { viewModel.loadHistory() })
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryEmptyScreen(
    modifier: Modifier = Modifier,
    selectEntry: (String) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize(),
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

@Preview(showBackground = true)
@Composable
fun HistoryListScreen(
    modifier: Modifier = Modifier,
    @PreviewParameter(JournalEntryVOListDataProvider::class) items: List<JournalEntryVO>,
    selectEntry: (String) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = rememberLazyListState(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
    ) {
        items(items) { item ->
            HistoryEntry(
                modifier = Modifier.fillMaxWidth(),
                entry = item,
                selectEntry = selectEntry
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryEntry(
    modifier: Modifier = Modifier,
    @PreviewParameter(JournalEntryVODataProvider::class) entry: JournalEntryVO,
    selectEntry: (String) -> Unit = {},
) {
    Card(
        modifier = modifier
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
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.End),
                text = entry.formattedDate(),
                style = MaterialTheme.typography.body2
            )
        }
    }

}