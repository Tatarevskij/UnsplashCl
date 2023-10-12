package com.example.unsplashcl.authorization

import android.net.Uri
import androidx.core.net.toUri
import net.openid.appauth.*
import kotlin.coroutines.suspendCoroutine

object AppAuth {

    private val serviceConfiguration = AuthorizationServiceConfiguration(
        Uri.parse(AuthConfig.AUTH_URI),
        Uri.parse(AuthConfig.TOKEN_URI),
        null, // registration endpoint
      //  Uri.parse(AuthConfig.END_SESSION_URI)
    )

    fun getAuthRequest(): AuthorizationRequest {
        val redirectUri = AuthConfig.CALLBACK_URL.toUri()

        return AuthorizationRequest.Builder(
            serviceConfiguration,
            AuthConfig.CLIENT_ID,
            AuthConfig.RESPONSE_TYPE,
            redirectUri
        )
            .setScopes(AuthConfig.SCOPE)
            .build()
    }

    /*fun getEndSessionRequest(): EndSessionRequest {
        return EndSessionRequest.Builder(serviceConfiguration)
            .setPostLogoutRedirectUri(AuthConfig.LOGOUT_CALLBACK_URL.toUri())
            .build()
    }*/

    fun getRefreshTokenRequest(refreshToken: String): TokenRequest {
        return TokenRequest.Builder(
            serviceConfiguration,
            AuthConfig.CLIENT_ID
        )
            .setGrantType(GrantTypeValues.REFRESH_TOKEN)
            .setScopes(AuthConfig.SCOPE)
            .setRefreshToken(refreshToken)
            .build()
    }

    suspend fun performTokenRequestSuspend(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
    ): TokensModel {
        return suspendCoroutine { continuation ->
            authService.performTokenRequest(tokenRequest, getClientAuthentication()) { response, ex ->
                when {
                    response != null -> {
                        //получение токена произошло успешно
                        val tokens = TokensModel(
                            accessToken = response.accessToken.orEmpty(),
                            refreshToken = response.refreshToken.orEmpty(),
                            idToken = response.idToken.orEmpty()
                        )
                        continuation.resumeWith(Result.success(tokens))
                    }
                    //получение токенов произошло неуспешно, показываем ошибку
                    ex != null -> { continuation.resumeWith(Result.failure(ex)) }
                    else -> error("unreachable")
                }
            }
        }
    }

    private fun getClientAuthentication(): ClientAuthentication {
        return ClientSecretPost(AuthConfig.CLIENT_SECRET)
    }

    private object AuthConfig {
        const val AUTH_URI = "https://unsplash.com/oauth/authorize"
        const val TOKEN_URI = "https://unsplash.com/oauth/token"
        // const val END_SESSION_URI = ""
        const val RESPONSE_TYPE = ResponseTypeValues.CODE
        val SCOPE = mutableListOf("public", "read_user", "read_photos", "write_likes", "read_collections")
        const val CLIENT_ID = "u9W4mf7Nm7LXMJN1RVqjWv8PtJ15y6NBl76YcKnQ4SM"
        const val CLIENT_SECRET = "oDYtdDkaBeUoJxJM9xuAj_wIg7EipgT_hMI-lWxFssA"
        const val CALLBACK_URL = "com.example.unsplashcl:/"
        // const val LOGOUT_CALLBACK_URL = "ru.kts.oauth://github.com/logout_callback"
    }
}