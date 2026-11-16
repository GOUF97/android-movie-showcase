package com.mandiri.movieapp.presentation.discover

import com.mandiri.movieapp.domain.model.Genre

data class DiscoverUiState(
    val genres: List<Genre> = emptyList(),
    val selectedGenreId: Int? = null,
    val isLoadingGenres: Boolean = false,
    val genreErrorMessage: String? = null
)

sealed interface DiscoverUiIntent {
    data class SelectGenre(val genreId: Int?) : DiscoverUiIntent
    data object RetryGenres : DiscoverUiIntent
}
