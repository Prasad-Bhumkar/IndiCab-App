package com.example.indicab.navigation

import androidx.lifecycle.ViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeController: ThemeController
) : ViewModel() {
    private val _themeState = MutableStateFlow<ThemeState>(ThemeState.Loading)
    val themeState = _themeState.asStateFlow()
    val themePreference = themeController.themePreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemePreference()
        )
    
    init {
        observeThemePreferences()
    }

    private fun observeThemePreferences() {
        viewModelScope.launch {
            themeController.themePreferencesFlow
                .catch { e ->
                    _themeState.value = ThemeState.Error(
                        e.message ?: "Failed to load theme preferences"
                    )
                }
                .collect { preferences ->
                    _themeState.value = ThemeState.Success(preferences)
                }
        }
    }

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            try {
                themeController.setThemeMode(themeMode)
            } catch (e: Exception) {
                _themeState.value = ThemeState.Error(
                    e.message ?: "Failed to update theme mode"
                )
            }
        }
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                themeController.setDynamicColorEnabled(enabled)
            } catch (e: Exception) {
                _themeState.value = ThemeState.Error(
                    e.message ?: "Failed to update dynamic color setting"
                )
            }
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            try {
                themeController.resetToDefaults()
            } catch (e: Exception) {
                _themeState.value = ThemeState.Error(
                    e.message ?: "Failed to reset theme settings"
                )
            }
        }
    }

    fun shouldUseDarkTheme(preference: ThemePreference = themePreference.value): Boolean {
        return themeController.shouldUseDarkTheme(preference)
    }

    class Factory @Inject constructor(
        private val themeController: ThemeController
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
                return ThemeViewModel(themeController) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

sealed class ThemeState {
    object Loading : ThemeState()
    data class Success(val preferences: ThemePreference) : ThemeState()
    data class Error(val message: String) : ThemeState()
}

sealed class ThemeEvent {
    data class ThemeModeChanged(val mode: ThemeMode) : ThemeEvent()
    data class DynamicColorToggled(val enabled: Boolean) : ThemeEvent()
    object ResetToDefaults : ThemeEvent()
    data class ShowError(val message: String) : ThemeEvent()
}

data class ThemeUiState(
    val currentThemeMode: ThemeMode = ThemeMode.SYSTEM,
    val isDynamicColorEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    companion object {
        fun fromPreference(preference: ThemePreference) = ThemeUiState(
            currentThemeMode = preference.themeMode,
            isDynamicColorEnabled = preference.isDynamicColorEnabled
        )
    }
}
