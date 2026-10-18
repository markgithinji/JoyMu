package com.example.joymu.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joymu.core.data.InquiryRepository
import com.example.joymu.core.data.InquiryRepositoryApi
import com.example.joymu.core.data.PropertyRepository
import com.example.joymu.core.model.InquiryDto
import com.example.joymu.core.model.Property
import com.example.joymu.feature.profile.data.AuthRepository
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ProfileUiState {
    data class Content(
        val currentUser: UserInfo? = null,
        val myProperties: List<Property> = emptyList(),
        val inquiries: List<InquiryDto> = emptyList(),
        val isLoadingData: Boolean = true
    ) : ProfileUiState
}

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val inquiryRepository: InquiryRepositoryApi = InquiryRepository(),
    private val propertyRepository: PropertyRepository = PropertyRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState.Content(currentUser = authRepository.getCurrentUser()))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingData = true, currentUser = authRepository.getCurrentUser())

            val inqResult = inquiryRepository.fetchInquiries()
            val propResult = propertyRepository.fetchProperties()

            val inquiries = inqResult.getOrDefault(emptyList())
            val properties = propResult.getOrDefault(emptyList())

            _uiState.value = _uiState.value.copy(
                inquiries = inquiries,
                myProperties = properties,
                isLoadingData = false
            )
        }
    }

    fun updatePrice(propertyId: String, newPriceKes: Int) {
        viewModelScope.launch {
            propertyRepository.updatePropertyPrice(propertyId, newPriceKes)
            loadData()
        }
    }

    fun deleteProperty(propertyId: String) {
        viewModelScope.launch {
            propertyRepository.deleteProperty(propertyId)
            loadData()
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.value = _uiState.value.copy(currentUser = null)
        }
    }
}
