package com.mandiri.movieapp.presentation.detail

import com.mandiri.movieapp.domain.model.MovieDetail
import com.mandiri.movieapp.domain.model.VideoTrailer

data class MovieDetailUiState(
    val isLoading: Boolean = true,
    val movieDetail: MovieDetail? = null,
    val trailers: List<VideoTrailer> = emptyList(),
    val selectedTrailer: VideoTrailer? = null,
    val errorMessage: String? = null
)

sealed interface MovieDetailUiIntent {
    data class SelectTrailer(val trailer: VideoTrailer) : MovieDetailUiIntent
    data object Retry : MovieDetailUiIntent
}
