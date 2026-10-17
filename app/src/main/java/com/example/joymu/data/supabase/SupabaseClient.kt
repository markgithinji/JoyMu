package com.example.joymu.data.supabase

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.auth.Auth

object SupabaseClientProvider {
    // Replace with your Supabase Project URL and Anon Key from Supabase Dashboard -> Project Settings -> API
    var SUPABASE_URL = "https://runydacpeyczjedpsfto.supabase.co"
    var SUPABASE_ANON_KEY = "sb_publishable_5au-H1A6iHdBpfepHPBbRQ_HHrPh_JL"

    val client by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            install(Storage)
            install(Auth)
        }
    }
}
