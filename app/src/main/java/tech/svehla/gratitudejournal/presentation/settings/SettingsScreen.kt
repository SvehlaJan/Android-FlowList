package tech.svehla.gratitudejournal.presentation.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch
import tech.svehla.gratitudejournal.R
import tech.svehla.gratitudejournal.domain.model.User
import tech.svehla.gratitudejournal.presentation.ui.components.SignInButton
import tech.svehla.gratitudejournal.presentation.ui.util.AuthResultContract

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var message by remember { mutableStateOf<String?>(null) }
    val user: User? by viewModel.currentUser.collectAsState(initial = null)
    val userLoggedIn: Boolean by viewModel.userLoggedIn.collectAsState(initial = false)
    var isLoading by remember { mutableStateOf(false) }
    val signInRequestCode = 1

    val authResultLauncher =
        rememberLauncherForActivityResult(contract = AuthResultContract()) { task ->
            try {
                val account = task?.getResult(ApiException::class.java)
                if (account == null) {
                    message = "Google sign in failed"
                } else {
                    coroutineScope.launch {
                        account.idToken?.let {
                            viewModel.signInWithGoogle(it)
                        } ?: run {
                            message = "Google sign in failed - no token"
                        }
                    }
                }
            } catch (e: ApiException) {
                message = "Google sign in failed"
            }
        }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (userLoggedIn) {
            Text(
                text = "Logged in as ${user?.email}",
                style = MaterialTheme.typography.h6
            )
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                viewModel.signOut()
            }) {
                Text(text = "Sign out")
            }
        } else {
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
            message?.let {
                isLoading = false
                Spacer(modifier = Modifier.height(30.dp))
                Text(text = it)
            }
        }
    }
}
