package com.example.joymu.feature.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
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
import com.example.joymu.core.data.InquiryRepository
import com.example.joymu.core.data.PropertyRepository
import com.example.joymu.core.designsystem.theme.JoyGreenPrimary
import com.example.joymu.core.designsystem.theme.JoyTerracottaSecondary
import com.example.joymu.core.model.InquiryDto
import com.example.joymu.core.model.Property
import com.example.joymu.feature.profile.data.AuthRepository
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val inquiryRepository = remember { InquiryRepository() }
    val propertyRepository = remember { PropertyRepository() }

    var currentUser by remember { mutableStateOf(authRepository.getCurrentUser()) }
    var inquiriesList by remember { mutableStateOf<List<InquiryDto>>(emptyList()) }
    var myPropertiesList by remember { mutableStateOf<List<Property>>(emptyList()) }
    var isLoadingData by remember { mutableStateOf(true) }

    var propertyToEdit by remember { mutableStateOf<Property?>(null) }
    var newPriceText by remember { mutableStateOf("") }
    var propertyToDelete by remember { mutableStateOf<Property?>(null) }

    fun loadData() {
        coroutineScope.launch {
            isLoadingData = true
            val inqResult = inquiryRepository.fetchInquiries()
            inqResult.onSuccess { inquiriesList = it }

            val propResult = propertyRepository.fetchProperties()
            propResult.onSuccess { myPropertiesList = it }

            isLoadingData = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(stringResource(R.string.agent_portal_title), fontWeight = FontWeight.Bold, fontSize = 20.sp)
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1560250097-0b93528c311a?w=400",
                            contentDescription = "Agent Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = currentUser?.email ?: "Otieno Real Estate",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Text(
                            text = if (currentUser != null) "Logged in via Supabase Auth 🟢" else "Nairobi Prime Properties • Verified Agent 🇰🇪",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (currentUser == null) {
                            Button(
                                onClick = onNavigateToAuth,
                                colors = ButtonDefaults.buttonColors(containerColor = JoyGreenPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.sign_in_register), fontWeight = FontWeight.Bold)
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    coroutineScope.launch {
                                        authRepository.signOut()
                                        currentUser = null
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.sign_out))
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.my_posted_listings),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Badge(containerColor = JoyGreenPrimary) {
                        Text("${myPropertiesList.size} Properties", color = Color.White)
                    }
                }
            }

            if (myPropertiesList.isNotEmpty()) {
                items(myPropertiesList) { property ->
                    val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(property.priceKes)

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = property.images.firstOrNull(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = property.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 1
                                )
                                Text(
                                    text = property.location,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "KSh $formattedPrice ${property.pricePeriod}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = JoyGreenPrimary
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                IconButton(
                                    onClick = {
                                        propertyToEdit = property
                                        newPriceText = property.priceKes.toString()
                                    }
                                ) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Price", tint = JoyGreenPrimary)
                                }
                                IconButton(
                                    onClick = {
                                        propertyToDelete = property
                                    }
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = JoyTerracottaSecondary)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.tenant_inquiries_inbox),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Badge(containerColor = JoyGreenPrimary) {
                        Text("${inquiriesList.size} Messages", color = Color.White)
                    }
                }
            }

            if (isLoadingData) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = JoyGreenPrimary)
                    }
                }
            } else if (inquiriesList.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("No tenant inquiries received yet.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(inquiriesList) { inquiry ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MarkUnreadChatAlt,
                                        contentDescription = null,
                                        tint = JoyGreenPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = inquiry.tenantName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                                Text(
                                    text = inquiry.tenantPhone,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = JoyGreenPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = JoyGreenPrimary.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = inquiry.propertyTitle,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = JoyGreenPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "“${inquiry.message}”",
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${inquiry.tenantPhone}"))
                                        context.startActivity(dialIntent)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.call), fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        val waPhone = inquiry.tenantPhone.replace("+", "").replace(" ", "")
                                        val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$waPhone?text=Hi%20${inquiry.tenantName},%20regarding%20your%20inquiry%20for%20${inquiry.propertyTitle}"))
                                        context.startActivity(waIntent)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.reply_whatsapp), fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = JoyGreenPrimary.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = JoyGreenPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Supabase Realtime Messaging Ready 💬",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = JoyGreenPrimary
                            )
                            Text(
                                text = "Tenant messages stored in Supabase PostgreSQL 'inquiries' table.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.account_settings),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsOptionRow(
                        icon = Icons.Default.AccountBalanceWallet,
                        title = "M-Pesa Payout Account",
                        subtitle = "0712 *** 678 (Verified)"
                    )
                    SettingsOptionRow(
                        icon = Icons.Default.VerifiedUser,
                        title = "Agent Deed & License Verification",
                        subtitle = "Verified status active"
                    )
                    SettingsOptionRow(
                        icon = Icons.AutoMirrored.Filled.Help,
                        title = "JoyMu Support & Agent Help",
                        subtitle = "Call +254 700 000 000"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (propertyToEdit != null) {
        AlertDialog(
            onDismissRequest = { propertyToEdit = null },
            title = { Text("Update Rent Price (KSh)") },
            text = {
                Column {
                    Text(text = propertyToEdit!!.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPriceText,
                        onValueChange = { newPriceText = it },
                        label = { Text("New Monthly Rent (KES)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val priceInt = newPriceText.toIntOrNull()
                        if (priceInt != null) {
                            coroutineScope.launch {
                                propertyRepository.updatePropertyPrice(propertyToEdit!!.id, priceInt)
                                propertyToEdit = null
                                loadData()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JoyGreenPrimary)
                ) {
                    Text("Save Price")
                }
            },
            dismissButton = {
                TextButton(onClick = { propertyToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (propertyToDelete != null) {
        AlertDialog(
            onDismissRequest = { propertyToDelete = null },
            title = { Text("Delete Listing?") },
            text = { Text("Are you sure you want to remove '${propertyToDelete!!.title}' from Supabase?") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            propertyRepository.deleteProperty(propertyToDelete!!.id)
                            propertyToDelete = null
                            loadData()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JoyTerracottaSecondary)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { propertyToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = JoyGreenPrimary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
