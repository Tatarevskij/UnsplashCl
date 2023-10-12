package com.example.unsplashcl.domain

import com.example.unsplashcl.data.Repository
import javax.inject.Inject

class RepoStatusResetUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend fun execute() {
        repository.statusReset()
    }
}