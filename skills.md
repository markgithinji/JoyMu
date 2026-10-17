# JoyMu Engineering Skills & Standards

This document establishes senior Android development standards and architectural rules for the **JoyMu** codebase.

---

## 1. MVI Architecture Standard (Model-View-Intent)
Every feature screen MUST follow strict Unidirectional Data Flow (UDF) using MVI:

- **`UiState`**: Immutable data class representing the complete UI rendering state.
- **`UiIntent`**: User actions or events triggered from the UI (`OnSearchQueryChanged`, `OnFavoriteClicked`, `OnFilterApplied`).
- **`UiSideEffect`**: One-off events consumed by the UI (`ShowSnackbar`, `NavigateToDetail`, `LaunchDialer`).
- **`ViewModel`**: Holds `StateFlow<UiState>` and exposes `Channel<UiSideEffect>`.

```kotlin
sealed interface ExploreUiState {
    data object Loading : ExploreUiState
    data class Success(val properties: List<Property>) : ExploreUiState
    data class Error(val message: String) : ExploreUiState
}

sealed interface ExploreUiIntent {
    data class Search(val query: String) : ExploreUiIntent
    data class ToggleFavorite(val propertyId: String) : ExploreUiIntent
}

sealed interface ExploreUiEffect {
    data class NavigateToDetail(val propertyId: String) : ExploreUiEffect
    data class ShowToast(val message: String) : ExploreUiEffect
}
```

---

## 2. Safe API Call Utility (`NetworkResult`)
All API calls MUST be wrapped in a type-safe `NetworkResult<T>` sealed class to guarantee exception safety:

```kotlin
sealed interface NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>
    data class Error(val code: Int? = null, val message: String, val cause: Throwable? = null) : NetworkResult<Nothing>
    data object Loading : NetworkResult<Nothing>
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(apiCall())
    } catch (e: HttpException) {
        NetworkResult.Error(code = e.code(), message = e.message(), cause = e)
    } catch (e: IOException) {
        NetworkResult.Error(message = "Network connection failed. Please check internet connection.", cause = e)
    } catch (e: Exception) {
        NetworkResult.Error(message = e.localizedMessage ?: "An unexpected error occurred.", cause = e)
    }
}
```

---

## 3. Edge-to-Edge & System Bar Insets
- `enableEdgeToEdge()` MUST be called in `onCreate()` before `setContent`.
- `AndroidManifest.xml` MUST specify `android:windowSoftInputMode="adjustResize"`.
- `Scaffold` padding values MUST be passed to `contentPadding` for `LazyColumn` / `LazyRow` to scroll cleanly behind navigation bars.
- Soft keyboard inputs MUST apply `Modifier.imePadding()` or `contentWindowInsets = WindowInsets.safeDrawing`.

---

## 4. Material 3 Design System & Dark Mode
- Dynamic colors supported on Android 12+ (SDK 31+).
- Emerald Green (`#006D44`) primary palette with custom Dark/Light `ColorScheme`.
- Status & Navigation bar icon contrast auto-configured based on darkTheme state.

---

## 5. String Localization Standard
- NO hardcoded string literals in `@Composable` functions.
- All UI text MUST be declared in `res/values/strings.xml` and referenced using `stringResource(R.string.key)`.
