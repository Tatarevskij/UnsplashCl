package com.example.unsplashcl

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.unsplashcl.authorization.AppAuth
import com.example.unsplashcl.data.*
import com.example.unsplashcl.entity.State
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

const val SHARED_PREFS_NAME = "saved_token"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton // Tell Dagger-Hilt to create a singleton accessible everywhere in ApplicationComponent (i.e. everywhere in the application)
    @Provides
    fun providePhotoDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        "photo_db"
    ).build() // The reason we can construct a database for the repo

    @Singleton
    @Provides
    fun providePhotoDao(db: AppDatabase) =
        db.getPhotoDao() // The reason we can implement a Dao for the database

    @Singleton
    @Provides
    fun sharedPrefsProvider(
        @ApplicationContext app: Context
    ): SharedPreferences = app.getSharedPreferences(
        SHARED_PREFS_NAME,
        Context.MODE_PRIVATE
    )

    @Singleton
    @Provides
    fun appAuthProvider(
    ): AppAuth = AppAuth

    @Singleton
    @Provides
    fun appStateProvider(
        @ApplicationContext app: Context
    ): State = State(app)

    @Singleton
    @Provides
    fun unsplashApiProvider(tokenPrefs: SharedPreferences
    ):  UnsplashApi = UnsplashApi(tokenPrefs)

    @Singleton
    @Provides
    fun repositoryProvider(db: AppDatabase, api: UnsplashApi, state: State
    ): Repository = Repository(api, UnsplashDb(db.getPhotoDao()), state)

}