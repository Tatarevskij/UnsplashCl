package com.example.unsplashcl.domain

import com.example.unsplashcl.data.Repository
import com.example.unsplashcl.entity.Photo
import com.example.unsplashcl.entity.PhotoWithDetails
import javax.inject.Inject

class GetPhotoByIdUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend fun execute(id: String): PhotoWithDetails? {
        return repository.getPhotoById(id)
    }
}