package com.example.unsplashcl.data

import android.content.SharedPreferences
import com.example.unsplashcl.ACCESS_TOKEN
import com.example.unsplashcl.data.dto.*
import com.example.unsplashcl.entity.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

private const val BASE_URL = "https://api.unsplash.com/"

class UnsplashApi @Inject constructor(
    private val tokenPrefs: SharedPreferences,
){
    init {
        setToken()
    }

    private var apiToken: String? = null
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request().newBuilder().addHeader("Authorization", "Bearer $apiToken").build()
            chain.proceed(request)}
            .build())
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val searchPhotosApi: SearchPhotosApi = retrofit.create(
        SearchPhotosApi::class.java
    )

    private val searchPhotosCollectionsApi: SearchPhotosCollectionsApi = retrofit.create(
        SearchPhotosCollectionsApi::class.java
    )

    private val searchUserApi: SearchUserApi = retrofit.create(
        SearchUserApi::class.java
    )

    suspend fun getPhotos(page: Int): List<Photo> {
        return this.searchPhotosApi.getPhotosFromDto(page)
    }

    suspend fun getPhotoById(id: String): PhotoWithDetails {
        return this.searchPhotosApi.getPhotoByIdFromDto(id)
    }

    suspend fun getPhotosByQuery(query: String, page: Int): List<Photo> {
        return this.searchPhotosApi.getPhotosByQueryFromDto(page, query).photosList
    }

    suspend fun likePhoto(id: String){
        this.searchPhotosApi.likePhoto(id)
    }

    suspend fun unlikePhoto(id: String){
        this.searchPhotosApi.unlikePhoto(id)
    }

    suspend fun trackPhotoDownload(id: String, ixid: String) {
        this.searchPhotosApi.trackPhotoDownload(id, ixid)
    }

    suspend fun getPhotosCollections(page: Int): List<PhotosCollection> {
        return this.searchPhotosCollectionsApi.getPhotosCollectionsFromDto(page)
    }

    suspend fun getPhotosByCollectionId(page: Int, id: String): List<Photo> {
        return this.searchPhotosCollectionsApi.getPhotosByCollectionIdFromDto(id, page)
    }

    suspend fun getMyInfo(): User {
        return this.searchUserApi.getMyInfoFromDto()
    }

    suspend fun getLikedPhotos(page: Int, name: String): List<Photo> {
        return this.searchUserApi.getLikedPhotosFromDto(name, page)
    }

    fun setToken() {
        apiToken = tokenPrefs.getString(ACCESS_TOKEN, null)
    }
}

interface SearchPhotosApi {
    @GET("photos?")
    suspend fun getPhotosFromDto(
        @Query("page") page: Int
    ): List<PhotoDto>

    @GET("photos/{id}")
    suspend fun getPhotoByIdFromDto(
        @Path("id") id: String,
    ): PhotoWithDetailsDto

    @GET("/search/photos?")
    suspend fun getPhotosByQueryFromDto(
        @Query("page") page: Int,
        @Query("query") query: String
    ): PhotosListDto

    @POST("/photos/{id}/like")
    suspend fun likePhoto(
        @Path("id") id: String,
    )

    @DELETE("/photos/{id}/like")
    suspend fun unlikePhoto(
        @Path("id") id: String,
    )

    @GET("/photos/{id}/download?")
    suspend fun trackPhotoDownload(
        @Path("id") id: String,
        @Query("ixid") ixid: String
    )
}

interface SearchPhotosCollectionsApi {
    @GET("collections?")
    suspend fun getPhotosCollectionsFromDto(
        @Query("page") page: Int
    ): List<PhotosCollectionDto>

    @GET("collections/{id}/photos?")
    suspend fun getPhotosByCollectionIdFromDto(
        @Path("id") id: String,
        @Query("page") page: Int
    ): List<PhotoDto>
}

interface SearchUserApi {
    @GET("/me")
    suspend fun getMyInfoFromDto(): UserDto

    @GET("/users/{name}/likes?")
    suspend fun getLikedPhotosFromDto(
        @Path("name") name: String,
        @Query("page") page: Int
    ): List<PhotoDto>
}

