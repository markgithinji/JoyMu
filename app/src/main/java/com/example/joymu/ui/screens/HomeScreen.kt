package com.example.joymu.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.joymu.model.Property
import com.example.joymu.model.PropertyType
import com.example.joymu.ui.components.CategoryChipRow
import com.example.joymu.ui.components.PropertyCard
import com.example.joymu.ui.components.SearchBarHeader

@Composable
fun HomeScreen(
    properties: List<Property>,
    onPropertyClick: (Property) -> Unit,
    onFavoriteToggle: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(PropertyType.ALL) }
    var selectedLocation by remember { mutableStateOf("Nairobi 🇰🇪") }

    // Filter properties
    val filteredProperties = properties.filter { property ->
        val matchesCategory = (selectedCategory == PropertyType.ALL) || (property.propertyType == selectedCategory)
        val matchesQuery = searchQuery.isBlank() ||
                property.title.contains(searchQuery, ignoreCase = true) ||
                property.location.contains(searchQuery, ignoreCase = true) ||
                property.neighborhood.contains(searchQuery, ignoreCase = true)

        matchesCategory && matchesQuery
    }

    val featuredProperties = filteredProperties.filter { it.isFeatured }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Search Header
        SearchBarHeader(
            searchQuery = searchQuery,
            onQueryChange = { searchQuery = it },
            selectedLocation = selectedLocation,
            onFilterClick = { /* Show Filter BottomSheet */ }
        )

        // Category Filter Row
        CategoryChipRow(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        // Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Featured Properties Carousel
            if (featuredProperties.isNotEmpty() && searchQuery.isBlank()) {
                item {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Featured Listings ⭐",
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

            // All Listings Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (searchQuery.isNotBlank()) "Search Results" else "Explore Properties",
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

            // Empty State
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
                                text = "No homes match your search",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Try changing your filters or location search.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Property List
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
}
