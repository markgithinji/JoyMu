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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.joymu.R
import com.example.joymu.core.data.DummyData
import com.example.joymu.core.data.PropertyRepository
import com.example.joymu.core.designsystem.theme.JoyGreenPrimary
import com.example.joymu.core.model.Property
import com.example.joymu.feature.detail.PropertyDetailScreen
import com.example.joymu.feature.explore.HomeScreen
import com.example.joymu.feature.postproperty.PostPropertyScreen
import com.example.joymu.feature.profile.AuthScreen
import com.example.joymu.feature.profile.ProfileScreen
import com.example.joymu.feature.saved.SavedScreen
import kotlinx.coroutines.launch

enum class JoyMuTab(val titleRes: Int, val icon: ImageVector) {
    EXPLORE(R.string.tab_explore, Icons.Default.Explore),
    SAVED(R.string.tab_saved, Icons.Default.Favorite),
    POST(R.string.tab_post, Icons.Default.AddCircle),
    PROFILE(R.string.tab_profile, Icons.Default.Person)
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
                        val tabTitle = stringResource(tab.titleRes)
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
                                        Icon(imageVector = tab.icon, contentDescription = tabTitle)
                                    }
                                } else {
                                    Icon(imageVector = tab.icon, contentDescription = tabTitle)
                                }
                            },
                            label = {
                                Text(
                                    text = tabTitle,
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
                    onProximityFilter = { lat, lng, radiusKm ->
                        coroutineScope.launch {
                            val result = if (lat != null && lng != null) {
                                repository.fetchNearbyProperties(lat, lng, radiusKm)
                            } else {
                                repository.fetchProperties()
                            }
                            result.onSuccess { propertiesList = it }
                        }
                    },
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
