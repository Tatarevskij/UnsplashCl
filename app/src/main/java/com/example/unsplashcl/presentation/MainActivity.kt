package com.example.unsplashcl.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.unsplashcl.App
import com.example.unsplashcl.R
import com.example.unsplashcl.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!


    @SuppressLint("SuspiciousIndentation")
    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (!map.values.all { it })
                Toast.makeText(this, "Permission is not granted", Toast.LENGTH_SHORT).show()
        }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermission()

        lifecycleScope.launch {
            viewModel.statusFlow.collect {
                if (it.appStatus != null)
                    Toast.makeText(this@MainActivity, it.appStatus, Toast.LENGTH_SHORT).show()
                if (it.repoStatus != null)
                    Toast.makeText(this@MainActivity, it.repoStatus, Toast.LENGTH_SHORT).show()
                viewModel.statusReset()
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val photosList = viewModel.photosToLoad
            viewModel.state.onLineStatusFlow.collect { onLine ->
                if (onLine && photosList.isNotEmpty()) {
                    photosList.forEach { photoItem ->
                       val uri = viewModel.saveImage(photoItem)
                        if (uri != null) {
                            viewModel.trackPhotoDownloadUseCase.execute(photoItem.id, photoItem.links.downloadLocation)
                            createNotification(getString(R.string.image_saved), uri)
                        }
                    }
                    photosList.clear()
                }
            }
        }

        binding.collectionsBtn.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_collectionsFragment)
        }
        binding.userBtn.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_userFragment)
        }
        binding.photosBtn.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_photosFragment)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermission(): Boolean {
        val isAllGranted = REQUEST_PERMISSIONS.all { permission ->
            this.let {
                ContextCompat.checkSelfPermission(
                    it,
                    permission
                )
            } == PackageManager.PERMISSION_GRANTED
        }
        if (isAllGranted) {
            Toast.makeText(this, "permission is Granted", Toast.LENGTH_SHORT).show()
            return true
        } else {
            launcher.launch(REQUEST_PERMISSIONS)
        }
        return false
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun createNotification(title: String, uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        else
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val notification = NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(uri)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val NOTIFICATION_ID = 1000

        private val REQUEST_PERMISSIONS: Array<String> = buildList {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }
}