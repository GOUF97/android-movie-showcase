package com.mandiri.movieapp.presentation.navigation

sealed class Screen(val route: String) {
    data object Discover : Screen("discover")
    data object MovieDetail : Screen("movie_detail/{movieId}") {
        fun createRoute(movieId: Int): String = "movie_detail/$movieId"
    }
}
