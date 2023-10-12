package com.example.unsplashcl.entity

interface PhotosCollection {
    val id: String
    val title: String
    val totalPhotos: Int
    val user: User
    val coverPhoto: Photo
}