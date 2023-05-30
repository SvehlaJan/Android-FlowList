package tech.svehla.gratitudejournal.history.presentation

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tech.svehla.gratitudejournal.core.presentation.model.JournalEntryVO
import tech.svehla.gratitudejournal.core.presentation.ui.components.ErrorScreen
import tech.svehla.gratitudejournal.core.presentation.ui.components.LoadingScreen
import java.time.LocalDate

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun HistoryScreenRoute(
    viewModel: HistoryViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit,
) {
    val state: HistoryScreenState by viewModel.state.collectAsStateWithLifecycle()

    HistoryScreen(
        state = state,
        onNavigateToDetail = onNavigateToDetail,
        onRefresh = { viewModel.loadHistory() },
    )
}

@Composable
fun HistoryScreen(
    state: HistoryScreenState,
    onNavigateToDetail: (String) -> Unit,
    onRefresh: () -> Unit,
) {
    if (state.entries != null) {
        val items = state.entries
        if (items.isEmpty()) {
            HistoryEmptyScreen(
                onEntryClick = onNavigateToDetail
            )
        } else {
            HistoryListScreen(
                items = items,
                onEntryClick = onNavigateToDetail
            )
        }
    } else if (state.isLoading) {
        LoadingScreen()
    } else {
        ErrorScreen(
            error = state.error,
            retry = onRefresh,
        )
    }
}

@Composable
fun HistoryEmptyScreen(
    modifier: Modifier = Modifier,
    onEntryClick: (String) -> Unit = {},
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
            onEntryClick(todayDateStr)
        }) {
            Text(text = "Let's do it!")
        }
    }
}

@Composable
fun HistoryListScreen(
    modifier: Modifier = Modifier,
    items: List<JournalEntryVO>,
    onEntryClick: (String) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = rememberLazyListState(),
        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(items) { item ->
            HistoryEntry(
                modifier = Modifier.fillMaxWidth(),
                entry = item,
                onEntryClick = onEntryClick
            )
        }
    }
}

@Composable
fun HistoryEntry(
    modifier: Modifier = Modifier,
    entry: JournalEntryVO,
    onEntryClick: (String) -> Unit = {},
) {
    Card(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .clickable { onEntryClick(entry.date) },
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