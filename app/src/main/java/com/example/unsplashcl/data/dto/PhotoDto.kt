package com.example.unsplashcl.data.dto

import com.example.unsplashcl.entity.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import javax.inject.Inject

@JsonClass(generateAdapter = true)
data class PhotoDto @Inject constructor(
    @Json(name = "id") override val id: String,
    @Json(name = "urls") override val urls: UrlsDto,
    @Json(name = "likes") override val likes: Int,
    @Json(name = "liked_by_user") override var likedByUser: Boolean,
    @Json(name = "user") override val user: UserDto,
) : Photo {
    @JsonClass(generateAdapter = true)
    data class UrlsDto(
        @Json(name = "full") override val full: String
    ) : Urls
}