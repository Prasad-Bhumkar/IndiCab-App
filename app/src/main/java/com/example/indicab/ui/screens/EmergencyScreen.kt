 package com.example.indicab.ui.screens
 
 import androidx.compose.animation.*
 import androidx.compose.foundation.layout.*
 import androidx.compose.foundation.lazy.LazyColumn
 import androidx.compose.foundation.lazy.items
 import androidx.compose.material.icons.Icons
 import androidx.compose.material.icons.filled.*
 import androidx.compose.material3.*
 import androidx.compose.runtime.*
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.text.style.TextAlign
 import androidx.compose.ui.unit.dp
 import androidx.lifecycle.viewmodel.compose.viewModel
 import androidx.navigation.NavController
 import com.example.indicab.components.SOSButton
 import com.example.indicab.models.*
 import com.example.indicab.viewmodels.EmergencyState
 import com.example.indicab.viewmodels.EmergencyViewModel
 import java.time.format.DateTimeFormatter
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 fun EmergencyScreen(
     navController: NavController,
     bookingId: String? = null,
     emergencyViewModel: EmergencyViewModel = viewModel()
 ) {
     val emergencyState by emergencyViewModel.emergencyState.collectAsState()
     val emergencyContacts by emergencyViewModel.emergencyContacts.collectAsState()
     val activeSOSAlerts by emergencyViewModel.activeSOSAlerts.collectAsState()
     val emergencySettings by emergencyViewModel.emergencySettings.collectAsState()
 
     var showContactDialog by remember { mutableStateOf(false) }
     var showSettingsDialog by remember { mutableStateOf(false) }
 
     Scaffold(
         topBar = {
             TopAppBar(
                 title = { Text("Emergency") },
                 navigationIcon = {
                     IconButton(onClick = { navController.popBackStack() }) {
                         Icon(Icons.Default.ArrowBack, "Back")
                     }
                 },
                 actions = {
                     IconButton(onClick = { showSettingsDialog = true }) {
                         Icon(Icons.Default.Settings, "Settings")
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
             when (emergencyState) {
                 is EmergencyState.Processing -> {
                     CircularProgressIndicator(
                         modifier = Modifier.align(Alignment.Center)
                     )
                 }
                 is EmergencyState.Error -> {
                     ErrorView(
                         error = (emergencyState as EmergencyState.Error).message,
                         onRetry = { emergencyViewModel.resetState() }
                     )
                 }
                 else -> {
                     Column(
                         modifier = Modifier
                             .fillMaxSize()
                             .padding(16.dp),
                         verticalArrangement = Arrangement.spacedBy(24.dp)
                     ) {
                         // SOS Button
                         SOSButton(
                             onTrigger = { type, description ->
                                 emergencyViewModel.setEmergencyType(type)
                                 description?.let { emergencyViewModel.setEmergencyDescription(it) }
                                 emergencyViewModel.triggerSOS()
                             },
                             modifier = Modifier.align(Alignment.CenterHorizontally)
                         )
 
                         // Active Alerts
                         if (activeSOSAlerts.isNotEmpty()) {
                             ActiveAlertsSection(
                                 alerts = activeSOSAlerts
                             )
                         }
 
                         // Emergency Contacts
                         EmergencyContactsSection(
                             contacts = emergencyContacts,
                             onAddContact = { showContactDialog = true }
                         )
 
                         // Quick Actions
                         QuickActionsSection(
                             onReportIncident = {
                                 // Navigate to incident report screen
                             },
                             onShareLocation = {
                                 emergencyViewModel.shareLocation(
                                     recipientType = ShareRecipientType.CONTACT
                                 )
                             }
                         )
                     }
                 }
             }
         }
     }
 
     // Add Contact Dialog
     if (showContactDialog) {
         AddContactDialog(
             onDismiss = { showContactDialog = false },
             onAdd = { name, phone, relationship ->
                 emergencyViewModel.addEmergencyContact(
                     name = name,
                     phone = phone,
                     relationship = relationship
                 )
                 showContactDialog = false
             }
         )
     }
 
     // Settings Dialog
     if (showSettingsDialog) {
         EmergencySettingsDialog(
             settings = emergencySettings,
             onDismiss = { showSettingsDialog = false },
             onUpdate = { settings ->
                 emergencyViewModel.updateEmergencySettings(
                     sosEnabled = settings.sosEnabled,
                     autoSOS = settings.autoSOS,
                     notifyContacts = settings.notifyContacts,
                     notifyAuthorities = settings.notifyAuthorities,
                     safetyChecksEnabled = settings.safetyChecksEnabled
                 )
                 showSettingsDialog = false
             }
         )
     }
 }
 
 @Composable
 private fun ActiveAlertsSection(
     alerts: List<SOSAlert>
 ) {
     Column(
         verticalArrangement = Arrangement.spacedBy(8.dp)
     ) {
         Text(
             text = "Active Alerts",
             style = MaterialTheme.typography.titleMedium
         )
 
         alerts.forEach { alert ->
             AlertCard(alert = alert)
         }
     }
 }
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 private fun AlertCard(
     alert: SOSAlert
 ) {
     val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
 
     Card(
         modifier = Modifier.fillMaxWidth(),
         colors = CardDefaults.cardColors(
             containerColor = MaterialTheme.colorScheme.errorContainer
         )
     ) {
         Column(
             modifier = Modifier.padding(16.dp),
             verticalArrangement = Arrangement.spacedBy(8.dp)
         ) {
             Row(
                 horizontalArrangement = Arrangement.SpaceBetween,
                 modifier = Modifier.fillMaxWidth()
             ) {
                 Text(
                     text = alert.type.name,
                     style = MaterialTheme.typography.titleMedium,
                     color = MaterialTheme.colorScheme.onErrorContainer
                 )
                 Text(
                     text = alert.createdAt.format(timeFormatter),
                     style = MaterialTheme.typography.bodySmall,
                     color = MaterialTheme.colorScheme.onErrorContainer
                 )
             }
 
             alert.description?.let {
                 Text(
                     text = it,
                     style = MaterialTheme.typography.bodyMedium,
                     color = MaterialTheme.colorScheme.onErrorContainer
                 )
             }
 
             LinearProgressIndicator(
                 modifier = Modifier.fillMaxWidth(),
                 color = MaterialTheme.colorScheme.error
             )
         }
     }
 }
 
 @Composable
 private fun EmergencyContactsSection(
     contacts: List<EmergencyContact>,
     onAddContact: () -> Unit
 ) {
     Column(
         verticalArrangement = Arrangement.spacedBy(8.dp)
     ) {
         Row(
             modifier = Modifier.fillMaxWidth(),
             horizontalArrangement = Arrangement.SpaceBetween,
             verticalAlignment = Alignment.CenterVertically
         ) {
             Text(
                 text = "Emergency Contacts",
                 style = MaterialTheme.typography.titleMedium
             )
             IconButton(onClick = onAddContact) {
                 Icon(Icons.Default.Add, "Add Contact")
             }
         }
 
         if (contacts.isEmpty()) {
             Text(
                 text = "No emergency contacts added",
                 style = MaterialTheme.typography.bodyMedium,
                 color = MaterialTheme.colorScheme.onSurfaceVariant,
                 textAlign = TextAlign.Center,
                 modifier = Modifier.fillMaxWidth()
             )
         } else {
             LazyColumn(
                 verticalArrangement = Arrangement.spacedBy(8.dp)
             ) {
                 items(contacts) { contact ->
                     ContactCard(contact = contact)
                 }
             }
         }
     }
 }
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 private fun ContactCard(
     contact: EmergencyContact
 ) {
     Card(
         modifier = Modifier.fillMaxWidth()
     ) {
         Row(
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(16.dp),
             horizontalArrangement = Arrangement.SpaceBetween,
             verticalAlignment = Alignment.CenterVertically
         ) {
             Column {
                 Text(
                     text = contact.name,
                     style = MaterialTheme.typography.titleMedium
                 )
                 Text(
                     text = contact.relationship,
                     style = MaterialTheme.typography.bodyMedium,
                     color = MaterialTheme.colorScheme.onSurfaceVariant
                 )
                 Text(
                     text = contact.phone,
                     style = MaterialTheme.typography.bodySmall
                 )
             }
             if (contact.notifyOnSOS) {
                 Icon(
                     Icons.Default.Notifications,
                     "SOS Notifications Enabled",
                     tint = MaterialTheme.colorScheme.primary
                 )
             }
         }
     }
 }
 
 @Composable
 private fun QuickActionsSection(
     onReportIncident: () -> Unit,
     onShareLocation: () -> Unit
 ) {
     Column(
         verticalArrangement = Arrangement.spacedBy(8.dp)
     ) {
         Text(
             text = "Quick Actions",
             style = MaterialTheme.typography.titleMedium
         )
 
         Row(
             modifier = Modifier.fillMaxWidth(),
             horizontalArrangement = Arrangement.spacedBy(16.dp)
         ) {
             Button(
                 onClick = onReportIncident,
                 modifier = Modifier.weight(1f)
             ) {
                 Icon(Icons.Default.Report, null)
                 Spacer(Modifier.width(8.dp))
                 Text("Report Incident")
             }
 
             Button(
                 onClick = onShareLocation,
                 modifier = Modifier.weight(1f)
             ) {
                 Icon(Icons.Default.Share, null)
                 Spacer(Modifier.width(8.dp))
                 Text("Share Location")
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
             textAlign = TextAlign.Center
         )
         Spacer(modifier = Modifier.height(16.dp))
         Button(onClick = onRetry) {
             Text("Retry")
         }
     }
 }
