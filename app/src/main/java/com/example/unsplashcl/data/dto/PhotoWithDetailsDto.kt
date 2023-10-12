package com.example.unsplashcl.data.dto

import com.example.unsplashcl.entity.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import javax.inject.Inject

@JsonClass(generateAdapter = true)
class PhotoWithDetailsDto @Inject constructor(
    @Json(name = "id") override val id: String,
    @Json(name = "urls") override val urls: UrlsDetailsDto,
    @Json(name = "links") override val links: PhotoLinksDto,
    @Json(name = "likes") override val likes: Int,
    @Json(name = "liked_by_user") override var likedByUser: Boolean,
    @Json(name = "user") override val user: UserDetailsDto,
    @Json(name = "downloads") override val downloads: Int,
    @Json(name = "exif") override val exif: ExifDto? = null,
    @Json(name = "location") override val location: LocationDto?,
    @Json(name = "tags") override val tags: List<TagDto>,
) : PhotoWithDetails {
    @JsonClass(generateAdapter = true)
    data class UrlsDetailsDto(
        @Json(name = "raw") override val raw: String,
        @Json(name = "full") override val full: String,
        @Json(name = "small") override val small: String,
    ) : UrlsDetails

    @JsonClass(generateAdapter = true)
    data class PhotoLinksDto(
        @Json(name = "download_location") override val downloadLocation: String
    ): PhotoLinks

    @JsonClass(generateAdapter = true)
    data class UserDetailsDto(
        @Json(name = "id") override val id: String,
        @Json(name = "username") override val name: String,
        @Json(name = "profile_image") override val profileImage: UserDto.ProfileImageDto,
        @Json(name = "location") override val location: String?,
        @Json(name = "links") override val links: UserDto.LinksDto?
    ) : UserDetails

    @JsonClass(generateAdapter = true)
    data class ExifDto(
        @Json(name = "make") override val make: String? = null,
        @Json(name = "model") override val model: String? = null,
        @Json(name = "exposure_time") override val exposureTime: String? = null,
        @Json(name = "aperture") override val aperture: String? = null,
        @Json(name = "focal_length") override val focalLength: String? = null,
        @Json(name = "iso") override val iso: Int?
    ) : Exif

    @JsonClass(generateAdapter = true)
    data class LocationDto(
        @Json(name = "city") override val city: String?,
        @Json(name = "country") override val country: String?,
        @Json(name = "position") override val position: PositionDto?
    ) : Location {
        @JsonClass(generateAdapter = true)
        data class PositionDto(
            @Json(name = "latitude") override val latitude: Double?,
            @Json(name = "longitude") override val longitude: Double?
        ) : Position
    }

    @JsonClass(generateAdapter = true)
    data class TagDto(
        @Json(name = "title") override val title: String
    ) : Tag
}