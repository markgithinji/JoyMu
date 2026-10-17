# JoyMu AI Agent Context & Multi-Module Target Map

This document serves as the persistent architectural roadmap for AI agents working on **JoyMu**.

---

## Target Multi-Module Architecture Map

```text
                                [ :app ]
                                   │
      ┌────────────────────────────┼────────────────────────────┐
      ▼                            ▼                            ▼
[ :feature:explore ]     [ :feature:detail ]          [ :feature:post_property ]
[ :feature:saved ]       [ :feature:profile ]         [ :feature:auth ]
      │                            │                            │
      └────────────────────────────┼────────────────────────────┘
                                   ▼
                            [ :core:data ]
                                   │
                 ┌─────────────────┴─────────────────┐
                 ▼                                   ▼
        [ :core:network ]                   [ :core:database ]
                 │                                   │
                 └─────────────────┬─────────────────┘
                                   ▼
                            [ :core:model ]
                                   │
                                   ▼
                        [ :core:designsystem ]
```

---

## Module Responsibilities

1. **`:core:model`**: Pure Kotlin data models (`Property`, `Agent`, `Inquiry`, `PropertyType`, `FilterState`). Zero Android dependencies.
2. **`:core:designsystem`**: M3 Theme, JoyMu color tokens, icons, and reusable components (`PropertyCard`, `SearchBarHeader`, `CategoryChipRow`, `FilterBottomSheet`).
3. **`:core:network`**: Retrofit / Ktor clients, Supabase Client configuration, `NetworkResult`, `safeApiCall`, and DTOs.
4. **`:core:data`**: Repositories (`PropertyRepository`, `InquiryRepository`, `AuthRepository`) implementing offline-first caching with Room and Supabase sync.
5. **`:feature:*`**: Individual feature modules containing MVI ViewModels, UI Screens, and Navigation destinations.
6. **`:app`**: Root application module wiring Hilt `@HiltAndroidApp`, Navigation graph, and main activity entry point.

---

## Coding Conventions
- **Kotlin Coroutines & Flow**: Use `StateFlow` for state, `SharedFlow`/`Channel` for one-off effects.
- **Hilt Dependency Injection**: All ViewModels annotated with `@HiltViewModel @Inject constructor(...)`.
- **Formatting**: Strict trailing commas, descriptive function names, and `strings.xml` resource references.
