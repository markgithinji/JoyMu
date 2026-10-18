package com.example.joymu.feature.explore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.joymu.R
import com.example.joymu.core.designsystem.component.CategoryChipRow
import com.example.joymu.core.designsystem.component.FilterBottomSheet
import com.example.joymu.core.designsystem.component.PropertyCard
import com.example.joymu.core.designsystem.component.SearchBarHeader
import com.example.joymu.core.designsystem.theme.JoyGreenPrimary
import com.example.joymu.core.model.FilterState
import com.example.joymu.core.model.Property
import com.example.joymu.core.model.PropertyType

data class ProximityOption(
    val label: String,
    val lat: Double?,
    val lng: Double?,
    val radiusKm: Double
)

val proximityOptions = listOf(
    ProximityOption("All Kenya 🇰🇪", null, null, 0.0),
    ProximityOption("Kilimani (5km)", -1.2921, 36.7865, 5.0),
    ProximityOption("Westlands (10km)", -1.2682, 36.8080, 10.0),
    ProximityOption("Mombasa (15km)", -4.0322, 39.7111, 15.0)
)

@Composable
fun HomeScreen(
    properties: List<Property>,
    onPropertyClick: (Property) -> Unit,
    onFavoriteToggle: (Property) -> Unit,
    onProximityFilter: (lat: Double?, lng: Double?, radiusKm: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(PropertyType.ALL) }
    var selectedProximity by remember { mutableStateOf(proximityOptions.first()) }
    var filterState by remember { mutableStateOf(FilterState()) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val filteredProperties = properties.filter { property ->
        val matchesCategory = (selectedCategory == PropertyType.ALL) || (property.propertyType == selectedCategory)
        val matchesQuery = searchQuery.isBlank() ||
                property.title.contains(searchQuery, ignoreCase = true) ||
                property.location.contains(searchQuery, ignoreCase = true) ||
                property.neighborhood.contains(searchQuery, ignoreCase = true)

        val matchesPrice = property.priceKes >= filterState.minPriceKes && property.priceKes <= filterState.maxPriceKes

        val matchesBedrooms = when (val beds = filterState.selectedBedrooms) {
            null -> true
            4 -> property.bedrooms >= 4
            else -> property.bedrooms == beds
        }

        val matchesBathrooms = when (val baths = filterState.selectedBathrooms) {
            null -> true
            3 -> property.bathrooms >= 3
            else -> property.bathrooms == baths
        }

        val matchesAmenities = filterState.selectedAmenities.isEmpty() ||
                property.amenities.containsAll(filterState.selectedAmenities)

        matchesCategory && matchesQuery && matchesPrice && matchesBedrooms && matchesBathrooms && matchesAmenities
    }

    val featuredProperties = filteredProperties.filter { it.isFeatured }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        SearchBarHeader(
            searchQuery = searchQuery,
            onQueryChange = { searchQuery = it },
            selectedLocation = selectedProximity.label.split(" ").firstOrNull() ?: "Nairobi",
            onFilterClick = { showFilterSheet = true }
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            items(proximityOptions) { option ->
                val isSelected = option == selectedProximity
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedProximity = option
                        onProximityFilter(option.lat, option.lng, option.radiusKm)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.NearMe,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (isSelected) Color.White else JoyGreenPrimary
                        )
                    },
                    label = {
                        Text(
                            text = option.label,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = JoyGreenPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        CategoryChipRow(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            if (featuredProperties.isNotEmpty() && searchQuery.isBlank() && !filterState.isFiltered()) {
                item {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.featured_listings),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(featuredProperties) { property ->
                                PropertyCard(
                                    property = property,
                                    onClick = { onPropertyClick(property) },
                                    onFavoriteToggle = onFavoriteToggle,
                                    modifier = Modifier.width(300.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (filterState.isFiltered()) stringResource(R.string.filtered_homes) else if (selectedProximity.lat != null) stringResource(R.string.nearby_homes) else if (searchQuery.isNotBlank()) stringResource(R.string.search_results) else stringResource(R.string.explore_properties),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "${filteredProperties.size} homes",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (filteredProperties.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.no_homes_found),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stringResource(R.string.try_changing_filters),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            TextButton(onClick = { filterState = FilterState() }) {
                                Text(stringResource(R.string.reset_all_filters), color = JoyGreenPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                items(filteredProperties) { property ->
                    PropertyCard(
                        property = property,
                        onClick = { onPropertyClick(property) },
                        onFavoriteToggle = onFavoriteToggle,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            filterState = filterState,
            matchingCount = filteredProperties.size,
            onApplyFilter = { newFilter -> filterState = newFilter },
            onResetFilter = { filterState = FilterState() },
            onDismissRequest = { showFilterSheet = false }
        )
    }
}
