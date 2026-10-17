package com.example.joymu.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.joymu.data.DummyData
import com.example.joymu.data.supabase.PropertyRepository
import com.example.joymu.model.Property
import com.example.joymu.ui.screens.*
import com.example.joymu.ui.theme.JoyGreenPrimary
import kotlinx.coroutines.launch

enum class JoyMuTab(val title: String, val icon: ImageVector) {
    EXPLORE("Explore", Icons.Default.Explore),
    SAVED("Saved", Icons.Default.Favorite),
    POST("Post Home", Icons.Default.AddCircle),
    PROFILE("Profile", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoyMuMainApp() {
    val repository = remember { PropertyRepository() }
    val coroutineScope = rememberCoroutineScope()

    var propertiesList by remember { mutableStateOf(DummyData.sampleProperties) }
    var selectedTab by remember { mutableStateOf(JoyMuTab.EXPLORE) }
    var selectedPropertyForDetail by remember { mutableStateOf<Property?>(null) }
    var isShowingAuthScreen by remember { mutableStateOf(false) }

    // Fetch properties from Supabase on app launch
    LaunchedEffect(Unit) {
        val result = repository.fetchProperties()
        result.onSuccess { fetchedList ->
            if (fetchedList.isNotEmpty()) {
                propertiesList = fetchedList
            }
        }
    }

    val savedCount = propertiesList.count { it.isSaved }

    fun toggleFavorite(property: Property) {
        propertiesList = propertiesList.map { p ->
            if (p.id == property.id) {
                p.copy(isSaved = !p.isSaved)
            } else p
        }
        if (selectedPropertyForDetail?.id == property.id) {
            selectedPropertyForDetail = selectedPropertyForDetail?.copy(isSaved = !(selectedPropertyForDetail?.isSaved ?: false))
        }
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = selectedPropertyForDetail == null && !isShowingAuthScreen,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    JoyMuTab.entries.forEach { tab ->
                        val isSelected = selectedTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            icon = {
                                if (tab == JoyMuTab.SAVED && savedCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge { Text("$savedCount") }
                                        }
                                    ) {
                                        Icon(imageVector = tab.icon, contentDescription = tab.title)
                                    }
                                } else {
                                    Icon(imageVector = tab.icon, contentDescription = tab.title)
                                }
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = JoyGreenPrimary,
                                selectedTextColor = JoyGreenPrimary,
                                indicatorColor = JoyGreenPrimary.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (isShowingAuthScreen) {
            AuthScreen(
                onAuthSuccess = { isShowingAuthScreen = false },
                onBackClick = { isShowingAuthScreen = false }
            )
        } else if (selectedPropertyForDetail != null) {
            PropertyDetailScreen(
                property = selectedPropertyForDetail!!,
                onBackClick = { selectedPropertyForDetail = null },
                onFavoriteToggle = { toggleFavorite(it) }
            )
        } else {
            val modifier = Modifier.padding(innerPadding)
            when (selectedTab) {
                JoyMuTab.EXPLORE -> HomeScreen(
                    properties = propertiesList,
                    onPropertyClick = { selectedPropertyForDetail = it },
                    onFavoriteToggle = { toggleFavorite(it) },
                    modifier = modifier
                )
                JoyMuTab.SAVED -> SavedScreen(
                    savedProperties = propertiesList.filter { it.isSaved },
                    onPropertyClick = { selectedPropertyForDetail = it },
                    onFavoriteToggle = { toggleFavorite(it) },
                    modifier = modifier
                )
                JoyMuTab.POST -> PostPropertyScreen(
                    onListingCreated = {
                        coroutineScope.launch {
                            val refreshed = repository.fetchProperties()
                            refreshed.onSuccess { propertiesList = it }
                        }
                        selectedTab = JoyMuTab.EXPLORE
                    },
                    modifier = modifier
                )
                JoyMuTab.PROFILE -> ProfileScreen(
                    onNavigateToAuth = { isShowingAuthScreen = true },
                    modifier = modifier
                )
            }
        }
    }
}
