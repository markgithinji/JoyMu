package com.example.joymu.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joymu.core.data.InquiryRepository
import com.example.joymu.core.data.InquiryRepositoryApi
import com.example.joymu.core.model.InquiryDto
import com.example.joymu.core.model.Property
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface DetailUiState {
    data class Content(
        val property: Property,
        val isSendingInquiry: Boolean = false,
        val isInquirySent: Boolean = false,
        val errorMessage: String? = null
    ) : DetailUiState
}

sealed interface DetailUiIntent {
    data class SendInquiry(
        val tenantName: String,
        val tenantPhone: String,
        val message: String
    ) : DetailUiIntent
    data object ToggleFavorite : DetailUiIntent
}

sealed interface DetailUiEffect {
    data class ShowToast(val message: String) : DetailUiEffect
}

class DetailViewModel(
    initialProperty: Property,
    private val inquiryRepository: InquiryRepositoryApi = InquiryRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState.Content(property = initialProperty))
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _effect = Channel<DetailUiEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: DetailUiIntent) {
        when (intent) {
            is DetailUiIntent.SendInquiry -> submitInquiry(intent.tenantName, intent.tenantPhone, intent.message)
            DetailUiIntent.ToggleFavorite -> toggleFavorite()
        }
    }

    private fun submitInquiry(tenantName: String, tenantPhone: String, message: String) {
        val currentContent = _uiState.value
        viewModelScope.launch {
            _uiState.value = currentContent.copy(isSendingInquiry = true, errorMessage = null)

            val dto = InquiryDto(
                propertyId = currentContent.property.id,
                propertyTitle = currentContent.property.title,
                tenantName = tenantName,
                tenantPhone = tenantPhone,
                message = message
            )

            val result = inquiryRepository.sendInquiry(dto)
            result.onSuccess {
                _uiState.value = currentContent.copy(isSendingInquiry = false, isInquirySent = true)
            }.onFailure { e ->
                _uiState.value = currentContent.copy(isSendingInquiry = false, errorMessage = e.localizedMessage)
            }
        }
    }

    private fun toggleFavorite() {
        val currentContent = _uiState.value
        val updatedProperty = currentContent.property.copy(isSaved = !currentContent.property.isSaved)
        _uiState.value = currentContent.copy(property = updatedProperty)
    }
}
