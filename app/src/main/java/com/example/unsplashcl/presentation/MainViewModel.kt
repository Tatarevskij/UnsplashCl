package com.example.unsplashcl.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.bumptech.glide.Glide
import com.example.unsplashcl.ACCESS_TOKEN
import com.example.unsplashcl.data.pagingSource.CollectionsPagingSource
import com.example.unsplashcl.data.pagingSource.LikedByUserPhotosPagingSource
import com.example.unsplashcl.data.pagingSource.PhotosByCollectionPagingSource
import com.example.unsplashcl.data.pagingSource.PhotosPagingSource
import com.example.unsplashcl.domain.*
import com.example.unsplashcl.entity.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val getRepositoryUseCase: GetRepositoryUseCase,
    private val tokenPrefs: SharedPreferences,
    private val getPhotoByIdUseCase: GetPhotoByIdUseCase,
    private val clearDbUseCase: ClearDbUseCase,
    private val likePhotoUseCase: LikePhotoUseCase,
    private val unlikePhotoUseCase: UnlikePhotoUseCase,
    private val getRepoStatusFlowUseCase: GetRepoStatusFlowUseCase,
    private val repoStatusResetUseCase: RepoStatusResetUseCase,
    private val getMyInfoUseCase: GetMyInfoUseCase,
    val trackPhotoDownloadUseCase: TrackPhotoDownloadUseCase,
    val state: State,
    application: Application
) : AndroidViewModel(application) {
    val photosToLoad: MutableList<PhotoWithDetails> = mutableListOf()

    private val _photoByIdFlow = MutableStateFlow<PhotoWithDetails?>(null)
    val photoByIdFlow = _photoByIdFlow.asStateFlow()

    private val _appStatusFlow = MutableStateFlow<String?>(null)
    val appStatusFlow = _appStatusFlow.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val statusFlow: StateFlow<AppState> = combine(getRepoStatusFlowUseCase.execute(), appStatusFlow) { repo, app ->
        AppState(repo, app)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initialValue = AppState())

    data class AppState(
        val repoStatus: String? = null,
        val appStatus: String? = null
    )

    private fun loadPhotosPagingFlow(query: String?): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(1),
            pagingSourceFactory = { PhotosPagingSource(getRepositoryUseCase.execute(), query) }
        ).flow.cachedIn(viewModelScope)
    }

    private fun loadCollectionsPagingFlow(): Flow<PagingData<PhotosCollection>> {
        return Pager(
            config = PagingConfig(1),
            pagingSourceFactory = { CollectionsPagingSource(getRepositoryUseCase.execute()) }
        ).flow.cachedIn(viewModelScope)
    }

    private fun loadPhotosByCollectionIdPagingFlow(id: String): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(1),
            pagingSourceFactory = {
                PhotosByCollectionPagingSource(
                    getRepositoryUseCase.execute(),
                    id
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    private fun loadPhotosLikedByUserPagingFlow(name: String): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(1),
            pagingSourceFactory = {
                LikedByUserPhotosPagingSource(
                    getRepositoryUseCase.execute(),
                    name
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    fun loadPhotos(photosPagedAdapter: PhotosPagedAdapter, query: String? = null) {
        viewModelScope.launch {
            photosPagedAdapter.loadStateFlow.collect{
                _isLoading.emit(it.source.refresh is LoadState.Loading)
                if (it.source.refresh is LoadState.Error){
                    _isLoading.value = false
                    _appStatusFlow.emit((it.source.refresh as LoadState.Error).error.message)
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            loadPhotosPagingFlow(query).collect {
                photosPagedAdapter.submitData(it)

            }
        }
    }

    fun loadCollections(collectionsPagedAdapter: CollectionsPagedAdapter) {
        viewModelScope.launch {
            collectionsPagedAdapter.loadStateFlow.collect{
                _isLoading.emit(it.source.refresh is LoadState.Loading)
                if (it.source.refresh is LoadState.Error){
                    _isLoading.value = false
                    _appStatusFlow.emit((it.source.refresh as LoadState.Error).error.message)
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            loadCollectionsPagingFlow().collect {
                collectionsPagedAdapter.submitData(it)
            }
        }
    }

    fun loadPhotosByCollectionId(photosPagedAdapter: PhotosPagedAdapter, id: String) {
        viewModelScope.launch {
            photosPagedAdapter.loadStateFlow.collect{
                _isLoading.emit(it.source.refresh is LoadState.Loading)
                if (it.source.refresh is LoadState.Error){
                    _isLoading.value = false
                    _appStatusFlow.emit((it.source.refresh as LoadState.Error).error.message)
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            loadPhotosByCollectionIdPagingFlow(id).collect {
                photosPagedAdapter.submitData(it)
            }
        }
    }

    fun loadPhotosLikedByUser(photosPagedAdapter: PhotosPagedAdapter, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loadPhotosLikedByUserPagingFlow(name).collect {
                photosPagedAdapter.submitData(it)
            }
        }
    }

    fun loadPhotoById(id: String) {
        viewModelScope.launch {
            _isLoading.emit(true)
            val photo = getPhotoByIdUseCase.execute(id)
            _photoByIdFlow.emit(photo)
            _isLoading.emit(false)
        }
    }

    suspend fun getMyInfo(): User? {
        return getMyInfoUseCase.execute()
    }

    fun clearDb() {
        viewModelScope.launch {
            clearDbUseCase.execute()
        }
    }

    fun likePhoto(id: String) {
        viewModelScope.launch {
            state.onLine = likePhotoUseCase.execute(id)
        }
    }

    fun unlikePhoto(id: String) {
        viewModelScope.launch {
            state.onLine = unlikePhotoUseCase.execute(id)
        }
    }

    fun getToken(): String? {
        println(tokenPrefs.getString(ACCESS_TOKEN, null))
        return tokenPrefs.getString(ACCESS_TOKEN, null)
    }

    fun removeToken() {
        tokenPrefs.edit().remove(ACCESS_TOKEN).apply()
    }

    suspend fun statusReset() {
        _appStatusFlow.emit(null)
        repoStatusResetUseCase.execute()
    }

    suspend fun saveImage(photoItem: PhotoWithDetails): String? {
        val name: String = photoItem.id + System.currentTimeMillis()
        val imageBitmap: Bitmap = getBitmap(photoItem.urls.raw)
        return saveBitmapInStorage(
            imageBitmap,
            name
        )
    }

    private fun getBitmap(url: String): Bitmap {
        return Glide.with(getApplication<Application>())
            .asBitmap()
            .load(url)
            .submit()
            .get()
    }

   @SuppressLint("SuspiciousIndentation")
  private suspend fun saveBitmapInStorage(bitmap: Bitmap, name: String): String? {
        var fos: OutputStream? = null
        var uri: Uri? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getApplication<Application>().contentResolver?.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                    try {
                        uri =
                            resolver.insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                contentValues
                            )!!
                        fos = resolver.openOutputStream(uri!!)
                    } catch (e: Exception) {
                        _appStatusFlow.emit(e.message)
                        println(e)
                    }

                }
            } else {
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, name)
                fos = withContext(Dispatchers.IO) {
                    FileOutputStream(image)
                }
                uri = image.toUri()
            }

            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                return uri.toString()
            } catch (e: Exception) {
                _appStatusFlow.emit("Image save failed:" + e.message)
                println(e)
            }
       return null
    }
}