package com.example.joymu.core.network

import com.example.joymu.core.model.Agent
import com.example.joymu.core.model.Property
import com.example.joymu.core.model.PropertyType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PropertyDto(
    val id: String,
    val title: String,
    val description: String,
    @SerialName("property_type")
    val propertyType: String,
    @SerialName("price_kes")
    val priceKes: Int,
    @SerialName("price_period")
    val pricePeriod: String = "/month",
    val location: String,
    val neighborhood: String,
    val bedrooms: Int,
    val bathrooms: Int,
    @SerialName("square_meters")
    val squareMeters: Int? = null,
    @SerialName("is_verified")
    val isVerified: Boolean = true,
    @SerialName("is_featured")
    val isFeatured: Boolean = false,
    val images: List<String> = emptyList(),
    val amenities: List<String> = emptyList(),
    @SerialName("agent_id")
    val agentId: String? = null,
    @SerialName("distance_meters")
    val distanceMeters: Double? = null
) {
    fun toDomainModel(agent: Agent): Property {
        val type = try {
            PropertyType.valueOf(propertyType.uppercase())
        } catch (e: Exception) {
            PropertyType.APARTMENT
        }

        val distanceKm = distanceMeters?.let {
            Math.round((it / 1000.0) * 10.0) / 10.0
        }

        return Property(
            id = id,
            title = title,
            description = description,
            propertyType = type,
            priceKes = priceKes,
            pricePeriod = pricePeriod,
            location = location,
            neighborhood = neighborhood,
            bedrooms = bedrooms,
            bathrooms = bathrooms,
            squareMeters = squareMeters,
            isVerified = isVerified,
            isFeatured = isFeatured,
            images = images,
            agent = agent,
            amenities = amenities,
            distanceKm = distanceKm
        )
    }
}
