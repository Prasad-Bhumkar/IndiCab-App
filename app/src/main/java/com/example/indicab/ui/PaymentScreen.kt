 package com.example.indicab.ui
 
 import android.widget.Toast
 import androidx.compose.foundation.layout.*
 import androidx.compose.material3.*
 import androidx.compose.runtime.*
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.platform.LocalContext
 import androidx.compose.ui.unit.dp
 import androidx.navigation.NavController
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 fun PaymentScreen(navController: NavController, amount: Double = 100.0) {
     var selectedPaymentMethod by remember { mutableStateOf("") }
     var isPaymentMethodMenuExpanded by remember { mutableStateOf(false) }
     val context = LocalContext.current
 
     Column(
         modifier = Modifier
             .fillMaxSize()
             .padding(16.dp),
         verticalArrangement = Arrangement.spacedBy(16.dp)
     ) {
         // Payment Amount Card
         Surface(
             modifier = Modifier.fillMaxWidth(),
             color = MaterialTheme.colorScheme.primaryContainer,
             shape = MaterialTheme.shapes.medium
         ) {
             Column(
                 modifier = Modifier.padding(16.dp),
                 horizontalAlignment = Alignment.CenterHorizontally
             ) {
                 Text(
                     text = "Total Amount",
                     style = MaterialTheme.typography.titleMedium
                 )
                 Text(
                     text = "₹${String.format("%.2f", amount)}",
                     style = MaterialTheme.typography.headlineMedium
                 )
             }
         }
 
         // Payment Method Selector
         ExposedDropdownMenuBox(
             expanded = isPaymentMethodMenuExpanded,
             onExpandedChange = { isPaymentMethodMenuExpanded = it }
         ) {
             TextField(
                 value = selectedPaymentMethod,
                 onValueChange = {},
                 readOnly = true,
                 label = { Text("Select Payment Method") },
                 trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPaymentMethodMenuExpanded) },
                 modifier = Modifier.menuAnchor().fillMaxWidth(),
                 colors = ExposedDropdownMenuDefaults.textFieldColors()
             )
 
             ExposedDropdownMenu(
                 expanded = isPaymentMethodMenuExpanded,
                 onDismissRequest = { isPaymentMethodMenuExpanded = false }
             ) {
                 DropdownMenuItem(
                     text = { Text("Credit/Debit Card") },
                     onClick = {
                         selectedPaymentMethod = "Credit/Debit Card"
                         isPaymentMethodMenuExpanded = false
                     }
                 )
                 DropdownMenuItem(
                     text = { Text("UPI") },
                     onClick = {
                         selectedPaymentMethod = "UPI"
                         isPaymentMethodMenuExpanded = false
                     }
                 )
                 DropdownMenuItem(
                     text = { Text("Net Banking") },
                     onClick = {
                         selectedPaymentMethod = "Net Banking"
                         isPaymentMethodMenuExpanded = false
                     }
                 )
             }
         }
 
         Spacer(modifier = Modifier.weight(1f))
 
         // Pay Button
         Button(
             onClick = { 
                 // Simulate payment success
                 Toast.makeText(context, "Payment Successful!", Toast.LENGTH_SHORT).show()
                 // Navigate to success screen or back
                 navController.popBackStack()
             },
             modifier = Modifier
                 .fillMaxWidth()
                 .height(56.dp),
             enabled = selectedPaymentMethod.isNotEmpty()
         ) {
             Text("Pay Now")
         }
     }
 } 