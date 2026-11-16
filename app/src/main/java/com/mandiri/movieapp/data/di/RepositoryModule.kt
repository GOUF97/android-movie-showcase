package com.mandiri.movieapp.data.di

import com.mandiri.movieapp.data.repository.GenreRepositoryImpl
import com.mandiri.movieapp.data.repository.MovieRepositoryImpl
import com.mandiri.movieapp.domain.repository.GenreRepository
import com.mandiri.movieapp.domain.repository.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGenreRepository(
        genreRepositoryImpl: GenreRepositoryImpl
    ): GenreRepository

    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository
}
