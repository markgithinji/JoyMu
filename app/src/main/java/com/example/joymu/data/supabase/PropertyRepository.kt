package com.example.joymu.data.supabase

import android.content.Context
import android.net.Uri
import com.example.joymu.data.DummyData
import com.example.joymu.model.Property
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID

class PropertyRepository {

    suspend fun fetchProperties(): Result<List<Property>> = withContext(Dispatchers.IO) {
        try {
            val dtoList = SupabaseClientProvider.client
                .from("properties")
                .select()
                .decodeList<PropertyDto>()

            val properties = dtoList.map { dto ->
                dto.toDomainModel(DummyData.sampleAgents.first())
            }

            if (properties.isNotEmpty()) {
                Result.success(properties)
            } else {
                Result.success(DummyData.sampleProperties)
            }
        } catch (e: Exception) {
            Result.success(DummyData.sampleProperties)
        }
    }

    suspend fun fetchNearbyProperties(
        lat: Double,
        lng: Double,
        radiusKm: Double
    ): Result<List<Property>> = withContext(Dispatchers.IO) {
        try {
            val parametersJson = buildJsonObject {
                put("user_lat", lat)
                put("user_lng", lng)
                put("radius_meters", radiusKm * 1000.0)
            }

            val dtoList = SupabaseClientProvider.client.postgrest.rpc(
                function = "get_nearby_properties",
                parameters = parametersJson
            ).decodeList<PropertyDto>()

            val properties = dtoList.map { dto ->
                dto.toDomainModel(DummyData.sampleAgents.first())
            }

            Result.success(properties)
        } catch (e: Exception) {
            fetchProperties()
        }
    }

    suspend fun createProperty(property: Property): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val dto = PropertyDto(
                id = property.id,
                title = property.title,
                description = property.description,
                propertyType = property.propertyType.name,
                priceKes = property.priceKes,
                pricePeriod = property.pricePeriod,
                location = property.location,
                neighborhood = property.neighborhood,
                bedrooms = property.bedrooms,
                bathrooms = property.bathrooms,
                squareMeters = property.squareMeters,
                isVerified = property.isVerified,
                isFeatured = property.isFeatured,
                images = property.images,
                amenities = property.amenities,
                agentId = property.agent.id
            )

            SupabaseClientProvider.client
                .from("properties")
                .insert(dto)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadPhoto(context: Context, uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: return@withContext Result.failure(Exception("Unable to read image file"))

            val fileName = "property_${UUID.randomUUID()}.jpg"
            val bucket = SupabaseClientProvider.client.storage.from("property-photos")

            bucket.upload(fileName, bytes)
            val publicUrl = bucket.publicUrl(fileName)

            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
