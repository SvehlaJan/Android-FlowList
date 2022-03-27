package tech.svehla.gratitudejournal.presentation.main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import javax.inject.Inject

interface AuthDelegate {
    fun signInAnonymously()
    fun onGoogleSignInResult(task: Task<GoogleSignInAccount>?)
    val signInErrorMessage: State<String>
}

class AuthDelegateImpl @Inject constructor() : AuthDelegate {
    override val signInErrorMessage: State<String> = mutableStateOf("")

    override fun signInAnonymously() {
        TODO("Not yet implemented")
    }

    override fun onGoogleSignInResult(task: Task<GoogleSignInAccount>?) {
//        try {
//            val account = task?.getResult(ApiException::class.java)
//            if (account == null) {
//                _state.value = _state.value.copy(signInErrorMessage = "Google sign in failed")
//            } else {
//                account.idToken?.let {
//                    _state.value = _state.value.copy(signInErrorMessage = null)
//                    signInWithGoogle(it)
//                } ?: run {
//                    _state.value =
//                        _state.value.copy(signInErrorMessage = "Google sign in failed - no token")
//                }
//            }
//        } catch (e: ApiException) {
//            _state.value = _state.value.copy(signInErrorMessage = "Google sign in failed")
//        }
    }

}