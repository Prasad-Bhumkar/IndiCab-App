 package com.example.indicab.components
 
 import androidx.compose.animation.*
 import androidx.compose.foundation.clickable
 import androidx.compose.foundation.layout.*
 import androidx.compose.foundation.lazy.LazyColumn
 import androidx.compose.foundation.lazy.items
 import androidx.compose.foundation.text.KeyboardOptions
 import androidx.compose.material.icons.Icons
 import androidx.compose.material.icons.filled.*
 import androidx.compose.material3.*
 import androidx.compose.runtime.*
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.text.input.KeyboardType
 import androidx.compose.ui.unit.dp
 import com.example.indicab.models.*
 import java.text.NumberFormat
 import java.util.*
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 fun PaymentMethodSelector(
     paymentMethods: List<PaymentMethod>,
     selectedPaymentMethod: PaymentMethod?,
     walletBalance: Wallet?,
     amount: Double,
     onPaymentMethodSelected: (PaymentMethod) -> Unit,
     onAddPaymentMethod: () -> Unit,
     onProcessPayment: () -> Unit,
     modifier: Modifier = Modifier
 ) {
     var showAddPaymentDialog by remember { mutableStateOf(false) }
     val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
 
     Column(
         modifier = modifier
             .fillMaxWidth()
             .padding(16.dp)
     ) {
         // Amount Display
         Card(
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(bottom = 16.dp)
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
                     text = currencyFormat.format(amount),
                     style = MaterialTheme.typography.headlineMedium,
                     color = MaterialTheme.colorScheme.primary
                 )
             }
         }
 
         // Wallet Balance (if available)
         walletBalance?.let { wallet ->
             Card(
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(bottom = 16.dp),
                 colors = CardDefaults.cardColors(
                     containerColor = MaterialTheme.colorScheme.secondaryContainer
                 )
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
                             text = "Wallet Balance",
                             style = MaterialTheme.typography.titleSmall
                         )
                         Text(
                             text = currencyFormat.format(wallet.balance),
                             style = MaterialTheme.typography.bodyLarge
                         )
                     }
                     if (wallet.balance >= amount) {
                         Button(
                             onClick = {
                                 paymentMethods.find { it.type == PaymentMethodType.WALLET }
                                     ?.let(onPaymentMethodSelected)
                             }
                         ) {
                             Text("Use Wallet")
                         }
                     }
                 }
             }
         }
 
         // Payment Methods List
         Text(
             text = "Select Payment Method",
             style = MaterialTheme.typography.titleMedium,
             modifier = Modifier.padding(vertical = 8.dp)
         )
 
         LazyColumn(
             verticalArrangement = Arrangement.spacedBy(8.dp)
         ) {
             items(paymentMethods) { paymentMethod ->
                 PaymentMethodCard(
                     paymentMethod = paymentMethod,
                     isSelected = paymentMethod == selectedPaymentMethod,
                     onClick = { onPaymentMethodSelected(paymentMethod) }
                 )
             }
 
             item {
                 OutlinedCard(
                     modifier = Modifier
                         .fillMaxWidth()
                         .clickable { showAddPaymentDialog = true }
                 ) {
                     Row(
                         modifier = Modifier
                             .fillMaxWidth()
                             .padding(16.dp),
                         horizontalArrangement = Arrangement.Center,
                         verticalAlignment = Alignment.CenterVertically
                     ) {
                         Icon(
                             imageVector = Icons.Default.Add,
                             contentDescription = "Add Payment Method"
                         )
                         Spacer(modifier = Modifier.width(8.dp))
                         Text("Add New Payment Method")
                     }
                 }
             }
         }
 
         // Pay Button
         Button(
             onClick = onProcessPayment,
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(vertical = 16.dp),
             enabled = selectedPaymentMethod != null
         ) {
             Text("Pay ${currencyFormat.format(amount)}")
         }
     }
 
     // Add Payment Method Dialog
     if (showAddPaymentDialog) {
         AddPaymentMethodDialog(
             onDismiss = { showAddPaymentDialog = false },
             onAddPaymentMethod = { type, name, details ->
                 onAddPaymentMethod()
                 showAddPaymentDialog = false
             }
         )
     }
 }
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 private fun PaymentMethodCard(
     paymentMethod: PaymentMethod,
     isSelected: Boolean,
     onClick: () -> Unit
 ) {
     Card(
         modifier = Modifier
             .fillMaxWidth()
             .clickable(onClick = onClick),
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
             horizontalArrangement = Arrangement.SpaceBetween,
             verticalAlignment = Alignment.CenterVertically
         ) {
             Row(
                 verticalAlignment = Alignment.CenterVertically
             ) {
                 Icon(
                     imageVector = when (paymentMethod.type) {
                         PaymentMethodType.CREDIT_CARD -> Icons.Default.CreditCard
                         PaymentMethodType.DEBIT_CARD -> Icons.Default.CreditCard
                         PaymentMethodType.UPI -> Icons.Default.Phone
                         PaymentMethodType.NET_BANKING -> Icons.Default.AccountBalance
                         PaymentMethodType.WALLET -> Icons.Default.AccountBalanceWallet
                         PaymentMethodType.CASH -> Icons.Default.Money
                     },
                     contentDescription = paymentMethod.type.name
                 )
                 Spacer(modifier = Modifier.width(16.dp))
                 Column {
                     Text(
                         text = paymentMethod.name,
                         style = MaterialTheme.typography.titleSmall
                     )
                     when (paymentMethod.type) {
                         PaymentMethodType.CREDIT_CARD,
                         PaymentMethodType.DEBIT_CARD -> {
                             Text(
                                 text = "•••• ${paymentMethod.details.cardNumber?.takeLast(4)}",
                                 style = MaterialTheme.typography.bodySmall
                             )
                         }
                         PaymentMethodType.UPI -> {
                             Text(
                                 text = paymentMethod.details.upiId ?: "",
                                 style = MaterialTheme.typography.bodySmall
                             )
                         }
                         else -> {}
                     }
                 }
             }
             if (isSelected) {
                 Icon(
                     imageVector = Icons.Default.CheckCircle,
                     contentDescription = "Selected",
                     tint = MaterialTheme.colorScheme.primary
                 )
             }
         }
     }
 }
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 private fun AddPaymentMethodDialog(
     onDismiss: () -> Unit,
     onAddPaymentMethod: (PaymentMethodType, String, PaymentMethodDetails) -> Unit
 ) {
     var selectedType by remember { mutableStateOf<PaymentMethodType?>(null) }
     var name by remember { mutableStateOf("") }
     var cardNumber by remember { mutableStateOf("") }
     var cardHolderName by remember { mutableStateOf("") }
     var expiryMonth by remember { mutableStateOf("") }
     var expiryYear by remember { mutableStateOf("") }
     var upiId by remember { mutableStateOf("") }
 
     AlertDialog(
         onDismissRequest = onDismiss,
         title = { Text("Add Payment Method") },
         text = {
             Column(
                 modifier = Modifier.fillMaxWidth(),
                 verticalArrangement = Arrangement.spacedBy(16.dp)
             ) {
                 // Payment Type Selection
                 PaymentMethodType.values().forEach { type ->
                     Row(
                         modifier = Modifier
                             .fillMaxWidth()
                             .clickable { selectedType = type }
                             .padding(8.dp),
                         verticalAlignment = Alignment.CenterVertically
                     ) {
                         RadioButton(
                             selected = selectedType == type,
                             onClick = { selectedType = type }
                         )
                         Spacer(modifier = Modifier.width(8.dp))
                         Text(type.name)
                     }
                 }
 
                 // Dynamic Form Based on Type
                 selectedType?.let { type ->
                     OutlinedTextField(
                         value = name,
                         onValueChange = { name = it },
                         label = { Text("Name") },
                         modifier = Modifier.fillMaxWidth()
                     )
 
                     when (type) {
                         PaymentMethodType.CREDIT_CARD,
                         PaymentMethodType.DEBIT_CARD -> {
                             OutlinedTextField(
                                 value = cardNumber,
                                 onValueChange = { cardNumber = it },
                                 label = { Text("Card Number") },
                                 keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                 modifier = Modifier.fillMaxWidth()
                             )
                             OutlinedTextField(
                                 value = cardHolderName,
                                 onValueChange = { cardHolderName = it },
                                 label = { Text("Card Holder Name") },
                                 modifier = Modifier.fillMaxWidth()
                             )
                             Row(
                                 modifier = Modifier.fillMaxWidth(),
                                 horizontalArrangement = Arrangement.spacedBy(8.dp)
                             ) {
                                 OutlinedTextField(
                                     value = expiryMonth,
                                     onValueChange = { expiryMonth = it },
                                     label = { Text("MM") },
                                     keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                     modifier = Modifier.weight(1f)
                                 )
                                 OutlinedTextField(
                                     value = expiryYear,
                                     onValueChange = { expiryYear = it },
                                     label = { Text("YY") },
                                     keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                     modifier = Modifier.weight(1f)
                                 )
                             }
                         }
                         PaymentMethodType.UPI -> {
                             OutlinedTextField(
                                 value = upiId,
                                 onValueChange = { upiId = it },
                                 label = { Text("UPI ID") },
                                 modifier = Modifier.fillMaxWidth()
                             )
                         }
                         else -> {}
                     }
                 }
             }
         },
         confirmButton = {
             Button(
                 onClick = {
                     selectedType?.let { type ->
                         val details = when (type) {
                             PaymentMethodType.CREDIT_CARD,
                             PaymentMethodType.DEBIT_CARD -> PaymentMethodDetails(
                                 cardNumber = cardNumber,
                                 cardHolderName = cardHolderName,
                                 expiryMonth = expiryMonth.toIntOrNull(),
                                 expiryYear = expiryYear.toIntOrNull()
                             )
                             PaymentMethodType.UPI -> PaymentMethodDetails(
                                 upiId = upiId
                             )
                             else -> PaymentMethodDetails()
                         }
                         onAddPaymentMethod(type, name, details)
                     }
                 },
                 enabled = selectedType != null && name.isNotBlank()
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
