package com.example.joymu.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joymu.data.DummyData
import com.example.joymu.data.supabase.PropertyRepository
import com.example.joymu.model.Property
import com.example.joymu.model.PropertyType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface PostPropertyUiState {
    data class Content(
        val isUploading: Boolean = false,
        val uploadStatusText: String = "",
        val isSubmitted: Boolean = false,
        val errorMessage: String? = null
    ) : PostPropertyUiState
}

class PostPropertyViewModel(
    private val repository: PropertyRepository = PropertyRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostPropertyUiState.Content())
    val uiState: StateFlow<PostPropertyUiState> = _uiState.asStateFlow()

    fun publishProperty(
        context: Context,
        title: String,
        description: String,
        location: String,
        priceKes: Int,
        selectedType: PropertyType,
        bedrooms: Int,
        bathrooms: Int,
        selectedAmenities: List<String>,
        selectedImageUris: List<Uri>
    ) {
        viewModelScope.launch {
            _uiState.value = PostPropertyUiState.Content(
                isUploading = true,
                uploadStatusText = "Uploading photos to Supabase Storage..."
            )

            val uploadedUrls = mutableListOf<String>()
            for (uri in selectedImageUris) {
                val uploadResult = repository.uploadPhoto(context, uri)
                uploadResult.onSuccess { url -> uploadedUrls.add(url) }
            }

            if (uploadedUrls.isEmpty()) {
                uploadedUrls.add("https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800")
            }

            _uiState.value = PostPropertyUiState.Content(
                isUploading = true,
                uploadStatusText = "Publishing listing to database..."
            )

            val newProperty = Property(
                id = "prop_${UUID.randomUUID().toString().take(8)}",
                title = title.ifBlank { "Modern Apartment" },
                description = description.ifBlank { "Beautiful property in $location" },
                propertyType = selectedType,
                priceKes = priceKes,
                pricePeriod = if (selectedType == PropertyType.AIRBNB) "/night" else "/month",
                location = location,
                neighborhood = location.split(",").firstOrNull() ?: "Nairobi",
                bedrooms = bedrooms,
                bathrooms = bathrooms,
                isVerified = true,
                images = uploadedUrls,
                agent = DummyData.sampleAgents.first(),
                amenities = selectedAmenities
            )

            val result = repository.createProperty(newProperty)
            result.onSuccess {
                _uiState.value = PostPropertyUiState.Content(isSubmitted = true)
            }.onFailure { e ->
                _uiState.value = PostPropertyUiState.Content(isSubmitted = true, errorMessage = e.localizedMessage)
            }
        }
    }
}
