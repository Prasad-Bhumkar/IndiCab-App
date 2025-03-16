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
 import com.example.indicab.components.PaymentMethodSelector
 import com.example.indicab.models.*
 import com.example.indicab.viewmodels.PaymentViewModel
 import java.text.NumberFormat
 import java.time.format.DateTimeFormatter
 import java.util.*
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 fun PaymentScreen(
     navController: NavController,
     bookingId: String? = null,
     amount: Double,
     paymentViewModel: PaymentViewModel = viewModel()
 ) {
     val paymentState by paymentViewModel.paymentState.collectAsState()
     val selectedPaymentMethod by paymentViewModel.selectedPaymentMethod.collectAsState()
     val activePaymentMethods by paymentViewModel.activePaymentMethods.collectAsState()
     val wallet by paymentViewModel.wallet.collectAsState()
     val recentTransactions by paymentViewModel.recentTransactions.collectAsState()
 
     var showTransactionHistory by remember { mutableStateOf(false) }
     var showErrorDialog by remember { mutableStateOf(false) }
     var errorMessage by remember { mutableStateOf("") }
 
     LaunchedEffect(paymentState) {
         when (paymentState) {
             is PaymentState.Error -> {
                 errorMessage = (paymentState as PaymentState.Error).message
                 showErrorDialog = true
             }
             is PaymentState.Success -> {
                 // Navigate back to home or booking confirmation
                 navController.popBackStack()
             }
             else -> {}
         }
     }
 
     Scaffold(
         topBar = {
             TopAppBar(
                 title = { Text("Payment") },
                 navigationIcon = {
                     IconButton(onClick = { navController.popBackStack() }) {
                         Icon(Icons.Default.ArrowBack, "Back")
                     }
                 },
                 actions = {
                     IconButton(onClick = { showTransactionHistory = !showTransactionHistory }) {
                         Icon(
                             if (showTransactionHistory) Icons.Default.Payment else Icons.Default.History,
                             "Toggle History"
                         )
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
             AnimatedContent(
                 targetState = showTransactionHistory,
                 transitionSpec = {
                     fadeIn() with fadeOut()
                 }
             ) { showHistory ->
                 if (showHistory) {
                     TransactionHistoryView(
                         transactions = recentTransactions,
                         modifier = Modifier.fillMaxSize()
                     )
                 } else {
                     PaymentMethodSelector(
                         paymentMethods = activePaymentMethods,
                         selectedPaymentMethod = selectedPaymentMethod,
                         walletBalance = wallet,
                         amount = amount,
                         onPaymentMethodSelected = { paymentViewModel.selectPaymentMethod(it) },
                         onAddPaymentMethod = { type, name, details ->
                             paymentViewModel.addPaymentMethod(type, name, details)
                         },
                         onProcessPayment = {
                             paymentViewModel.processPayment(amount, bookingId)
                         },
                         modifier = Modifier.fillMaxSize()
                     )
                 }
             }
 
             // Loading Indicator
             if (paymentState is PaymentState.Processing) {
                 CircularProgressIndicator(
                     modifier = Modifier.align(Alignment.Center)
                 )
             }
         }
     }
 
     // Error Dialog
     if (showErrorDialog) {
         AlertDialog(
             onDismissRequest = { showErrorDialog = false },
             title = { Text("Payment Failed") },
             text = { Text(errorMessage) },
             confirmButton = {
                 TextButton(onClick = { showErrorDialog = false }) {
                     Text("OK")
                 }
             }
         )
     }
 }
 
 @Composable
 private fun TransactionHistoryView(
     transactions: List<Transaction>,
     modifier: Modifier = Modifier
 ) {
     val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
     val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm") }
 
     if (transactions.isEmpty()) {
         Box(
             modifier = modifier.padding(16.dp),
             contentAlignment = Alignment.Center
         ) {
             Text(
                 text = "No transactions yet",
                 style = MaterialTheme.typography.bodyLarge,
                 textAlign = TextAlign.Center
             )
         }
     } else {
         LazyColumn(
             modifier = modifier.padding(16.dp),
             verticalArrangement = Arrangement.spacedBy(8.dp)
         ) {
             items(transactions) { transaction ->
                 TransactionCard(
                     transaction = transaction,
                     currencyFormat = currencyFormat,
                     dateFormatter = dateFormatter
                 )
             }
         }
     }
 }
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 private fun TransactionCard(
     transaction: Transaction,
     currencyFormat: NumberFormat,
     dateFormatter: DateTimeFormatter
 ) {
     Card(
         modifier = Modifier.fillMaxWidth()
     ) {
         Column(
             modifier = Modifier.padding(16.dp)
         ) {
             Row(
                 modifier = Modifier.fillMaxWidth(),
                 horizontalArrangement = Arrangement.SpaceBetween,
                 verticalAlignment = Alignment.CenterVertically
             ) {
                 Column {
                     Text(
                         text = transaction.type.name,
                         style = MaterialTheme.typography.titleMedium
                     )
                     Text(
                         text = transaction.createdAt.format(dateFormatter),
                         style = MaterialTheme.typography.bodySmall
                     )
                 }
                 Text(
                     text = currencyFormat.format(transaction.amount),
                     style = MaterialTheme.typography.titleMedium,
                     color = when {
                         transaction.type == TransactionType.REFUND -> MaterialTheme.colorScheme.error
                         transaction.status == TransactionStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                         else -> MaterialTheme.colorScheme.onSurface
                     }
                 )
             }
             
             Spacer(modifier = Modifier.height(8.dp))
             
             Row(
                 modifier = Modifier.fillMaxWidth(),
                 horizontalArrangement = Arrangement.SpaceBetween,
                 verticalAlignment = Alignment.CenterVertically
             ) {
                 Chip(
                     onClick = {},
                     enabled = false,
                     colors = ChipDefaults.chipColors(
                         containerColor = when (transaction.status) {
                             TransactionStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
                             TransactionStatus.FAILED -> MaterialTheme.colorScheme.errorContainer
                             else -> MaterialTheme.colorScheme.surfaceVariant
                         }
                     )
                 ) {
                     Text(transaction.status.name)
                 }
                 
                 if (transaction.bookingId != null) {
                     Text(
                         text = "Booking: ${transaction.bookingId}",
                         style = MaterialTheme.typography.bodySmall
                     )
                 }
             }
         }
     }
 }
