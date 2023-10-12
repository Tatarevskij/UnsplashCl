package com.example.unsplashcl.presentation.onboarding


import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsplashcl.ACCESS_TOKEN
import com.example.unsplashcl.REFRESH_TOKEN
import com.example.unsplashcl.authorization.AppAuth
import com.example.unsplashcl.domain.SetApiTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    val tokenPrefs: SharedPreferences,
    private val appAuth: AppAuth,
    private val setApiTokenUseCase: SetApiTokenUseCase,
    application: Application
) : AndroidViewModel(application) {

    private val authService: AuthorizationService = AuthorizationService(getApplication())
    private val openAuthPageEventChannel = Channel<Intent>(Channel.BUFFERED)
    private val authSuccessEventChannel = Channel<Unit>(Channel.BUFFERED)

    val openAuthPageFlow: Flow<Intent>
        get() = openAuthPageEventChannel.receiveAsFlow()

    val authSuccessFlow: Flow<Unit>
        get() = authSuccessEventChannel.receiveAsFlow()

    fun onAuthCodeReceived(tokenRequest: TokenRequest) {
        viewModelScope.launch {
            runCatching {
                appAuth.performTokenRequestSuspend(
                    authService = authService,
                    tokenRequest = tokenRequest
                )
            }.onSuccess {
                authSuccessEventChannel.send(Unit)
                tokenPrefs.edit()
                    .putString(ACCESS_TOKEN, it.accessToken)
                    .putString(REFRESH_TOKEN, it.refreshToken)
                    .apply()
                setApiTokenUseCase.execute()
            }.onFailure {
                Toast.makeText(getApplication(), it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openLoginPage() {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        val authRequest = AppAuth.getAuthRequest()

        val openAuthPageIntent = authService.getAuthorizationRequestIntent(
            authRequest,
            customTabsIntent
        )
        openAuthPageEventChannel.trySendBlocking(openAuthPageIntent)
    }

    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }
}
