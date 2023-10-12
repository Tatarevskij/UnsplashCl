package com.example.unsplashcl.data.pagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.unsplashcl.data.Repository
import com.example.unsplashcl.entity.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PhotosPagingSource @Inject constructor(
    private val repository: Repository,
    private val query: String?
) : PagingSource<Int, Photo>() {
    override fun getRefreshKey(state: PagingState<Int, Photo>): Int = FIRST_PAGE_NUMBER

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val page = params.key ?: FIRST_PAGE_NUMBER
        return kotlin.runCatching {
            withContext(Dispatchers.IO) {
                repository.getPhotos(page, query) as List<Photo>
            }}.fold(
                onSuccess = {
                    LoadResult.Page(
                        data = it,
                        prevKey = null,
                        nextKey = if (it.isEmpty()) null else page + 1
                    )
                },
                onFailure = {
                    throw it
                    LoadResult.Error(it)
                }
            )
        }

    private companion object {
        private const val FIRST_PAGE_NUMBER = 1
    }
}
