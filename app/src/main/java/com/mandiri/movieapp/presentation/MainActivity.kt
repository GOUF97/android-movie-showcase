package com.mandiri.movieapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mandiri.movieapp.presentation.navigation.AppNavGraph
import com.mandiri.movieapp.presentation.theme.MandiriMovieTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MandiriMovieTheme {
                AppNavGraph()
            }
        }
    }
}
