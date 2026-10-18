package com.example.joymu.core.data

import com.example.joymu.core.model.InquiryDto

interface InquiryRepositoryApi {
    suspend fun sendInquiry(inquiry: InquiryDto): Result<Boolean>
    suspend fun fetchInquiries(): Result<List<InquiryDto>>
}
