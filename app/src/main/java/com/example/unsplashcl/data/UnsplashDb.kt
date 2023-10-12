package com.example.unsplashcl.data

import com.example.unsplashcl.entity.*
import javax.inject.Inject

class UnsplashDb @Inject constructor(
    private val photoDao: PhotoDao
) {
    fun getAllPhotos(): List<PhotoInRepo> {
        return photoDao.getAll()
    }

    fun getAllCollections(): List<CollectionInRepo> {
        return photoDao.getAllCollections()
    }

    suspend fun addPhoto(photo: NewPhotoInRepo) {
        photoDao.insert(photo)
    }

    suspend fun addCollection(collection: NewCollectionInRepo) {
        photoDao.insertCollection(collection)
    }

    suspend fun clearAll() {
        photoDao.deleteAllPhotos()
        photoDao.deleteAllCollections()
    }
}