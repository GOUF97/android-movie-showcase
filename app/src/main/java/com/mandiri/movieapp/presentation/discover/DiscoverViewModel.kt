package com.mandiri.movieapp.presentation.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mandiri.movieapp.core.network.NetworkResult
import com.mandiri.movieapp.domain.model.Movie
import com.mandiri.movieapp.domain.usecase.GetDiscoverMoviesPagedUseCase
import com.mandiri.movieapp.domain.usecase.GetMovieGenresUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val getMovieGenresUseCase: GetMovieGenresUseCase,
    private val getDiscoverMoviesPagedUseCase: GetDiscoverMoviesPagedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    private val _selectedGenreId = MutableStateFlow<Int?>(null)

    val moviesPagedFlow: Flow<PagingData<Movie>> = _selectedGenreId
        .flatMapLatest { genreId ->
            getDiscoverMoviesPagedUseCase(genreId)
        }
        .cachedIn(viewModelScope)

    init {
        loadGenres()
    }

    fun onIntent(intent: DiscoverUiIntent) {
        when (intent) {
            is DiscoverUiIntent.SelectGenre -> {
                if (_uiState.value.selectedGenreId != intent.genreId) {
                    _uiState.update { it.copy(selectedGenreId = intent.genreId) }
                    _selectedGenreId.value = intent.genreId
                }
            }
            is DiscoverUiIntent.RetryGenres -> {
                loadGenres()
            }
        }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingGenres = true, genreErrorMessage = null) }
            when (val result = getMovieGenresUseCase()) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            genres = result.data,
                            isLoadingGenres = false,
                            genreErrorMessage = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoadingGenres = false,
                            genreErrorMessage = result.message
                        )
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoadingGenres = true) }
                }
            }
        }
    }
}
