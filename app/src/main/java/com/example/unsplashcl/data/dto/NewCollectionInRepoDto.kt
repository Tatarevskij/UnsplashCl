package com.example.unsplashcl.data.dto

import com.example.unsplashcl.entity.NewCollectionInRepo
import com.example.unsplashcl.entity.PhotosCollection

class NewCollectionInRepoDto(
) {
    fun execute(collection: PhotosCollection): NewCollectionInRepo {
        return NewCollectionInRepo(
            id = collection.id,
            title = collection.title,
            totalPhotos = collection.totalPhotos,
            imgUrl = collection.coverPhoto.urls.full,
            iconUrl = collection.user.profileImage.large,
            userName = collection.user.name,
            coverId = collection.coverPhoto.id,
            userId = collection.user.id
        )
    }
}