package com.example.indicab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.indicab.viewmodels.PaymentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    rideId: String,
    fareAmount: Double,
    onPaymentComplete: () -> Unit,
    onBackPressed: () -> Unit,
    viewModel: PaymentViewModel = viewModel()
) {
    var selectedPaymentMethod by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Fare Amount: ₹$fareAmount",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Select Payment Method",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        PaymentMethodItem("UPI") { selectedPaymentMethod = "UPI" }
                        PaymentMethodItem("Card") { selectedPaymentMethod = "Card" }
                        PaymentMethodItem("Cash") { selectedPaymentMethod = "Cash" }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        selectedPaymentMethod?.let { method ->
                            viewModel.processPayment(rideId, fareAmount, method)
                            onPaymentComplete()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedPaymentMethod != null
                ) {
                    Text("Pay Now")
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodItem(
    method: String,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = false,
            onClick = onSelect
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(method)
    }
}
