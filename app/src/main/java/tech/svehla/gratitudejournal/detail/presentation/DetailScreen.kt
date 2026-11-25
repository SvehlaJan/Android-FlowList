package tech.svehla.gratitudejournal.detail.presentation

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import tech.svehla.gratitudejournal.core.presentation.model.JournalEntryVO
import tech.svehla.gratitudejournal.core.presentation.ui.components.ErrorScreen
import tech.svehla.gratitudejournal.core.presentation.ui.components.GiphyPicker
import tech.svehla.gratitudejournal.core.presentation.ui.components.LoadingScreen
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date

@Composable
fun DetailScreenRoute(
    viewModel: DetailViewModel = hiltViewModel(),
    date: String,
    onBackPressed: () -> Unit,
) {
    val state: DetailScreenState by viewModel.state.collectAsStateWithLifecycle()

    state.events.firstOrNull()?.let { event ->
        LaunchedEffect(event) {
            when (event) {
                is UIEvent.NavigateBack -> {
                    onBackPressed()
                }
            }
            // Once the event is consumed, notify the ViewModel.
            viewModel.onEventConsumed(event.id)
        }
    }

    DisposableEffect(key1 = viewModel) {
        onDispose {
            viewModel.onStop()
        }
    }

    DetailScreen(
        state = state,
        onUiAction = { viewModel.onUiAction(it) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    state: DetailScreenState,
    onUiAction: (UIAction) -> Unit,
) {
    when {
        state.isLoading -> {
            LoadingScreen()
        }

        state.error != null -> {
            ErrorScreen(
                error = state.error,
                retry = {
                    onUiAction(UIAction.RefreshData)
                },
            )
        }

        state.showGifPicker -> {
            GiphyPicker(
                modifier = Modifier.fillMaxSize(),
                onMediaSelected = { media ->
                    media?.images?.original?.gifUrl?.let { url ->
                        onUiAction(UIAction.GifSelected(url))
                    }
                },
                onPickerDismissed = {
                    onUiAction(UIAction.GifPickerDismissed)
                },
            )
        }

        state.showDatePicker -> {
            val selectedDateMillis = state.content?.date?.let { dateStr ->
                LocalDate.parse(dateStr).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            }
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDateMillis,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis <= System.currentTimeMillis()
                    }
                },
            )
            DatePickerDialog(
                onDismissRequest = { onUiAction(UIAction.DateSelected(null)) },
                confirmButton = {
                    Button(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onUiAction(UIAction.DateSelected(date))
                        }
                    }) {
                        Text("Confirm")
                    }
                },
            ) {
                DatePicker(state = datePickerState)
            }
        }

        state.content != null -> {
            DetailScreenContent(
                journalEntry = state.content,
                onUiAction = {
                    onUiAction(it)
                },
            )
        }
    }
}

@Composable
fun DetailScreenContent(
    journalEntry: JournalEntryVO,
    onUiAction: (UIAction) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val defaultKeyboardOptions = KeyboardOptions.Default.copy(
        capitalization = KeyboardCapitalization.Sentences,
        autoCorrectEnabled = false,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next
    )
    val context = LocalContext.current
    val imageLoader = remember(context) {
        ImageLoader.Builder(context).components {
            if (SDK_INT >= 28) {
                add(AnimatedImageDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }.build()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        Text(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
                .clickable { onUiAction(UIAction.DateClicked) },
            text = journalEntry.formattedDate(),
            style = MaterialTheme.typography.headlineMedium,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("First Note") },
            value = journalEntry.firstNote,
            keyboardOptions = defaultKeyboardOptions,
            onValueChange = {
                onUiAction(UIAction.FirstNoteUpdated(it))
            },
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Second Note") },
            value = journalEntry.secondNote,
            keyboardOptions = defaultKeyboardOptions,
            onValueChange = {
                onUiAction(UIAction.SecondNoteUpdated(it))
            },
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Third Note") },
            value = journalEntry.thirdNote,
            onValueChange = {
                onUiAction(UIAction.ThirdNoteUpdated(it))
            },
            keyboardOptions = defaultKeyboardOptions,
            keyboardActions = KeyboardActions(onNext = {
                focusManager.clearFocus()
            }),
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (journalEntry.gifUrl != null) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onUiAction(UIAction.GifPickerRequested) },
                painter = rememberAsyncImagePainter(
                    model = journalEntry.gifUrl,
                    imageLoader = imageLoader,
                    contentScale = ContentScale.FillBounds,
                ),
                contentDescription = "Gif"
            )
        } else {
            Button(
                onClick = { onUiAction(UIAction.GifPickerRequested) },
            ) {
                Text("Add a GIF to this day")
            }
        }
    }
}

@Composable
@Preview
fun DetailScreenContentPreview() {
    DetailScreenContent(
        journalEntry = JournalEntryVO(
            date = "2021-01-01",
            firstNote = "First Note",
            secondNote = "Second Note",
            thirdNote = "Third Note",
            gifUrl = "https://media.giphy.com/media/3o7aD2X9Y5WVqU9s52/giphy.gif",
            imageUrl = null,
        )
    )
}
