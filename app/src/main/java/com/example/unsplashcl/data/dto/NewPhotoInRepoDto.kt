package com.example.unsplashcl.data.dto

import com.example.unsplashcl.entity.NewPhotoInRepo
import com.example.unsplashcl.entity.Photo
import javax.inject.Inject

class NewPhotoInRepoDto @Inject constructor(
) {
    fun execute(photo: Photo): NewPhotoInRepo {
        return NewPhotoInRepo(
            id = photo.id,
            imgFull = photo.urls.full,
            likes = photo.likes,
            likedByUser = photo.likedByUser,
            userId = photo.user.id,
            userName = photo.user.name,
            profileImage = photo.user.profileImage.large,
        )
    }
}