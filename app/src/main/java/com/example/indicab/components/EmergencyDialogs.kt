package com.example.indicab.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.indicab.models.EmergencySettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, phone: String, relationship: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf(false) }

    LaunchedEffect(name, phone, relationship) {
        isValid = name.isNotBlank() && 
                 phone.isNotBlank() && 
                 relationship.isNotBlank() &&
                 phone.matches(Regex("^[+]?[0-9]{10,13}$"))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Emergency Contact",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardType = KeyboardType.Phone,
                    singleLine = true
                )

                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    label = { Text("Relationship") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(name, phone, relationship) },
                enabled = isValid
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencySettingsDialog(
    settings: EmergencySettings?,
    onDismiss: () -> Unit,
    onUpdate: (EmergencySettings) -> Unit
) {
    var sosEnabled by remember(settings) { 
        mutableStateOf(settings?.sosEnabled ?: true) 
    }
    var autoSOS by remember(settings) { 
        mutableStateOf(settings?.autoSOS ?: true) 
    }
    var notifyContacts by remember(settings) { 
        mutableStateOf(settings?.notifyContacts ?: true) 
    }
    var notifyAuthorities by remember(settings) { 
        mutableStateOf(settings?.notifyAuthorities ?: true) 
    }
    var safetyChecksEnabled by remember(settings) { 
        mutableStateOf(settings?.safetyChecksEnabled ?: true) 
    }
    var autoSOSDelay by remember(settings) { 
        mutableStateOf(settings?.autoSOSDelay?.toString() ?: "30") 
    }
    var safetyCheckInterval by remember(settings) { 
        mutableStateOf(settings?.safetyCheckInterval?.toString() ?: "30") 
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Emergency Settings",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // SOS Settings
                SettingsSection(
                    title = "SOS Settings"
                ) {
                    SettingsSwitch(
                        title = "Enable SOS",
                        subtitle = "Allow triggering emergency alerts",
                        checked = sosEnabled,
                        onCheckedChange = { sosEnabled = it }
                    )

                    SettingsSwitch(
                        title = "Auto SOS",
                        subtitle = "Automatically trigger SOS in critical situations",
                        checked = autoSOS,
                        onCheckedChange = { autoSOS = it }
                    )

                    if (autoSOS) {
                        OutlinedTextField(
                            value = autoSOSDelay,
                            onValueChange = { autoSOSDelay = it },
                            label = { Text("Auto SOS Delay (seconds)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardType = KeyboardType.Number,
                            singleLine = true
                        )
                    }
                }

                // Notification Settings
                SettingsSection(
                    title = "Notification Settings"
                ) {
                    SettingsSwitch(
                        title = "Notify Emergency Contacts",
                        subtitle = "Send alerts to emergency contacts",
                        checked = notifyContacts,
                        onCheckedChange = { notifyContacts = it }
                    )

                    SettingsSwitch(
                        title = "Notify Authorities",
                        subtitle = "Alert emergency services",
                        checked = notifyAuthorities,
                        onCheckedChange = { notifyAuthorities = it }
                    )
                }

                // Safety Check Settings
                SettingsSection(
                    title = "Safety Check Settings"
                ) {
                    SettingsSwitch(
                        title = "Enable Safety Checks",
                        subtitle = "Periodic safety verification",
                        checked = safetyChecksEnabled,
                        onCheckedChange = { safetyChecksEnabled = it }
                    )

                    if (safetyChecksEnabled) {
                        OutlinedTextField(
                            value = safetyCheckInterval,
                            onValueChange = { safetyCheckInterval = it },
                            label = { Text("Check Interval (minutes)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardType = KeyboardType.Number,
                            singleLine = true
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    settings?.let {
                        onUpdate(
                            it.copy(
                                sosEnabled = sosEnabled,
                                autoSOS = autoSOS,
                                autoSOSDelay = autoSOSDelay.toIntOrNull() ?: 30,
                                notifyContacts = notifyContacts,
                                notifyAuthorities = notifyAuthorities,
                                safetyChecksEnabled = safetyChecksEnabled,
                                safetyCheckInterval = safetyCheckInterval.toIntOrNull() ?: 30
                            )
                        )
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        content()
    }
}

@Composable
private fun SettingsSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
