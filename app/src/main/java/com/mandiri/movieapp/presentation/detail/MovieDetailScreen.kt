package com.mandiri.movieapp.presentation.detail

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.mandiri.movieapp.domain.model.MovieDetail
import com.mandiri.movieapp.domain.model.Review
import com.mandiri.movieapp.domain.model.VideoTrailer
import com.mandiri.movieapp.presentation.component.EmptyStateView
import com.mandiri.movieapp.presentation.component.ErrorStateView
import com.mandiri.movieapp.presentation.component.LoadingStateView
import com.mandiri.movieapp.presentation.component.MoviePosterImage
import com.mandiri.movieapp.presentation.component.RatingBadge
import com.mandiri.movieapp.presentation.component.YouTubePlayerComposable
import com.mandiri.movieapp.presentation.theme.MandiriDarkBg
import com.mandiri.movieapp.presentation.theme.MandiriGold
import com.mandiri.movieapp.presentation.theme.MandiriSurface
import com.mandiri.movieapp.presentation.theme.MandiriSurfaceVariant
import com.mandiri.movieapp.presentation.theme.TextPrimary
import com.mandiri.movieapp.presentation.theme.TextSecondary
import com.mandiri.movieapp.presentation.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val reviewPagingItems = viewModel.reviewsPagedFlow.collectAsLazyPagingItems()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.movieDetail?.title ?: "Movie Detail",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MandiriDarkBg
                )
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                LoadingStateView(modifier = Modifier.padding(innerPadding))
            }
            uiState.errorMessage != null -> {
                ErrorStateView(
                    message = uiState.errorMessage ?: "Failed to load movie details.",
                    onRetry = { viewModel.onIntent(MovieDetailUiIntent.Retry) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            uiState.movieDetail != null -> {
                val detail = uiState.movieDetail!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    // 1. Hero Backdrop & Poster Header
                    item {
                        MovieHeroSection(detail = detail)
                    }

                    // 2. Movie Overview & Storyline
                    item {
                        MovieOverviewSection(detail = detail)
                    }

                    // 3. YouTube Trailer Section
                    item {
                        MovieTrailerSection(
                            trailers = uiState.trailers,
                            selectedTrailer = uiState.selectedTrailer,
                            onSelectTrailer = { trailer ->
                                viewModel.onIntent(MovieDetailUiIntent.SelectTrailer(trailer))
                            }
                        )
                    }

                    // 4. User Reviews Section Header
                    item {
                        ReviewsHeaderSection()
                    }

                    // 5. Endless Scrolling Reviews List
                    if (reviewPagingItems.itemCount == 0 &&
                        reviewPagingItems.loadState.refresh is LoadState.NotLoading
                    ) {
                        item {
                            EmptyStateView(
                                title = "No Reviews Yet",
                                message = "There are no written reviews submitted for this movie.",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        items(
                            count = reviewPagingItems.itemCount,
                            key = { index ->
                                val review = reviewPagingItems[index]
                                review?.id ?: index
                            }
                        ) { index ->
                            val review = reviewPagingItems[index]
                            if (review != null) {
                                ReviewItemCard(
                                    review = review,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }
                        }

                        // Append Load State for Reviews Pagination
                        when (val append = reviewPagingItems.loadState.append) {
                            is LoadState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = MandiriGold,
                                            strokeWidth = 2.dp,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                            is LoadState.Error -> {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Failed to load more reviews",
                                            color = TextSecondary,
                                            fontSize = 12.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            onClick = { reviewPagingItems.retry() },
                                            colors = ButtonDefaults.buttonColors(containerColor = MandiriGold),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = Color.Black
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = "Retry", color = Color.Black, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MovieHeroSection(detail: MovieDetail) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        // Backdrop Image with Gradient Scrim
        MoviePosterImage(
            imageUrl = detail.backdropUrl ?: detail.posterUrl,
            contentDescription = detail.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MandiriDarkBg.copy(alpha = 0.6f),
                            MandiriDarkBg
                        )
                    )
                )
        )

        // Poster and Title Row at bottom of hero
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .width(100.dp)
                    .aspectRatio(2f / 3f)
            ) {
                MoviePosterImage(
                    imageUrl = detail.posterUrl,
                    contentDescription = detail.title,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = detail.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                if (detail.tagline.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "\"${detail.tagline}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MandiriGold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RatingBadge(voteAverage = detail.voteAverage)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = detail.releaseDate.take(4).ifEmpty { "N/A" },
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = detail.formattedRuntime,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }

    // Genre Chips
    if (detail.genres.isNotEmpty()) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            detail.genres.forEach { genre ->
                SuggestionChip(
                    onClick = {},
                    label = { Text(text = genre.name, fontSize = 11.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MandiriSurfaceVariant,
                        labelColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(6.dp)
                )
            }
        }
    }
}

@Composable
private fun MovieOverviewSection(detail: MovieDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Storyline",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = detail.overview.ifBlank { "No overview available for this movie." },
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun MovieTrailerSection(
    trailers: List<VideoTrailer>,
    selectedTrailer: VideoTrailer?,
    onSelectTrailer: (VideoTrailer) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Trailers & Videos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedTrailer != null) {
            // Embedded YouTube Player
            YouTubePlayerComposable(
                youtubeKey = selectedTrailer.youtubeKey,
                title = selectedTrailer.title
            )

            // Multiple trailers selector chips
            if (trailers.size > 1) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(trailers, key = { it.id }) { trailer ->
                        FilterChip(
                            selected = trailer.id == selectedTrailer.id,
                            onClick = { onSelectTrailer(trailer) },
                            label = {
                                Text(
                                    text = trailer.title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MandiriGold,
                                selectedLabelColor = Color.Black,
                                containerColor = MandiriSurfaceVariant,
                                labelColor = TextSecondary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = MandiriSurfaceVariant),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No official video trailer available for this movie.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ReviewsHeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "User Reviews",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Endless scrolling community opinions",
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary
        )
    }
}

@Composable
private fun ReviewItemCard(
    review: Review,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MandiriSurface),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Author info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (review.authorAvatarUrl != null) {
                    MoviePosterImage(
                        imageUrl = review.authorAvatarUrl,
                        contentDescription = review.author,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MandiriSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.author,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    if (review.createdAt.isNotBlank()) {
                        Text(
                            text = review.createdAt.take(10),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                    }
                }

                if (review.rating != null) {
                    RatingBadge(voteAverage = review.rating)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Review Content (Expandable)
            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 20.sp,
                maxLines = if (isExpanded) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis
            )

            if (review.content.length > 200) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isExpanded) "Show Less" else "Read More",
                    color = MandiriGold,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { isExpanded = !isExpanded }
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}
