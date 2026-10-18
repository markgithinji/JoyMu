# JoyMu AI Agent Context & Multi-Module Target Map

This document serves as the persistent architectural roadmap for AI agents working on **JoyMu**.

---

## Target Multi-Module Architecture Map

```text
                                [ :app ]
                                   │
      ┌────────────────────────────┼────────────────────────────┐
      ▼                            ▼                            ▼
[ :feature:explore ]     [ :feature:detail ]          [ :feature:postproperty ]
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

## Module Responsibilities & Architectural Rules

1. **Feature Data Isolation**:
   - Repositories, DTOs, or state handlers used strictly by a single feature (e.g. `AuthRepository` in `feature.profile` / `feature.auth`, `InquiryRepository` in `feature.profile` / `feature.detail`) MUST reside in their respective feature package/module rather than polluting global `:core:data`.

2. **Inter-Feature Communication via Contracts**:
   - Feature modules/packages MUST NEVER depend on or import other feature modules/packages directly (`:feature:explore` MUST NOT import `:feature:detail`).
   - If Feature A needs a service, contract, or navigation target from Feature B, the contract interface MUST live in a lower shared core module (`:core:model` or `:core:data`). Feature B implements the contract interface, and Feature A interacts only with the interface contract.

3. **`:core:model`**: Pure Kotlin data models (`Property`, `Agent`, `Inquiry`, `PropertyType`, `FilterState`). Zero Android dependencies.
4. **`:core:designsystem`**: M3 Theme, JoyMu color tokens, icons, and reusable components (`PropertyCard`, `SearchBarHeader`, `CategoryChipRow`, `FilterBottomSheet`).
5. **`:core:network`**: Supabase Client configuration, `NetworkResult`, `safeApiCall`, and shared network DTOs.
6. **`:core:data`**: Core shared repositories (`PropertyRepository`) implementing offline-first caching and Supabase sync.
7. **`:feature:*`**: Individual feature packages containing ViewModels, UI Screens, feature-specific repositories, and Navigation destinations.
8. **`:app`**: Root application module wiring Hilt `@HiltAndroidApp`, Navigation graph, and main activity entry point.

---

## Coding Conventions
- **Kotlin Coroutines & Flow**: Use `StateFlow` for state, `SharedFlow`/`Channel` for one-off effects.
- **Hilt Dependency Injection**: All ViewModels annotated with `@HiltViewModel @Inject constructor(...)`.
- **Formatting**: Strict trailing commas, descriptive function names, and `strings.xml` resource references.
