package com.example.unsplashcl.data

import androidx.room.*
import com.example.unsplashcl.entity.CollectionInRepo
import com.example.unsplashcl.entity.NewCollectionInRepo
import com.example.unsplashcl.entity.NewPhotoInRepo
import com.example.unsplashcl.entity.PhotoInRepo

@Dao
interface PhotoDao {
    @Transaction
    @Query("SELECT * FROM photos ORDER BY db_id DESC")
    fun getAll(): List<PhotoInRepo>

    @Transaction
    @Query("SELECT * FROM collections ORDER BY db_id DESC")
    fun getAllCollections(): List<CollectionInRepo>

    @Transaction
    @Query("DELETE FROM photos")
    suspend fun deleteAllPhotos()

    @Transaction
    @Query("DELETE FROM collections")
    suspend fun deleteAllCollections()

    @Insert(entity = PhotoInRepo::class)
    suspend fun insert(photo: NewPhotoInRepo)

    @Insert(entity = CollectionInRepo::class)
    suspend fun insertCollection(collection: NewCollectionInRepo)

    @Delete
    suspend fun delete(photo: PhotoInRepo)

    @Update
    suspend fun update(photo: PhotoInRepo)
}