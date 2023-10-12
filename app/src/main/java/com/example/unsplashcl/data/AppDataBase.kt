package com.example.unsplashcl.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.unsplashcl.entity.CollectionInRepo
import com.example.unsplashcl.entity.PhotoInRepo

@Database(entities = [PhotoInRepo::class, CollectionInRepo::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getPhotoDao(): PhotoDao
}