package com.example.joymu.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.joymu.model.FilterState
import com.example.joymu.ui.theme.JoyGreenPrimary
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    filterState: FilterState,
    matchingCount: Int,
    onApplyFilter: (FilterState) -> Unit,
    onResetFilter: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var minPrice by remember(filterState) { mutableFloatStateOf(filterState.minPriceKes) }
    var maxPrice by remember(filterState) { mutableFloatStateOf(filterState.maxPriceKes) }
    var selectedBedrooms by remember(filterState) { mutableStateOf(filterState.selectedBedrooms) }
    var selectedBathrooms by remember(filterState) { mutableStateOf(filterState.selectedBathrooms) }
    val selectedAmenities = remember(filterState) { mutableStateListOf(*filterState.selectedAmenities.toTypedArray()) }

    val amenityOptions = listOf(
        "Borehole Water", "Backup Generator", "High-Speed WiFi",
        "24/7 Security/CCTV", "Swimming Pool", "Gym", "Balcony View", "Underground Parking"
    )

    val formattedMinPrice = NumberFormat.getNumberInstance(Locale.US).format(minPrice.toInt())
    val formattedMaxPrice = NumberFormat.getNumberInstance(Locale.US).format(maxPrice.toInt())

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Properties 🎛️",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onResetFilter) {
                        Text("Reset All", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                // Rent Price Slider Section
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Monthly Rent Range (KSh)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                text = "KSh $formattedMinPrice – KSh $formattedMaxPrice",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = JoyGreenPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        RangeSlider(
                            value = minPrice..maxPrice,
                            onValueChange = { range ->
                                minPrice = range.start
                                maxPrice = range.endInclusive
                            },
                            valueRange = 0f..300000f,
                            steps = 29,
                            colors = SliderDefaults.colors(
                                thumbColor = JoyGreenPrimary,
                                activeTrackColor = JoyGreenPrimary
                            )
                        )
                    }
                }

                // Bedrooms Count Section
                item {
                    Column {
                        Text(text = "Bedrooms Count", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val bedroomOptions = listOf(null to "Any", 0 to "Studio", 1 to "1 Bed", 2 to "2 Beds", 3 to "3 Beds", 4 to "4+ Beds")
                            bedroomOptions.forEach { (count, label) ->
                                val isSelected = selectedBedrooms == count
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedBedrooms = count },
                                    label = { Text(label, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = JoyGreenPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                // Bathrooms Count Section
                item {
                    Column {
                        Text(text = "Bathrooms Count", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val bathroomOptions = listOf(null to "Any", 1 to "1 Bath", 2 to "2 Baths", 3 to "3+ Baths")
                            bathroomOptions.forEach { (count, label) ->
                                val isSelected = selectedBathrooms == count
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedBathrooms = count },
                                    label = { Text(label, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = JoyGreenPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                // Amenities Section
                item {
                    Column {
                        Text(text = "Specific Amenities", fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Apply Filters Button
            Button(
                onClick = {
                    onApplyFilter(
                        FilterState(
                            minPriceKes = minPrice,
                            maxPriceKes = maxPrice,
                            selectedBedrooms = selectedBedrooms,
                            selectedBathrooms = selectedBathrooms,
                            selectedAmenities = selectedAmenities.toSet()
                        )
                    )
                    onDismissRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = JoyGreenPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Apply Filters ($matchingCount homes match)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
