package com.example.unsplashcl.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class NewPhotoInRepo(
    @PrimaryKey
    @ColumnInfo(name = "db_id")
    val dbId: Int? = null,
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "img_full")
    val imgFull: String,
    @ColumnInfo(name = "likes")
    val likes: Int,
    @ColumnInfo(name = "liked_by_user")
    val likedByUser: Boolean,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "user_name")
    val userName: String,
    @ColumnInfo(name = "profile_image")
    val profileImage: String,
)

