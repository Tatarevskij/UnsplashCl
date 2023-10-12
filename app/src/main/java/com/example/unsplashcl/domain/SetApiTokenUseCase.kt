package com.example.unsplashcl.domain

import com.example.unsplashcl.data.Repository
import javax.inject.Inject

class SetApiTokenUseCase @Inject constructor(
    private val repository: Repository
) {
    fun execute() {
        repository.setToken()
    }
}