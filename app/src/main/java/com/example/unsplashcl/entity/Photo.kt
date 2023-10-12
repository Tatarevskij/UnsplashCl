package com.example.unsplashcl.entity

interface Photo {
    val id: String
    val urls: Urls
    val likes: Int
    var likedByUser: Boolean
    val user: User
}

interface Urls {
    val full: String
}

interface PhotosList {
    val photosList: List<Photo>
}