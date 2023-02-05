package tech.svehla.gratitudejournal.settings.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tech.svehla.gratitudejournal.R
import tech.svehla.gratitudejournal.core.presentation.ui.components.SignInWithGoogleButton
import tech.svehla.gratitudejournal.core.presentation.ui.util.AuthResultContract

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun SettingsScreenRoute(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val signInRequestCode = 1
    val authResultLauncher =
        rememberLauncherForActivityResult(contract = AuthResultContract()) { task ->
            viewModel.onGoogleSignInResult(task)
        }

    val state: SettingsScreenState by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onSignInClicked = {
            viewModel.onSignInClicked()
            authResultLauncher.launch(signInRequestCode)
        },
        onSignOutClicked = {
            viewModel.signOut()
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsScreen(
    @PreviewParameter(SettingsScreenStateParameterProvider::class) state: SettingsScreenState,
    onSignOutClicked: () -> Unit = {},
    onSignInClicked: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            elevation = 10.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                if (state.currentUser != null) {
                    Text(
                        text = "Logged in as ${state.currentUser.email}",
                        style = MaterialTheme.typography.body1
                    )
                    Spacer(Modifier.height(32.dp))
                    Button(
                        modifier = Modifier.align(Alignment.End),
                        onClick = onSignOutClicked
                    ) {
                        Text(text = "Sign out")
                    }
                } else {
                    Text(
                        text = "Sign in to sync your journal entries across devices",
                        style = MaterialTheme.typography.body1
                    )
                    Spacer(Modifier.height(32.dp))
                    SignInWithGoogleButton(
                        modifier = Modifier.align(Alignment.End),
                        text = "Sign in with Google",
                        loadingText = "Signing in...",
                        isLoading = state.isLoading,
                        icon = painterResource(id = R.drawable.ic_google_logo),
                        onClick = onSignInClicked
                    )
                    state.signInErrorMessage?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = it)
                    }
                }
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            elevation = 10.dp,
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Used libraries",
                    style = MaterialTheme.typography.body1,
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "AndroidX bundle",
                    style = MaterialTheme.typography.body2,
                )
                Text(
                    text = "Firebase",
                    style = MaterialTheme.typography.body2,
                )
                Text(
                    text = "Giphy",
                    style = MaterialTheme.typography.body2,
                )
                Text(
                    text = "Timber",
                    style = MaterialTheme.typography.body2,
                )
            }
        }
    }
}

object SettingsScreenStateParameterProvider : PreviewParameterProvider<SettingsScreenState> {
    override val values: Sequence<SettingsScreenState>
        get() = sequenceOf(
            SettingsScreenState(
                currentUser = null,
                isLoading = false,
                signInErrorMessage = null
            ),
            SettingsScreenState(
                currentUser = null,
                isLoading = true,
                signInErrorMessage = null
            ),
        )
}