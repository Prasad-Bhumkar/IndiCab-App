 package com.example.indicab.ui.screens
 
 import androidx.compose.foundation.layout.*
 import androidx.compose.material3.*
 import androidx.compose.runtime.*
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.unit.dp
 import com.example.indicab.models.DriverDocument
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 fun DriverDocumentsScreen(
     documents: List<DriverDocument>,
     onBack: () -> Unit
 ) {
     Scaffold(
         topBar = {
             TopAppBar(
                 title = { Text("Driver Documents") },
                 navigationIcon = {
                     IconButton(onClick = onBack) {
                         Icon(Icons.Default.ArrowBack, "Back")
                     }
                 }
             )
         }
     ) { padding ->
         Column(
             modifier = Modifier
                 .fillMaxSize()
                 .padding(padding)
                 .padding(16.dp),
             verticalArrangement = Arrangement.spacedBy(16.dp)
         ) {
             Text(
                 text = "Documents Overview",
                 style = MaterialTheme.typography.titleLarge
             )
 
             if (documents.isEmpty()) {
                 Text("No documents available.")
             } else {
                 documents.forEach { document ->
                     DocumentCard(document = document)
                 }
             }
         }
     }
 }
 
 @Composable
 private fun DocumentCard(document: DriverDocument) {
     Card(
         modifier = Modifier.fillMaxWidth(),
         elevation = CardDefaults.cardElevation(4.dp)
     ) {
         Column(
             modifier = Modifier.padding(16.dp),
             verticalArrangement = Arrangement.spacedBy(8.dp)
         ) {
             Text(
                 text = "Document Type: ${document.type}",
                 style = MaterialTheme.typography.titleMedium
             )
             Text("Document Number: ${document.number}")
             Text("Issued By: ${document.issuedBy}")
             Text("Issued Date: ${document.issuedDate}")
             Text("Expiry Date: ${document.expiryDate}")
 
             // Additional document details can be added here
         }
     }
 }
