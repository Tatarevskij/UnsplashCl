package com.example.unsplashcl.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class CollectionInRepo(
    @PrimaryKey
    @ColumnInfo(name = "db_id")
    val dbId: Int,
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "total_photos")
    val totalPhotos: Int,
    @ColumnInfo(name = "img_url")
    val imgUrl: String,
    @ColumnInfo(name = "icon_url")
    val iconUrl: String,
    @ColumnInfo(name = "user_name")
    val userName: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "cover_id")
    val coverId: String
)
