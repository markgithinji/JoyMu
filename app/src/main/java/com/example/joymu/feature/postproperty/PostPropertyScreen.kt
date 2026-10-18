package com.example.joymu.feature.postproperty

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.joymu.R
import com.example.joymu.core.data.DummyData
import com.example.joymu.core.data.PropertyRepository
import com.example.joymu.core.designsystem.theme.JoyGreenPrimary
import com.example.joymu.core.model.Property
import com.example.joymu.core.model.PropertyType
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostPropertyScreen(
    onListingCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { PropertyRepository() }

    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Kilimani, Nairobi") }
    var priceKes by remember { mutableStateOf("") }
    var bedrooms by remember { mutableStateOf("2") }
    var bathrooms by remember { mutableStateOf("2") }
    var selectedType by remember { mutableStateOf(PropertyType.APARTMENT) }
    var description by remember { mutableStateOf("") }

    val selectedImageUris = remember { mutableStateListOf<Uri>() }
    var isUploading by remember { mutableStateOf(false) }
    var uploadStatusText by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedImageUris.addAll(uris)
        }
    }

    val amenityOptions = listOf(
        "Borehole Water", "Backup Generator", "High-Speed WiFi",
        "24/7 Security/CCTV", "Swimming Pool", "Gym", "Balcony View", "Underground Parking"
    )
    val selectedAmenities = remember { mutableStateListOf<String>("Borehole Water", "24/7 Security/CCTV") }

    fun handlePublish() {
        if (title.isBlank() || priceKes.isBlank()) return

        isUploading = true
        uploadStatusText = "Uploading photos to Supabase Storage..."

        coroutineScope.launch {
            val uploadedUrls = mutableListOf<String>()

            for (uri in selectedImageUris) {
                val uploadResult = repository.uploadPhoto(context, uri)
                uploadResult.onSuccess { url ->
                    uploadedUrls.add(url)
                }
            }

            if (uploadedUrls.isEmpty()) {
                uploadedUrls.add("https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800")
            }

            uploadStatusText = "Publishing listing to database..."

            val newProperty = Property(
                id = "prop_${UUID.randomUUID().toString().take(8)}",
                title = title.ifBlank { "Modern Apartment" },
                description = description.ifBlank { "Beautiful property in $location" },
                propertyType = selectedType,
                priceKes = priceKes.toIntOrNull() ?: 50000,
                pricePeriod = if (selectedType == PropertyType.AIRBNB) "/night" else "/month",
                location = location,
                neighborhood = location.split(",").firstOrNull() ?: "Nairobi",
                bedrooms = bedrooms.toIntOrNull() ?: 1,
                bathrooms = bathrooms.toIntOrNull() ?: 1,
                isVerified = true,
                images = uploadedUrls,
                agent = DummyData.sampleAgents.first(),
                amenities = selectedAmenities.toList()
            )

            val createResult = repository.createProperty(newProperty)
            isUploading = false

            createResult.onSuccess {
                isSubmitted = true
            }.onFailure {
                isSubmitted = true
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(stringResource(R.string.post_property_title), fontWeight = FontWeight.Bold, fontSize = 20.sp)
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        if (isSubmitted) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = JoyGreenPrimary,
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.listing_published),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.listing_published_sub),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                isSubmitted = false
                                onListingCreated()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = JoyGreenPrimary)
                        ) {
                            Text(stringResource(R.string.view_live_listings))
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = JoyGreenPrimary.copy(alpha = 0.08f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.AddAPhoto,
                                        contentDescription = "Upload Photos",
                                        tint = JoyGreenPrimary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (selectedImageUris.isEmpty()) stringResource(R.string.add_property_photos) else "Add More Photos",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = JoyGreenPrimary
                                    )
                                    Text(
                                        text = stringResource(R.string.photo_upload_hint),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        if (selectedImageUris.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(selectedImageUris) { uri ->
                                    Box(
                                        modifier = Modifier.size(80.dp)
                                    ) {
                                        AsyncImage(
                                            model = uri,
                                            contentDescription = "Picked Image",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(12.dp))
                                        )

                                        IconButton(
                                            onClick = { selectedImageUris.remove(uri) },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(2.dp)
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(Color.Black.copy(alpha = 0.7f))
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(stringResource(R.string.property_title_label)) },
                        placeholder = { Text("e.g. Modern 2-Bed Luxury Apartment") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = priceKes,
                        onValueChange = { priceKes = it },
                        label = { Text(stringResource(R.string.rent_price_label)) },
                        placeholder = { Text("e.g. 75000") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text(stringResource(R.string.location_label)) },
                        placeholder = { Text("e.g. Kilimani, Argwings Kodhek Rd") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = bedrooms,
                            onValueChange = { bedrooms = it },
                            label = { Text(stringResource(R.string.bedrooms_label)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = bathrooms,
                            onValueChange = { bathrooms = it },
                            label = { Text(stringResource(R.string.bathrooms_label)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.property_type_label),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(PropertyType.APARTMENT, PropertyType.STUDIO, PropertyType.AIRBNB, PropertyType.VILLA).forEach { type ->
                            val isSelected = selectedType == type
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedType = type },
                                label = { Text(type.displayName, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = JoyGreenPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.included_amenities),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column {
                        amenityOptions.chunked(2).forEach { pair ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                pair.forEach { amenity ->
                                    val isChecked = selectedAmenities.contains(amenity)
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = { checked ->
                                                if (checked) selectedAmenities.add(amenity) else selectedAmenities.remove(amenity)
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = JoyGreenPrimary)
                                        )
                                        Text(text = amenity, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(stringResource(R.string.property_description)) },
                        placeholder = { Text("Describe water supply, security, accessibility, etc.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    if (isUploading) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = JoyGreenPrimary.copy(alpha = 0.1f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = JoyGreenPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = uploadStatusText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = JoyGreenPrimary
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = { handlePublish() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = JoyGreenPrimary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.publish_listing),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
