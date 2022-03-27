package tech.svehla.gratitudejournal.presentation.ui.components

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.viewinterop.AndroidView
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.core.models.enums.MediaType
import com.giphy.sdk.ui.GiphyLoadingProvider
import com.giphy.sdk.ui.getPlaceholderColor
import com.giphy.sdk.ui.pagination.GPHContent
import com.giphy.sdk.ui.views.GPHGridCallback
import com.giphy.sdk.ui.views.GiphyGridView
import tech.svehla.gratitudejournal.presentation.ui.view.GiphyLoadingDrawable

@Composable
fun GiphyPicker(
    modifier: Modifier = Modifier,
    onMediaSelected: (Media?) -> Unit
) {
    val focusRequester = FocusRequester()
    var searchQuery by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        focusRequester.requestFocus()
        onDispose { }
    }

    BackHandler {
        onMediaSelected(null)
    }

    Column(
        modifier = modifier
    ) {
        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { context ->
                GiphyGridView(
                    context = context
                ).apply {
                    direction = GiphyGridView.VERTICAL
                    spanCount = 2
                    cellPadding = 20
                    callback = object : GPHGridCallback {
                        override fun contentDidUpdate(resultCount: Int) {}

                        override fun didSelectMedia(media: Media) {
                            onMediaSelected(media)
                        }
                    }

                    val loadingProviderClient = object : GiphyLoadingProvider {
                        override fun getLoadingDrawable(position: Int): Drawable {
                            val shape =
                                if (position % 2 == 0) GiphyLoadingDrawable.Shape.Rect else GiphyLoadingDrawable.Shape.Circle
                            val drawable = GiphyLoadingDrawable(shape)
                            drawable.setColorFilter(getPlaceholderColor(), PorterDuff.Mode.SRC_ATOP)
                            return drawable
                        }
                    }

                    setGiphyLoadingProvider(loadingProviderClient)
                    content = GPHContent.recents
                }
            }, update = { view ->
                view.content = GPHContent.searchQuery(searchQuery, mediaType = MediaType.gif)
            })
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = searchQuery,
            placeholder = { Text("Search Giphy") },
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            onValueChange = {
                searchQuery = it
            }
        )
    }
}