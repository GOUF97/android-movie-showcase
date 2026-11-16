package com.mandiri.movieapp.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mandiri.movieapp.core.network.NetworkResult
import com.mandiri.movieapp.domain.model.Review
import com.mandiri.movieapp.domain.model.VideoTrailer
import com.mandiri.movieapp.domain.usecase.GetMovieDetailUseCase
import com.mandiri.movieapp.domain.usecase.GetMovieReviewsPagedUseCase
import com.mandiri.movieapp.domain.usecase.GetMovieTrailersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val getMovieTrailersUseCase: GetMovieTrailersUseCase,
    private val getMovieReviewsPagedUseCase: GetMovieReviewsPagedUseCase
) : ViewModel() {

    val movieId: Int = checkNotNull(savedStateHandle["movieId"])

    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    val reviewsPagedFlow: Flow<PagingData<Review>> =
        getMovieReviewsPagedUseCase(movieId).cachedIn(viewModelScope)

    init {
        loadMovieData()
    }

    fun onIntent(intent: MovieDetailUiIntent) {
        when (intent) {
            is MovieDetailUiIntent.SelectTrailer -> {
                _uiState.update { it.copy(selectedTrailer = intent.trailer) }
            }
            is MovieDetailUiIntent.Retry -> {
                loadMovieData()
            }
        }
    }

    private fun loadMovieData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Concurrently load movie details and trailers using structured concurrency
            val detailDeferred = async { getMovieDetailUseCase(movieId) }
            val trailersDeferred = async { getMovieTrailersUseCase(movieId) }

            val detailResult = detailDeferred.await()
            val trailersResult = trailersDeferred.await()

            when (detailResult) {
                is NetworkResult.Success -> {
                    val trailers = if (trailersResult is NetworkResult.Success) {
                        trailersResult.data
                    } else {
                        emptyList()
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            movieDetail = detailResult.data,
                            trailers = trailers,
                            selectedTrailer = trailers.firstOrNull(),
                            errorMessage = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = detailResult.message
                        )
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}
