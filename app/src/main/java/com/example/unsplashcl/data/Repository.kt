package com.example.unsplashcl.data

import android.content.Context
import com.example.unsplashcl.R
import com.example.unsplashcl.data.dto.CollectionFromRepoDto
import com.example.unsplashcl.data.dto.NewCollectionInRepoDto
import com.example.unsplashcl.data.dto.NewPhotoInRepoDto
import com.example.unsplashcl.data.dto.PhotoInRepoDto
import com.example.unsplashcl.entity.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withTimeout
import java.io.IOException
import javax.inject.Inject


class Repository @Inject constructor(
    private val unsplashApi: UnsplashApi,
    private val unsplashDb: UnsplashDb,
    private val state: State
) {
    private val newPhotoInRepoDto = NewPhotoInRepoDto()
    private val newCollectionInRepoDto = NewCollectionInRepoDto()

    private val _repoStatusFlow = MutableStateFlow<String?>(null)
    val repoStatusFlow = _repoStatusFlow.asStateFlow()

    suspend fun getPhotos(page: Int, query: String?): List<Photo>? {
        return try {
            getPhotosFromApi(page, query)
        } catch (ex: Exception) {
            state.isOnLineCheck()
            _repoStatusFlow.emit("loading from server failed: ${ex.message}, trying to load from cache.")
            getPhotosFromDb()
        }
    }

    suspend fun getCollections(page: Int): List<PhotosCollection>? {
        return try {
            getCollectionsFromApi(page)
        } catch (ex: Exception) {
            state.isOnLineCheck()
            _repoStatusFlow.emit("loading from server failed: ${ex.message}, trying to load from cache.")
            getCollectionsFromDb()
        }
    }

    suspend fun getPhotosByCollectionId(page: Int, id: String): List<Photo>? {
        return try {
            getPhotosByCollectionIdFromApi(page, id)
        } catch (ex: Exception) {
            state.isOnLineCheck()
            _repoStatusFlow.emit("loading from server failed: ${ex.message}")
            null
        }
    }

    suspend fun getPhotosLikedByUser(page: Int, name: String): List<Photo>? {
        return try {
            getPhotosLikedByUserFromApi(page, name)
        } catch (ex: Exception) {
            state.isOnLineCheck()
            _repoStatusFlow.emit("loading from server failed: ${ex.message}.")
            null
        }
    }

    suspend fun getPhotoById(id: String): PhotoWithDetails? {
        return try {
            unsplashApi.getPhotoById(id)
        } catch (ex: Exception) {
            state.isOnLineCheck()
            _repoStatusFlow.emit("loading from server failed: ${ex.message}.")
            null
        }
    }

    private suspend fun getPhotosFromApi(page: Int, query: String?): List<Photo> {
        val photosList: List<Photo>
        withTimeout(5000L) {
            photosList = if (query != null) {
                unsplashApi.getPhotosByQuery(query, page)
            } else unsplashApi.getPhotos(page)
        }
        addPhotosToDb(photosList)
        return photosList
    }

    private suspend fun getPhotosByCollectionIdFromApi(page: Int, id: String): List<Photo>? {
        return try {
            withTimeout(5000L) {
                unsplashApi.getPhotosByCollectionId(page, id)
            }
        } catch (ex: Exception) {
            throw ex
        }
    }

    private suspend fun getPhotosLikedByUserFromApi(page: Int, name: String): List<Photo> {
        return try {
            withTimeout(5000L) {
                unsplashApi.getLikedPhotos(page, name)
            }
        } catch (ex: Exception) {
            throw ex
        }
    }

    private suspend fun getCollectionsFromApi(page: Int): List<PhotosCollection> {
        val collection: List<PhotosCollection>
        withTimeout(5000L) {
            collection = unsplashApi.getPhotosCollections(page)
        }
        addCollectionsToDb(collection)
        return collection
    }

    private suspend fun getCollectionsFromDb(): List<PhotosCollection>? {
        val photosList = emptyList<PhotosCollection>().toMutableList()
        return try {
            unsplashDb.getAllCollections().forEach {
                photosList.add(CollectionFromRepoDto(it))
            }
            if (photosList.isEmpty()) throw IOException("cache is empty")
            photosList
        } catch (ex: Exception) {
            _repoStatusFlow.emit("get collections from db failed: ${ex.message}")
            null
        }
    }

    private suspend fun getPhotosFromDb(): List<Photo>? {
        val photosList = emptyList<Photo>().toMutableList()
        return try {
            unsplashDb.getAllPhotos().forEach {
                photosList.add(PhotoInRepoDto(it))
            }
            if (photosList.isEmpty()) throw IOException("cache is empty")
            photosList
        } catch (ex: Exception) {
            _repoStatusFlow.emit("get photos from Db failed: ${ex.message}")
            null
        }
    }

    suspend fun likePhoto(id: String): Boolean {
        return try {
            unsplashApi.likePhoto(id)
            true
        } catch (ex: Exception) {
            state.isOnLineCheck()
            _repoStatusFlow.emit(ex.message)
            false
        }

    }

    suspend fun unlikePhoto(id: String): Boolean {
        return try {
            unsplashApi.unlikePhoto(id)
            true
        } catch (ex: Exception) {
            state.isOnLineCheck()
            _repoStatusFlow.emit(ex.message)
            false
        }
    }


    private suspend fun addPhotosToDb(photos: List<Photo>) {
        photos.forEach {
            unsplashDb.addPhoto(newPhotoInRepoDto.execute(it))
        }
    }

    private suspend fun addCollectionsToDb(collections: List<PhotosCollection>) {
        collections.forEach {
            unsplashDb.addCollection(newCollectionInRepoDto.execute(it))
        }
    }

    suspend fun getMyInfo(): User? {
        return try {
            unsplashApi.getMyInfo()
        } catch (ex: Exception) {
            state.isOnLineCheck()
            _repoStatusFlow.emit("loading from server failed: ${ex.message}")
            null
        }
    }

    suspend fun trackPhotoDownloadFromApi(id: String, ixid: String) {
        try {
            unsplashApi.trackPhotoDownload(id, ixid)
        } catch (ex: Exception) {
            _repoStatusFlow.emit("photo download tracking failed:$ex")
        }

    }

    fun setToken() {
        unsplashApi.setToken()
    }

    suspend fun clearDb() {
        unsplashDb.clearAll()
    }

    suspend fun statusReset() {
        _repoStatusFlow.emit(null)
    }
}