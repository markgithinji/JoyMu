package com.example.joymu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joymu.data.supabase.PropertyRepository
import com.example.joymu.model.FilterState
import com.example.joymu.model.Property
import com.example.joymu.model.PropertyType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ExploreUiState {
    data object Loading : ExploreUiState
    data class Success(
        val properties: List<Property>,
        val searchQuery: String = "",
        val selectedCategory: PropertyType = PropertyType.ALL,
        val filterState: FilterState = FilterState()
    ) : ExploreUiState
    data class Error(val message: String) : ExploreUiState
}

sealed interface ExploreUiIntent {
    data class Search(val query: String) : ExploreUiIntent
    data class SelectCategory(val category: PropertyType) : ExploreUiIntent
    data class ApplyProximity(val lat: Double?, val lng: Double?, val radiusKm: Double) : ExploreUiIntent
    data class ApplyFilter(val filterState: FilterState) : ExploreUiIntent
    data class ToggleFavorite(val propertyId: String) : ExploreUiIntent
    data object Refresh : ExploreUiIntent
}

sealed interface ExploreUiEffect {
    data class NavigateToDetail(val propertyId: String) : ExploreUiEffect
    data class ShowToast(val message: String) : ExploreUiEffect
}

class ExploreViewModel(
    private val repository: PropertyRepository = PropertyRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExploreUiState>(ExploreUiState.Loading)
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private val _effect = Channel<ExploreUiEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadProperties()
    }

    fun onIntent(intent: ExploreUiIntent) {
        when (intent) {
            is ExploreUiIntent.Search -> updateSearchQuery(intent.query)
            is ExploreUiIntent.SelectCategory -> updateCategory(intent.category)
            is ExploreUiIntent.ApplyProximity -> fetchProximity(intent.lat, intent.lng, intent.radiusKm)
            is ExploreUiIntent.ApplyFilter -> updateFilter(intent.filterState)
            is ExploreUiIntent.ToggleFavorite -> toggleFavorite(intent.propertyId)
            ExploreUiIntent.Refresh -> loadProperties()
        }
    }

    private fun loadProperties() {
        viewModelScope.launch {
            _uiState.value = ExploreUiState.Loading
            val result = repository.fetchProperties()
            result.onSuccess { list ->
                _uiState.value = ExploreUiState.Success(properties = list)
            }.onFailure { e ->
                _uiState.value = ExploreUiState.Error(e.localizedMessage ?: "Failed to load properties")
            }
        }
    }

    private fun updateSearchQuery(query: String) {
        val currentState = _uiState.value
        if (currentState is ExploreUiState.Success) {
            _uiState.value = currentState.copy(searchQuery = query)
        }
    }

    private fun updateCategory(category: PropertyType) {
        val currentState = _uiState.value
        if (currentState is ExploreUiState.Success) {
            _uiState.value = currentState.copy(selectedCategory = category)
        }
    }

    private fun updateFilter(filterState: FilterState) {
        val currentState = _uiState.value
        if (currentState is ExploreUiState.Success) {
            _uiState.value = currentState.copy(filterState = filterState)
        }
    }

    private fun fetchProximity(lat: Double?, lng: Double?, radiusKm: Double) {
        viewModelScope.launch {
            _uiState.value = ExploreUiState.Loading
            val result = if (lat != null && lng != null) {
                repository.fetchNearbyProperties(lat, lng, radiusKm)
            } else {
                repository.fetchProperties()
            }
            result.onSuccess { list ->
                _uiState.value = ExploreUiState.Success(properties = list)
            }.onFailure { e ->
                _uiState.value = ExploreUiState.Error(e.localizedMessage ?: "Failed to fetch nearby properties")
            }
        }
    }

    private fun toggleFavorite(propertyId: String) {
        val currentState = _uiState.value
        if (currentState is ExploreUiState.Success) {
            val updatedList = currentState.properties.map { p ->
                if (p.id == propertyId) p.copy(isSaved = !p.isSaved) else p
            }
            _uiState.value = currentState.copy(properties = updatedList)
        }
    }
}
