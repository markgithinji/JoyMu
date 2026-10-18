package com.example.joymu.core.data

import com.example.joymu.core.network.SupabaseClientProvider
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class UserProfile(
    val id: String,
    val email: String,
    val name: String,
    val phoneNumber: String,
    val role: String = "AGENT"
)

class AuthRepository {

    private val authClient = SupabaseClientProvider.client.auth

    suspend fun signUpWithEmail(
        email: String,
        password: String,
        name: String = "",
        phone: String = "",
        role: String = "AGENT"
    ): Result<UserInfo?> = withContext(Dispatchers.IO) {
        try {
            authClient.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            val user = authClient.currentUserOrNull()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<UserInfo?> = withContext(Dispatchers.IO) {
        try {
            authClient.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val user = authClient.currentUserOrNull()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPhoneOtp(phoneNumber: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            authClient.signUpWith(OTP) {
                this.phone = phoneNumber
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyPhoneOtp(phoneNumber: String, token: String): Result<UserInfo?> = withContext(Dispatchers.IO) {
        try {
            authClient.verifyPhoneOtp(
                type = OtpType.Phone.SMS,
                phone = phoneNumber,
                token = token
            )
            val user = authClient.currentUserOrNull()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            authClient.signOut()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser(): UserInfo? {
        return authClient.currentUserOrNull()
    }
}
