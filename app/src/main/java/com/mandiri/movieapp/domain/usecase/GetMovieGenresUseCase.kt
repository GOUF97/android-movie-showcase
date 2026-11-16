package com.mandiri.movieapp.domain.usecase

import com.mandiri.movieapp.core.network.NetworkResult
import com.mandiri.movieapp.domain.model.Genre
import com.mandiri.movieapp.domain.repository.GenreRepository
import javax.inject.Inject

class GetMovieGenresUseCase @Inject constructor(
    private val genreRepository: GenreRepository
) {
    suspend operator fun invoke(): NetworkResult<List<Genre>> {
        return genreRepository.getMovieGenres()
    }
}
