package tech.svehla.gratitudejournal.presentation.detail

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import tech.svehla.gratitudejournal.presentation.model.JournalEntryVO
import tech.svehla.gratitudejournal.presentation.ui.ErrorScreen
import tech.svehla.gratitudejournal.presentation.ui.LoadingScreen
import tech.svehla.gratitudejournal.presentation.ui.components.GiphyPicker

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun DetailScreen(
    date: String,
    viewModel: DetailViewModel,
    onBackPressed: () -> Unit
) {
    val state: DetailScreenStateNew by viewModel.state.collectAsStateWithLifecycle()

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

    when {
        state.isLoading -> {
            LoadingScreen()
        }
        state.errorReason != null -> {
            ErrorScreen(
                error = state.errorReason,
                retry = {
                    viewModel.loadDetail(date)
                }
            )
        }
        state.showGifPicker -> {
            GiphyPicker(
                modifier = Modifier.fillMaxSize(),
                onMediaSelected = { media ->
                    media?.images?.original?.gifUrl?.let { url ->
                        viewModel.onUiAction(UIAction.GifSelected(url))
                    }
                }
            )
        }
        state.content != null -> {
            DetailScreenContent(
                journalEntry = state.content!!,
                onUiAction = {
                    viewModel.onUiAction(it)
                }
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
        autoCorrect = false,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next
    )
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = journalEntry.formattedDate(),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
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