package com.example.unsplashcl.data.dto

import com.example.unsplashcl.entity.*


class CollectionFromRepoDto(
    private val collectionInRepo: CollectionInRepo,

    override val id: String = collectionInRepo.id,
    override val title: String = collectionInRepo.title,
    override val totalPhotos: Int = collectionInRepo.totalPhotos,
    override val user: User = UserDto(
        id = collectionInRepo.userId,
        name = collectionInRepo.userName,
        profileImage = UserDto.ProfileImageDto(
            small = collectionInRepo.iconUrl,
            large = collectionInRepo.iconUrl
        ),
        location = null,
        links = null
    ),
    override val coverPhoto: Photo = PhotoDto(
        id = collectionInRepo.coverId,
        urls = PhotoDto.UrlsDto(
            full = collectionInRepo.imgUrl,
        ),
        likes = 0,
        likedByUser = false,
        user = UserDto(
            id = "",
            name = "",
            profileImage = UserDto.ProfileImageDto(
                small = "",
                large = ""
            ),
            location = null,
            links = null
        ),
    ),

    ): PhotosCollection {
    data class UserDto(
        override val id: String,
        override val name: String,
        override val profileImage: ProfileImageDto,
        override val location: String?,
        override val links: Links?
    ) : User {
        data class ProfileImageDto(
            override val small: String,
            override val large: String
        ) : ProfileImage
    }
    data class PhotoDto(
        override val id: String,
        override val urls: Urls,
        override val likes: Int,
        override var likedByUser: Boolean,
        override val user: User,
    ): Photo {
        data class UrlsDto(
            override val full: String,
        ): Urls
    }
}