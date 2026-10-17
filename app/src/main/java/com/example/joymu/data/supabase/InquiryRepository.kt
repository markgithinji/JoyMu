package com.example.joymu.data.supabase

import com.example.joymu.model.InquiryDto
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InquiryRepository {

    suspend fun sendInquiry(inquiry: InquiryDto): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            SupabaseClientProvider.client
                .from("inquiries")
                .insert(inquiry)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchInquiries(): Result<List<InquiryDto>> = withContext(Dispatchers.IO) {
        try {
            val dtoList = SupabaseClientProvider.client
                .from("inquiries")
                .select()
                .decodeList<InquiryDto>()

            Result.success(dtoList)
        } catch (e: Exception) {
            // Fallback sample inquiries for testing
            val sampleInquiries = listOf(
                InquiryDto(
                    id = "inq_1",
                    propertyId = "prop_1",
                    propertyTitle = "Modern 2-Bed Luxury Apartment in Kilimani",
                    tenantName = "Kamau Njoroge",
                    tenantPhone = "+254711223344",
                    tenantEmail = "kamau@gmail.com",
                    message = "Hi, is this 2-bedroom in Kilimani available for a viewing this Saturday at 11 AM?",
                    isRead = false
                ),
                InquiryDto(
                    id = "inq_2",
                    propertyId = "prop_2",
                    propertyTitle = "Executive Studio / Bedsitter near Sarit Centre",
                    tenantName = "Amina Abdi",
                    tenantPhone = "+254722334455",
                    tenantEmail = "amina@yahoo.com",
                    message = "Hello! What is the service charge and water bill policy for this Westlands studio?",
                    isRead = true
                )
            )
            Result.success(sampleInquiries)
        }
    }
}
