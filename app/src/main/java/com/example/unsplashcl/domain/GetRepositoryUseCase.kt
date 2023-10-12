package com.example.unsplashcl.domain

import com.example.unsplashcl.data.Repository
import javax.inject.Inject

class GetRepositoryUseCase @Inject constructor(
    private val repository: Repository
) {
    fun execute(): Repository{
        return repository
    }
}