package com.example.unsplashcl.data.dto

import com.example.unsplashcl.entity.Links
import com.example.unsplashcl.entity.ProfileImage
import com.example.unsplashcl.entity.User
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id") override val id: String,
    @Json(name = "username") override val name: String,
    @Json(name = "profile_image") override val profileImage: ProfileImageDto,
    @Json(name = "location") override val location: String?,
    @Json(name = "links") override val links: LinksDto
) : User {
    @JsonClass(generateAdapter = true)
    data class ProfileImageDto(
        @Json(name = "small") override val small: String,
        @Json(name = "large") override val large: String
    ) : ProfileImage

    @JsonClass(generateAdapter = true)
    data class LinksDto(
        @Json(name = "likes") override val likes: String
    ): Links
}