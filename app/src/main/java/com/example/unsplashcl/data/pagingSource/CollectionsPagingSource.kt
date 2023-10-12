package com.example.unsplashcl.data.pagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.unsplashcl.data.Repository
import com.example.unsplashcl.entity.PhotosCollection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CollectionsPagingSource @Inject constructor(
    private val repository: Repository,
): PagingSource<Int, PhotosCollection>() {
    override fun getRefreshKey(state: PagingState<Int, PhotosCollection>): Int = FIRST_PAGE_NUMBER

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotosCollection> {
        val page = params.key ?: FIRST_PAGE_NUMBER
        return kotlin.runCatching {
            withContext(Dispatchers.IO) {
                repository.getCollections(page) as List<PhotosCollection>
            }
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it,
                    prevKey = null,
                    nextKey = if (it.isEmpty()) null else page + 1
                )
            },
            onFailure = {
                LoadResult.Error(it)
            }
        )
    }

    private companion object {
        private const val FIRST_PAGE_NUMBER = 1
    }

}