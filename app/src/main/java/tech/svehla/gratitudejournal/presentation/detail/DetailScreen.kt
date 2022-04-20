package tech.svehla.gratitudejournal.presentation.detail

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import tech.svehla.gratitudejournal.domain.model.JournalEntry
import tech.svehla.gratitudejournal.presentation.ui.ErrorScreen
import tech.svehla.gratitudejournal.presentation.ui.LoadingScreen
import tech.svehla.gratitudejournal.presentation.ui.components.GiphyPicker
import tech.svehla.gratitudejournal.presentation.ui.util.observeAsSate

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DetailScreen(
    date: String,
    viewModel: DetailViewModel,
    onBackPressed: () -> Unit
) {
    val state: DetailScreenState by viewModel.state.collectAsState()
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

    val lifeCycleState = LocalLifecycleOwner.current.lifecycle.observeAsSate()
    val uiState = lifeCycleState.value

    when (state.uiState) {
        UIState.Content -> {
            DetailScreenContent(
                viewModel = viewModel,
                onSelectGifClicked = {
                    viewModel.onSelectGifClicked()
                }
            )
        }
        is UIState.GifPicker -> {
            GiphyPicker(
                modifier = Modifier.fillMaxSize(),
                onMediaSelected = { media ->
                    viewModel.onGifSelected(media)
                }
            )
        }
        is UIState.Error -> {
            ErrorScreen(
                error = (state.uiState as UIState.Error).error,
                retry = {
                    viewModel.loadDetail(date)
                }
            )
        }
        is UIState.Loading -> {
            LoadingScreen()
        }
    }
}

@Composable
fun DetailScreenContent(
    viewModel: DetailViewModel,
    onSelectGifClicked: () -> Unit,
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
            text = JournalEntry.formatDate(viewModel.date.value),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("First Note") },
            value = viewModel.firstNote.value,
            keyboardOptions = defaultKeyboardOptions,
            onValueChange = {
                viewModel.firstNote.value = it
            },
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Second Note") },
            value = viewModel.secondNote.value,
            keyboardOptions = defaultKeyboardOptions,
            onValueChange = {
                viewModel.secondNote.value = it
            },
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Third Note") },
            value = viewModel.thirdNote.value,
            onValueChange = {
                viewModel.thirdNote.value = it
            },
            keyboardOptions = defaultKeyboardOptions,
            keyboardActions = KeyboardActions(onNext = {
                focusManager.clearFocus()
            }),
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (viewModel.gifUrl.value != null) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onSelectGifClicked() },
                painter = rememberAsyncImagePainter(
                    model = viewModel.gifUrl.value!!,
                    imageLoader = imageLoader,
                    contentScale = ContentScale.FillBounds,
                ),
                contentDescription = "Gif"
            )
        } else {
            Button(onClick = onSelectGifClicked) {
                Text("Add a GIF to this day")
            }
        }
    }
}