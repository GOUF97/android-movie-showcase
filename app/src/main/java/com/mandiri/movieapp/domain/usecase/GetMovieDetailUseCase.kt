package com.mandiri.movieapp.domain.usecase

import com.mandiri.movieapp.core.network.NetworkResult
import com.mandiri.movieapp.domain.model.MovieDetail
import com.mandiri.movieapp.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): NetworkResult<MovieDetail> {
        return movieRepository.getMovieDetail(movieId)
    }
}
