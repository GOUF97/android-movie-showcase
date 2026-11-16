# Mandiri Movie App (The Movie Database - TMDB)

A production-grade, enterprise-ready Android application architected with **Clean Architecture**, **Unidirectional Data Flow (UDF)**, and **Jetpack Compose**, built according to the Mandiri Senior Android Engineer technical specification (Option 1).

---

## 🚀 Features & User Stories Covered

| Requirement | Implementation & Highlights |
| :--- | :--- |
| **1. Official Genres** | Fetch and render official TMDB genres with in-memory mutex-safe caching to avoid duplicate requests. |
| **2. Discover Movies by Genre** | Horizontal genre chip filters with seamless reactive switching (`flatMapLatest`) and endless scrolling. |
| **3. Primary Movie Info** | Hero backdrop banner, title, release year, runtime, tagline, rating badge, genres chips, and storyline overview. |
| **4. User Reviews** | Community reviews displayed with author avatars, ratings, timestamps, and expandable ("Read more / Show less") text. |
| **5. YouTube Trailer** | Embedded `YouTubePlayerView` with lifecycle observer (auto-pause on background) and fallback intent button. |
| **6. Endless Scrolling** | **AndroidX Paging 3** (`Pager`, `PagingSource`, `cachedIn`) implemented on both Movies Grid and User Reviews. |
| **7. Positive & Negative Cases** | Comprehensive error states (offline banner, 401/429/5xx HTTP handling, retry actions, and empty states). |

---

## 🏛️ Architectural Overview

The application follows **Clean Architecture** principles strictly separated into layers:

```
┌────────────────────────────────────────────────────────────────────────┐
│                        PRESENTATION (COMPOSE)                          │
│   DiscoverScreen (Genres + Movies Grid)  │  MovieDetailScreen (Trailer)│
│                        ViewModel (UDF / StateFlow)                     │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│                             DOMAIN LAYER                               │
│   GetGenresUseCase │ GetMoviesPagedUseCase │ GetReviewsPagedUseCase   │
│                 Repository Interfaces (Pure Kotlin)                    │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│                              DATA LAYER                                │
│   RepositoryImpl │ Paging 3 Sources │ DTO Mappers │ Retrofit ApiService│
└────────────────────────────────────────────────────────────────────────┘
```

### Key Architectural Decisions:
1. **Unidirectional Data Flow (UDF)**: ViewModels expose immutable `StateFlow<UiState>` collected with `collectAsStateWithLifecycle()` to prevent background lifecycle leaks.
2. **Cancellation & Race Conditions**: Genre selection switches dynamically using `flatMapLatest`, automatically canceling in-flight paging requests when users rapidly tap different genres.
3. **Paging 3 Architecture**: PagingSources (`DiscoverMoviesPagingSource`, `MovieReviewsPagingSource`) cleanly separate remote data streams from UI consumption.
4. **Resilience Monad**: A sealed `NetworkResult<T>` captures HTTP exceptions (401, 404, 429, 5xx) and IOExceptions into human-readable, actionable error messages.
5. **Credential Security**: TMDB API Keys and Read Access Tokens are injected via `local.properties` into `BuildConfig`—never committed to version control.

---

## 🛠️ Tech Stack & Libraries

- **Language**: Kotlin 2.0+ (Coroutines, StateFlow, Flows)
- **UI Framework**: 100% Jetpack Compose with Material Design 3
- **Dependency Injection**: Dagger Hilt
- **Asynchronous & Pagination**: Kotlin Coroutines, Flow, AndroidX Paging 3
- **Networking & Serialization**: Retrofit 2, OkHttp 3, Kotlinx Serialization
- **Media & Images**:
  - [Android-YouTube-Player](https://github.com/PierfrancescoSoffritti/android-youtube-player)
  - [Coil](https://coil-kt.github.io/coil/) (Asynchronous image loading with shimmer placeholders)
- **Testing**: JUnit 4, MockK, Turbine, Kotlinx Coroutines Test

---

## ⚙️ Setup & Configuration

1. **Clone the repository**:
   ```bash
   git clone <repository_url>
   cd "project mandiri FE android"
   ```

2. **Configure TMDB Credentials**:
   Create a `local.properties` file in the root directory (or update your existing one) with your TMDB credentials:
   ```properties
   TMDB_API_KEY=your_tmdb_api_key_here
   TMDB_ACCESS_TOKEN=your_tmdb_read_access_token_here
   ```
   *Note: If `TMDB_ACCESS_TOKEN` is provided, the app uses Bearer authentication (`Authorization: Bearer <TOKEN>`); otherwise, it automatically falls back to `api_key` query parameter.*

3. **Build and Run**:
   Open the project in Android Studio (Ladybug / Koala or newer) and run the `app` configuration.

4. **Run Unit Tests**:
   Execute unit tests via Gradle:
   ```bash
   ./gradlew testDebugUnitTest
   ```

---

## 🧪 Unit Testing Coverage

Unit tests are written with **MockK** and **Turbine** to test asynchronous state flows and edge cases:
- `DiscoverViewModelTest`:
  - `init should load genres successfully (Positive Case)`
  - `init should update state with error when fetching genres fails (Negative Case)`
  - `onIntent SelectGenre should update selectedGenreId`
  - `onIntent RetryGenres should re-invoke getMovieGenresUseCase`
- `MovieDetailViewModelTest`:
  - `init should fetch movie detail and trailers concurrently (Positive Case)`
  - `init should show error state when getMovieDetail fails (Negative Case)`
  - `init should handle empty trailers gracefully (Negative Edge Case)`
  - `onIntent SelectTrailer should update selectedTrailer in state`
- `MovieMappersTest`:
  - Transformation verification, null safety, avatar URL extraction, and runtime calculations.
