package com.mandiri.movieapp.domain.repository

import androidx.paging.PagingData
import com.mandiri.movieapp.core.network.NetworkResult
import com.mandiri.movieapp.domain.model.Movie
import com.mandiri.movieapp.domain.model.MovieDetail
import com.mandiri.movieapp.domain.model.Review
import com.mandiri.movieapp.domain.model.VideoTrailer
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getDiscoverMoviesPaged(genreId: Int?): Flow<PagingData<Movie>>
    suspend fun getMovieDetail(movieId: Int): NetworkResult<MovieDetail>
    suspend fun getMovieTrailers(movieId: Int): NetworkResult<List<VideoTrailer>>
    fun getMovieReviewsPaged(movieId: Int): Flow<PagingData<Review>>
}
