package com.example.unsplashcl.data.dto

import com.example.unsplashcl.entity.*


class PhotoInRepoDto(
    val photo: PhotoInRepo,
    override val id: String = photo.id,
    override val urls: Urls = UrlsDto(
        full = photo.imgFull,
    ),
    override val likes: Int = photo.likes,
    override var likedByUser: Boolean = photo.likedByUser,
    override val user: User = UserDto(
        id = photo.userId,
        name = photo.userName,
        profileImage = UserDto.ProfileImageDto(
            small = photo.profileImage,
            large = photo.profileImage
        ),
        location = null,
        links = null
    ),
) : Photo {
    data class UrlsDto(
        override val full: String,
    ) : Urls

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
}