package com.example.joymu.data

import com.example.joymu.model.Agent
import com.example.joymu.model.Property
import com.example.joymu.model.PropertyType

object DummyData {

    val sampleAgents = listOf(
        Agent(
            id = "agent_1",
            name = "Otieno Real Estate",
            companyName = "Nairobi Prime Properties",
            phoneNumber = "+254712345678",
            whatsappNumber = "254712345678",
            avatarUrl = "https://images.unsplash.com/photo-1560250097-0b93528c311a?w=400",
            rating = 4.9,
            reviewCount = 42,
            isVerified = true
        ),
        Agent(
            id = "agent_2",
            name = "Jane Wanjiku",
            companyName = "Kilimani Living Realtors",
            phoneNumber = "+254722987654",
            whatsappNumber = "254722987654",
            avatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400",
            rating = 4.8,
            reviewCount = 19,
            isVerified = true
        ),
        Agent(
            id = "agent_3",
            name = "Hassan Mohammed",
            companyName = "Coastline Luxury Homes",
            phoneNumber = "+254733112233",
            whatsappNumber = "254733112233",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
            rating = 5.0,
            reviewCount = 31,
            isVerified = true
        )
    )

    val sampleProperties = listOf(
        Property(
            id = "prop_1",
            title = "Modern 2-Bed Luxury Apartment in Kilimani",
            description = "Stunning fully furnished 2-bedroom en-suite apartment situated along Argwings Kodhek Road. Features an open-plan fitted kitchen, high-speed lifts, full backup generator, borehole water supply, and top-tier 24/7 security with biometric access.",
            propertyType = PropertyType.APARTMENT,
            priceKes = 75000,
            pricePeriod = "/month",
            location = "Kilimani, Nairobi",
            neighborhood = "Argwings Kodhek Rd",
            bedrooms = 2,
            bathrooms = 2,
            squareMeters = 115,
            isVerified = true,
            isFeatured = true,
            images = listOf(
                "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800",
                "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800",
                "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800"
            ),
            agent = sampleAgents[0],
            amenities = listOf("Borehole Water", "Backup Generator", "High-Speed WiFi", "Gym & Pool", "24/7 Security/CCTV", "Balcony View")
        ),
        Property(
            id = "prop_2",
            title = "Executive Studio / Bedsitter near Sarit Centre",
            description = "Cozy executive studio apartment in Westlands, just 3 minutes walk from Sarit Centre and Westgate Mall. High security, ample underground parking, internet ready, and water included.",
            propertyType = PropertyType.STUDIO,
            priceKes = 35000,
            pricePeriod = "/month",
            location = "Westlands, Nairobi",
            neighborhood = "Muthithi Road",
            bedrooms = 1,
            bathrooms = 1,
            squareMeters = 45,
            isVerified = true,
            isFeatured = true,
            images = listOf(
                "https://images.unsplash.com/photo-1536376072261-38c75010e6c9?w=800",
                "https://images.unsplash.com/photo-1502005229762-cf1b2da7c5d6?w=800"
            ),
            agent = sampleAgents[1],
            amenities = listOf("High-Speed WiFi", "Underground Parking", "CCTV Surveillance", "Borehole Water", "Elevator")
        ),
        Property(
            id = "prop_3",
            title = "Beachfront 3-Bed Villa with Private Pool",
            description = "Exclusive beach vacation shortlet villa in Nyali, Mombasa. Direct ocean access, private infinity pool, air-conditioned rooms, chef service on request, and high-speed Wi-Fi. Ideal for weekend getaways.",
            propertyType = PropertyType.AIRBNB,
            priceKes = 18500,
            pricePeriod = "/night",
            location = "Nyali, Mombasa",
            neighborhood = "Links Road",
            bedrooms = 3,
            bathrooms = 3,
            squareMeters = 240,
            isVerified = true,
            isFeatured = true,
            images = listOf(
                "https://images.unsplash.com/photo-1580587771525-78b9dba3b914?w=800",
                "https://images.unsplash.com/photo-1613977257363-707ba9348227?w=800",
                "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800"
            ),
            agent = sampleAgents[2],
            amenities = listOf("Private Swimming Pool", "Beach Access", "Air Conditioning", "Chef Available", "Free Wi-Fi", "Ample Parking")
        ),
        Property(
            id = "prop_4",
            title = "Spacious 3-Bedroom Master En-Suite",
            description = "Generously sized 3-bedroom apartment with DSQ in Kileleshwa. Features hardwood floors, large balcony overlooking serene greenery, separate dining area, solar water heating, and electric fence.",
            propertyType = PropertyType.APARTMENT,
            priceKes = 95000,
            pricePeriod = "/month",
            location = "Kileleshwa, Nairobi",
            neighborhood = "Oloitokitok Rd",
            bedrooms = 3,
            bathrooms = 3,
            squareMeters = 160,
            isVerified = true,
            isFeatured = false,
            images = listOf(
                "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800",
                "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=800"
            ),
            agent = sampleAgents[0],
            amenities = listOf("DSQ Included", "Solar Water Heater", "Backup Generator", "Borehole Water", "Garden", "Electric Fence")
        ),
        Property(
            id = "prop_5",
            title = "Cozy 1-Bedroom Apartment near SGR & JKIA",
            description = "Ideal for frequent travelers and airport workers. Located in Syokau, 10 minutes drive from JKIA and SGR Nairobi Terminus. Modern finishings, solar lighting in common areas, clean borehole water.",
            propertyType = PropertyType.APARTMENT,
            priceKes = 26000,
            pricePeriod = "/month",
            location = "Syokau, Machakos",
            neighborhood = "Chania Avenue",
            bedrooms = 1,
            bathrooms = 1,
            squareMeters = 55,
            isVerified = false,
            isFeatured = false,
            images = listOf(
                "https://images.unsplash.com/photo-1560185007-c5ca9d2c014d?w=800"
            ),
            agent = sampleAgents[1],
            amenities = listOf("Close to Airport/SGR", "Borehole Water", "24/7 Security", "Balcony", "Parking Space")
        ),
        Property(
            id = "prop_6",
            title = "Luxury 5-Bedroom Mansion in Gated Runda Estate",
            description = "Palatial 5-bedroom all en-suite residence set on 0.5 acres in Runda. UN-approved security standards, landscaped lawn, heated pool, sauna, guardhouse, and double garage.",
            propertyType = PropertyType.VILLA,
            priceKes = 350000,
            pricePeriod = "/month",
            location = "Runda, Nairobi",
            neighborhood = "Runda Drive",
            bedrooms = 5,
            bathrooms = 5,
            squareMeters = 450,
            isVerified = true,
            isFeatured = true,
            images = listOf(
                "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=800",
                "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=800"
            ),
            agent = sampleAgents[2],
            amenities = listOf("UN Approved Security", "Heated Pool", "0.5 Acre Garden", "Sauna & Gym", "Perimeter Fence", "Double Garage")
        )
    )
}
