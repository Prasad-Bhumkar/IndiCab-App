package com.example.indicab.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.indicab.ui.theme.ThemeMode
import com.example.indicab.viewmodels.ThemeState
import com.example.indicab.viewmodels.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel = viewModel()
) {
    val themeState by themeViewModel.themeState.collectAsState()
    val themePreference by themeViewModel.themePreference.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Theme Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(Icons.Default.Refresh, "Reset to defaults")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (themeState) {
                is ThemeState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ThemeState.Error -> {
                    ErrorView(
                        error = (themeState as ThemeState.Error).message,
                        onRetry = { /* Implement retry logic */ }
                    )
                }
                is ThemeState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Theme Mode Selection
                        ThemeModeSection(
                            currentMode = themePreference.themeMode,
                            onModeSelected = { themeViewModel.setThemeMode(it) }
                        )

                        Divider()

                        // Dynamic Color Toggle
                        DynamicColorSection(
                            isDynamicColorEnabled = themePreference.isDynamicColorEnabled,
                            onToggle = { themeViewModel.setDynamicColorEnabled(it) }
                        )

                        Divider()

                        // Preview Section
                        ThemePreviewSection(
                            isDarkTheme = themeViewModel.shouldUseDarkTheme(themePreference)
                        )
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Theme Settings") },
            text = { Text("Are you sure you want to reset all theme settings to their default values?") },
            confirmButton = {
                Button(
                    onClick = {
                        themeViewModel.resetToDefaults()
                        showResetDialog = false
                    }
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ThemeModeSection(
    currentMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Theme Mode",
            style = MaterialTheme.typography.titleMedium
        )

        Column {
            ThemeMode.values().forEach { mode ->
                ThemeModeOption(
                    mode = mode,
                    isSelected = mode == currentMode,
                    onSelect = { onModeSelected(mode) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeModeOption(
    mode: ThemeMode,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (mode) {
                    ThemeMode.LIGHT -> Icons.Default.LightMode
                    ThemeMode.DARK -> Icons.Default.DarkMode
                    ThemeMode.SYSTEM -> Icons.Default.Settings
                },
                contentDescription = null
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when (mode) {
                        ThemeMode.LIGHT -> "Light"
                        ThemeMode.DARK -> "Dark"
                        ThemeMode.SYSTEM -> "System Default"
                    },
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = when (mode) {
                        ThemeMode.LIGHT -> "Always use light theme"
                        ThemeMode.DARK -> "Always use dark theme"
                        ThemeMode.SYSTEM -> "Follow system theme"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun DynamicColorSection(
    isDynamicColorEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Dynamic Color",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Use dynamic colors",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Apply system accent colors to the theme",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = isDynamicColorEnabled,
                onCheckedChange = onToggle
            )
        }
    }
}

@Composable
private fun ThemePreviewSection(
    isDarkTheme: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Preview",
            style = MaterialTheme.typography.titleMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Theme Preview",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "This is how your selected theme will look.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(onClick = { /* Preview button */ }) {
                    Text("Primary Button")
                }
                OutlinedButton(onClick = { /* Preview button */ }) {
                    Text("Secondary Button")
                }
                TextButton(onClick = { /* Preview button */ }) {
                    Text("Text Button")
                }
            }
        }
    }
}

@Composable
private fun ErrorView(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
