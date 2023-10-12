package com.example.unsplashcl.domain

import com.example.unsplashcl.data.Repository
import com.example.unsplashcl.entity.User
import com.example.unsplashcl.entity.UserDetails
import javax.inject.Inject

class GetMyInfoUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend fun execute(): User? {
       return repository.getMyInfo()
    }
}