package com.example.joymu.core.model

enum class PropertyType(val displayName: String) {
    ALL("All Properties"),
    APARTMENT("Apartments"),
    STUDIO("Studios"),
    BEDSITTER("Bedsitters"),
    AIRBNB("Shortlet / Airbnb"),
    VILLA("Villas & Houses"),
    COMMERCIAL("Commercial")
}

data class Agent(
    val id: String,
    val name: String,
    val companyName: String,
    val phoneNumber: String,
    val whatsappNumber: String,
    val avatarUrl: String,
    val rating: Double = 4.9,
    val reviewCount: Int = 24,
    val isVerified: Boolean = true
)

data class Property(
    val id: String,
    val title: String,
    val description: String,
    val propertyType: PropertyType,
    val priceKes: Int,
    val pricePeriod: String = "/month", // "/month" or "/night"
    val location: String, // e.g. "Kilimani, Nairobi"
    val neighborhood: String, // e.g. "Argwings Kodhek Rd"
    val bedrooms: Int,
    val bathrooms: Int,
    val squareMeters: Int? = null,
    val isVerified: Boolean = true,
    val isFeatured: Boolean = false,
    val images: List<String>,
    val agent: Agent,
    val amenities: List<String>,
    val isSaved: Boolean = false,
    val distanceKm: Double? = null
)
