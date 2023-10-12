package com.example.unsplashcl.entity

interface PhotoWithDetails {
    val id: String
    val urls: UrlsDetails
    val links: PhotoLinks
    val likes: Int
    var likedByUser: Boolean
    val user: UserDetails
    val downloads: Int
    val exif: Exif?
    val location: Location?
    val tags: List<Tag>
}

interface UrlsDetails {
    val raw: String
    val full: String
    val small: String
}

interface PhotoLinks {
    val downloadLocation: String
}

interface Exif {
    val make: String?
    val model: String?
    val exposureTime: String?
    val aperture: String?
    val focalLength: String?
    val iso: Int?
}

interface Location {
    val city: String?
    val country: String?
    val position: Position?
}

interface Position {
    val latitude: Double?
    val longitude: Double?
}

interface Tag {
    val title: String
}