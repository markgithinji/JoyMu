package com.example.joymu.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.joymu.R
import com.example.joymu.core.data.AuthRepository
import com.example.joymu.core.designsystem.theme.JoyGreenPrimary
import kotlinx.coroutines.launch

enum class AuthMode {
    EMAIL_SIGN_IN,
    EMAIL_SIGN_UP,
    PHONE_OTP
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }

    var authMode by remember { mutableStateOf(AuthMode.EMAIL_SIGN_IN) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("+254 ") }
    var isAgentRole by remember { mutableStateOf(true) }

    var otpCode by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun handleAuth() {
        errorMessage = null
        isLoading = true

        coroutineScope.launch {
            when (authMode) {
                AuthMode.EMAIL_SIGN_IN -> {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Please enter your email and password"
                        isLoading = false
                        return@launch
                    }
                    val result = authRepository.signInWithEmail(email.trim(), password)
                    isLoading = false
                    result.onSuccess {
                        onAuthSuccess()
                    }.onFailure { e ->
                        errorMessage = e.localizedMessage ?: "Invalid email or password"
                    }
                }
                AuthMode.EMAIL_SIGN_UP -> {
                    if (email.isBlank() || password.length < 6) {
                        errorMessage = "Password must be at least 6 characters"
                        isLoading = false
                        return@launch
                    }
                    val role = if (isAgentRole) "AGENT" else "TENANT"
                    val result = authRepository.signUpWithEmail(email.trim(), password, fullName, phoneNumber, role)
                    isLoading = false
                    result.onSuccess {
                        onAuthSuccess()
                    }.onFailure { e ->
                        errorMessage = e.localizedMessage ?: "Registration failed. Try again."
                    }
                }
                AuthMode.PHONE_OTP -> {
                    if (!isOtpSent) {
                        if (phoneNumber.length < 10) {
                            errorMessage = "Enter valid phone number e.g. +254 712 345 678"
                            isLoading = false
                            return@launch
                        }
                        val result = authRepository.sendPhoneOtp(phoneNumber.replace(" ", ""))
                        isLoading = false
                        result.onSuccess {
                            isOtpSent = true
                        }.onFailure { e ->
                            errorMessage = e.localizedMessage ?: "Failed to send SMS OTP"
                        }
                    } else {
                        if (otpCode.length < 6) {
                            errorMessage = "Enter 6-digit SMS verification code"
                            isLoading = false
                            return@launch
                        }
                        val result = authRepository.verifyPhoneOtp(phoneNumber.replace(" ", ""), otpCode.trim())
                        isLoading = false
                        result.onSuccess {
                            onAuthSuccess()
                        }.onFailure { e ->
                            errorMessage = e.localizedMessage ?: "Invalid OTP verification code"
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.auth_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (authMode == AuthMode.EMAIL_SIGN_UP) stringResource(R.string.create_account) else stringResource(R.string.welcome_back),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Access live Kenyan rental listings & agent portal",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = authMode == AuthMode.EMAIL_SIGN_IN,
                        onClick = { authMode = AuthMode.EMAIL_SIGN_IN },
                        label = { Text(stringResource(R.string.sign_in), fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JoyGreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = authMode == AuthMode.EMAIL_SIGN_UP,
                        onClick = { authMode = AuthMode.EMAIL_SIGN_UP },
                        label = { Text(stringResource(R.string.register), fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JoyGreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = authMode == AuthMode.PHONE_OTP,
                        onClick = { authMode = AuthMode.PHONE_OTP },
                        label = { Text(stringResource(R.string.phone_sms), fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JoyGreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            if (errorMessage != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = errorMessage!!,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            when (authMode) {
                AuthMode.EMAIL_SIGN_IN -> {
                    item {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                AuthMode.EMAIL_SIGN_UP -> {
                    item {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name / Agency Name") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("M-Pesa Phone Number") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password (6+ chars)") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Account Type:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            RadioButton(selected = isAgentRole, onClick = { isAgentRole = true })
                            Text("Agent / Landlord", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            RadioButton(selected = !isAgentRole, onClick = { isAgentRole = false })
                            Text("Tenant", fontSize = 13.sp)
                        }
                    }
                }

                AuthMode.PHONE_OTP -> {
                    item {
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Safaricom / Airtel Phone Number") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                            placeholder = { Text("+254 712 345 678") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isOtpSent
                        )
                    }

                    if (isOtpSent) {
                        item {
                            OutlinedTextField(
                                value = otpCode,
                                onValueChange = { otpCode = it },
                                label = { Text("6-Digit Verification SMS Code") },
                                leadingIcon = { Icon(imageVector = Icons.Default.Sms, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { handleAuth() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JoyGreenPrimary),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = when (authMode) {
                                AuthMode.EMAIL_SIGN_IN -> "Sign In to JoyMu"
                                AuthMode.EMAIL_SIGN_UP -> "Create Account"
                                AuthMode.PHONE_OTP -> if (isOtpSent) "Verify Code & Sign In" else "Send SMS Verification Code"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
