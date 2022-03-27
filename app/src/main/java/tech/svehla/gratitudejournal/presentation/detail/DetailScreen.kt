package tech.svehla.gratitudejournal.presentation.detail

import android.os.Build.VERSION.SDK_INT
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.size.Size
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.svehla.gratitudejournal.R
import tech.svehla.gratitudejournal.presentation.ui.ErrorScreen
import tech.svehla.gratitudejournal.presentation.ui.LoadingScreen
import tech.svehla.gratitudejournal.presentation.ui.components.GiphyPicker
import tech.svehla.gratitudejournal.presentation.ui.components.NumberPicker
import tech.svehla.gratitudejournal.presentation.ui.components.SignInButton
import tech.svehla.gratitudejournal.presentation.ui.util.AuthResultContract

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DetailScreen(
    date: String,
    viewModel: DetailViewModel,
    onBackPressed: () -> Unit
) {
    val state: DetailScreenState by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    )

    state.events.firstOrNull()?.let { event ->
        LaunchedEffect(event) {
            when (event) {
                is UIEvent.ShowSignInBottomSheet -> {
                    coroutineScope.launch(Dispatchers.Main) {
                        bottomSheetScaffoldState.bottomSheetState.expand()
                    }
                }
                is UIEvent.NavigateBack -> {
                    onBackPressed()
                }
            }
            // Once the event is consumed, notify the ViewModel.
            viewModel.onEventConsumed(event.id)
        }
    }


    val authResultLauncher =
        rememberLauncherForActivityResult(contract = AuthResultContract()) { task ->
            viewModel.onGoogleSignInResult(task)
        }

    BackHandler {
        val forceBackNavigation = bottomSheetScaffoldState.bottomSheetState.isExpanded
        viewModel.onBackPressed(forceBackNavigation)
    }

    when (state.uiState) {
        UIState.Content -> {
            BottomSheetScaffold(
                scaffoldState = bottomSheetScaffoldState,
                sheetShape = RoundedCornerShape(16.dp),
                sheetContent = {
                    BottomSheetContent(
                        authResultLauncher = authResultLauncher,
                        googleSingInMessage = state.signInErrorMessage,
                        onAnonymousSignInClicked = {
                            viewModel.signInAnonymously()
                        },
                    )
                },
                sheetPeekHeight = 0.dp
            ) {
                DetailScreenContent(
                    viewModel = viewModel,
                    onSelectGifClicked = {
                        viewModel.onSelectGifClicked()
                    }
                )
            }
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
                message = (state.uiState as UIState.Error).errorMessage,
                retry = {
                    viewModel.getDetail(date)
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
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = viewModel.date.value,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
        )
        OutlinedTextField(
            label = { Text("First Note") },
            value = viewModel.firstNote.value,
            keyboardOptions = defaultKeyboardOptions,
            onValueChange = {
                viewModel.firstNote.value = it
            },
        )
        OutlinedTextField(
            label = { Text("Second Note") },
            value = viewModel.secondNote.value,
            keyboardOptions = defaultKeyboardOptions,
            onValueChange = {
                viewModel.secondNote.value = it
            },
        )
        OutlinedTextField(
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

        if (viewModel.gifUrl.value != null) {
            Image(
                painter = rememberImagePainter(
                    imageLoader = imageLoader,
                    data = viewModel.gifUrl.value!!,
                    builder = {
                        size(Size.ORIGINAL)
                    }
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
            )
        } else {
            Button(onClick = onSelectGifClicked) {
                Text("Add a GIF to this day")
            }
        }
    }
}

@Composable
fun BottomSheetContent(
    authResultLauncher: ManagedActivityResultLauncher<Int, Task<GoogleSignInAccount>?>,
    googleSingInMessage: String?,
    onAnonymousSignInClicked: () -> Unit,
) {
    val signInRequestCode = 1
    var isLoading by remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = "Do you want to sync your notes across devices?")
            Spacer(modifier = Modifier.weight(1f))
            SignInButton(
                text = "Sign in with Google",
                loadingText = "Signing in...",
                isLoading = isLoading,
                icon = painterResource(id = R.drawable.ic_google_logo),
                onClick = {
                    isLoading = true
                    authResultLauncher.launch(signInRequestCode)
                }
            )
            googleSingInMessage?.let {
                isLoading = false
                Spacer(modifier = Modifier.height(30.dp))
                Text(text = it)
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onAnonymousSignInClicked) {
                Text(text = "Continue without signing in")
            }
        }
    }
}