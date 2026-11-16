package com.mandiri.movieapp.core.network

import com.mandiri.movieapp.data.model.DiscoverMovieResponseDto
import com.mandiri.movieapp.data.model.GenreListResponseDto
import com.mandiri.movieapp.data.model.MovieDetailDto
import com.mandiri.movieapp.data.model.MovieReviewsResponseDto
import com.mandiri.movieapp.data.model.MovieVideosResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    @GET("genre/movie/list")
    suspend fun getMovieGenres(): GenreListResponseDto

    @GET("discover/movie")
    suspend fun getDiscoverMovies(
        @Query("with_genres") genreId: Int? = null,
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("include_adult") includeAdult: Boolean = false
    ): DiscoverMovieResponseDto

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: Int
    ): MovieDetailDto

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int
    ): MovieVideosResponseDto

    @GET("movie/{movie_id}/reviews")
    suspend fun getMovieReviews(
        @Path("movie_id") movieId: Int,
        @Query("page") page: Int = 1
    ): MovieReviewsResponseDto
}
