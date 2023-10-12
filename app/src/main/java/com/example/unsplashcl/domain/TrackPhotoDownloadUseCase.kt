package com.example.unsplashcl.domain

import com.example.unsplashcl.data.Repository
import javax.inject.Inject

class TrackPhotoDownloadUseCase @Inject constructor(
    private val repository: Repository
) {
   suspend fun execute(id: String, ixid: String) {
        repository.trackPhotoDownloadFromApi(id, ixid)
    }
}