package com.example.unsplashcl.data.dto

import com.example.unsplashcl.entity.PhotosCollection
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import javax.inject.Inject

@JsonClass(generateAdapter = true)
class PhotosCollectionDto @Inject constructor(
    @Json(name = "id") override val id: String,
    @Json(name = "title") override val title: String,
    @Json(name = "total_photos") override val totalPhotos: Int,
    @Json(name = "user") override val user: UserDto,
    @Json(name = "cover_photo") override val coverPhoto: PhotoDto
): PhotosCollection