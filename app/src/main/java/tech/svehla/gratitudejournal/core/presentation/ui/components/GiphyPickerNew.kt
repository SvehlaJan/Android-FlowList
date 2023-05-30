package tech.svehla.gratitudejournal.core.presentation.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.themes.GPHTheme
import com.giphy.sdk.ui.views.dialogview.GiphyDialogView
import com.giphy.sdk.ui.views.dialogview.setup
import timber.log.Timber

@Composable
fun GiphyPickerNew(
    modifier: Modifier = Modifier,
    onMediaSelected: (Media?) -> Unit,
) {
    val focusRequester = FocusRequester()
    var searchQuery by remember { mutableStateOf("") }
    var contentType by rememberSaveable { mutableStateOf(GPHContentType.gif) }
    var showView by rememberSaveable { mutableStateOf(false) }
    var offset by rememberSaveable { mutableStateOf(0f) }
    val configuration = LocalConfiguration.current

    DisposableEffect(Unit) {
//        focusRequester.requestFocus()
        onDispose { }
    }

    BackHandler {
        onMediaSelected(null)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(LocalConfiguration.current.screenHeightDp.dp)
            .background(Color.Black.copy(alpha = 0.5f))
    ) {
        AndroidView(
            factory = { ctx ->
                val settings = GPHSettings(theme = GPHTheme.Light, stickerColumnCount = 3)
                GiphyDialogView(ctx).apply {
                    setup(
                        settings.copy(selectedContentType = contentType),
                    )
                    this.listener = object : GiphyDialogView.Listener {
                        override fun onGifSelected(
                            media: Media,
                            searchTerm: String?,
                            selectedContentType: GPHContentType,
                        ) {
                            onMediaSelected(media)
                            showView = false
                        }

                        override fun onClosed(selectedContentType: GPHContentType) {
                            contentType = selectedContentType
                        }

                        override fun didSearchTerm(term: String) {
                            Timber.d("didSearchTerm: $term")
                        }

                        override fun onFocusSearch() {
                            Timber.d("onFocusSearch")
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(LocalConfiguration.current.screenHeightDp.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(35.dp)
                .background(Color.Transparent)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { change, dragAmount ->
                            offset += change.positionChange().y
                            if (offset > configuration.screenHeightDp.dp.toPx() * 0.6 || dragAmount > 50) {
                                showView = false
                            }
                        },
                        onDragEnd = {
                            if (offset <= configuration.screenHeightDp.dp.toPx() * 0.6) {
                                offset = 0f
                            }
                        },
                        onDragCancel = {
                            if (offset <= configuration.screenHeightDp.dp.toPx() * 0.6) {
                                offset = 0f
                            }
                        }
                    )
                }
        )
    }
}