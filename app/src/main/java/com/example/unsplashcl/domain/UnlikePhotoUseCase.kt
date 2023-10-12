package com.example.unsplashcl.domain

import com.example.unsplashcl.data.Repository
import javax.inject.Inject

class UnlikePhotoUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend fun execute(id: String): Boolean {
        return repository.unlikePhoto(id)
    }
}