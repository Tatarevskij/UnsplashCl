package com.example.unsplashcl.data.dto

import com.example.unsplashcl.entity.PhotosList
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import javax.inject.Inject

@JsonClass(generateAdapter = true)
data class PhotosListDto @Inject constructor(
    @Json(name = "results") override val photosList: List<PhotoDto>
): PhotosList
