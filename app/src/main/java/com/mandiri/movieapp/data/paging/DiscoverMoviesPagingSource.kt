package com.mandiri.movieapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mandiri.movieapp.core.network.TmdbApiService
import com.mandiri.movieapp.data.mapper.toDomain
import com.mandiri.movieapp.domain.model.Movie

class DiscoverMoviesPagingSource(
    private val apiService: TmdbApiService,
    private val genreId: Int?
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val position = params.key ?: 1
        return try {
            val response = apiService.getDiscoverMovies(
                genreId = genreId,
                page = position
            )
            val movies = response.results.map { it.toDomain() }

            val nextKey = if (position >= response.totalPages || movies.isEmpty()) {
                null
            } else {
                position + 1
            }

            LoadResult.Page(
                data = movies,
                prevKey = if (position == 1) null else position - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
