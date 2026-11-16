package com.mandiri.movieapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mandiri.movieapp.core.network.NetworkResult
import com.mandiri.movieapp.core.network.safeApiCall
import com.mandiri.movieapp.core.network.TmdbApiService
import com.mandiri.movieapp.core.util.IoDispatcher
import com.mandiri.movieapp.data.mapper.toDomain
import com.mandiri.movieapp.data.paging.DiscoverMoviesPagingSource
import com.mandiri.movieapp.data.paging.MovieReviewsPagingSource
import com.mandiri.movieapp.domain.model.Movie
import com.mandiri.movieapp.domain.model.MovieDetail
import com.mandiri.movieapp.domain.model.Review
import com.mandiri.movieapp.domain.model.VideoTrailer
import com.mandiri.movieapp.domain.repository.MovieRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val apiService: TmdbApiService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MovieRepository {

    override fun getDiscoverMoviesPaged(genreId: Int?): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 4
            ),
            pagingSourceFactory = { DiscoverMoviesPagingSource(apiService, genreId) }
        ).flow
    }

    override suspend fun getMovieDetail(movieId: Int): NetworkResult<MovieDetail> =
        withContext(ioDispatcher) {
            safeApiCall {
                apiService.getMovieDetail(movieId).toDomain()
            }
        }

    override suspend fun getMovieTrailers(movieId: Int): NetworkResult<List<VideoTrailer>> =
        withContext(ioDispatcher) {
            safeApiCall {
                apiService.getMovieVideos(movieId).results.map { it.toDomain() }
            }
        }

    override fun getMovieReviewsPaged(movieId: Int): Flow<PagingData<Review>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 2
            ),
            pagingSourceFactory = { MovieReviewsPagingSource(apiService, movieId) }
        ).flow
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
