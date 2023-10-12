package com.example.unsplashcl.entity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class State @Inject constructor(
    private val application: Context
)  {
    private val _onLineStatusFlow = MutableStateFlow(false)
    val onLineStatusFlow = _onLineStatusFlow.asStateFlow()

    var loading: Boolean = false
    var onLine: Boolean = false

    fun isOnLineCheck() {
        runBlocking(Dispatchers.IO) {
            val trigger = isNetworkAvailable()
            onLine = trigger
            _onLineStatusFlow.emit(trigger)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getApplication(application).getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // For 29 api or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->    true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ->   true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->   true
                else ->     false
            }
        }
        // For below 29 api
        else {
            if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting) {
                return true
            }
        }
        return false
    }
}