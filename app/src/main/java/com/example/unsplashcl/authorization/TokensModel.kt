package com.example.unsplashcl.authorization

data class TokensModel(
    val accessToken: String,
    val refreshToken: String,
    val idToken: String
)
