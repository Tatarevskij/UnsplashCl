package com.example.unsplashcl.entity

interface User {
    val id: String
    val name: String
    val profileImage: ProfileImage
    val location: String?
    val links: Links?
}

interface ProfileImage {
    val small: String
    val large: String
}

interface Links {
    val likes: String
}

interface UserDetails {
    val id: String
    val name: String
    val profileImage: ProfileImage
    val location: String?
    val links: Links?
}