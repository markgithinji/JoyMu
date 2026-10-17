package com.example.joymu.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InquiryDto(
    val id: String = java.util.UUID.randomUUID().toString(),
    @SerialName("property_id")
    val propertyId: String,
    @SerialName("property_title")
    val propertyTitle: String,
    @SerialName("tenant_name")
    val tenantName: String,
    @SerialName("tenant_phone")
    val tenantPhone: String,
    @SerialName("tenant_email")
    val tenantEmail: String = "",
    val message: String,
    @SerialName("is_read")
    val isRead: Boolean = false
)

data class Inquiry(
    val id: String,
    val propertyId: String,
    val propertyTitle: String,
    val tenantName: String,
    val tenantPhone: String,
    val tenantEmail: String,
    val message: String,
    val isRead: Boolean = false
)
