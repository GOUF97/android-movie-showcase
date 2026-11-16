package com.mandiri.movieapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mandiri.movieapp.core.network.TmdbApiService
import com.mandiri.movieapp.data.mapper.toDomain
import com.mandiri.movieapp.domain.model.Review

class MovieReviewsPagingSource(
    private val apiService: TmdbApiService,
    private val movieId: Int
) : PagingSource<Int, Review>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Review> {
        val position = params.key ?: 1
        return try {
            val response = apiService.getMovieReviews(
                movieId = movieId,
                page = position
            )
            val reviews = response.results.map { it.toDomain() }

            val nextKey = if (position >= response.totalPages || reviews.isEmpty()) {
                null
            } else {
                position + 1
            }

            LoadResult.Page(
                data = reviews,
                prevKey = if (position == 1) null else position - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Review>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
