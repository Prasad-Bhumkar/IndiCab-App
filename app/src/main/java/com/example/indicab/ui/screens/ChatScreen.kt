package com.example.indicab.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.indicab.models.*
import com.example.indicab.viewmodels.ChatUIState
import com.example.indicab.viewmodels.ChatViewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    bookingId: String,
    chatViewModel: ChatViewModel = viewModel(),
    attachFile: () -> Unit // Attach function added to handle file attachment
) {
    val chatState by chatViewModel.chatState.collectAsState()
    val messages by chatViewModel.messages.collectAsState()
    val participants by chatViewModel.participants.collectAsState()
    val typingUsers by chatViewModel.typingUsers.collectAsState()
    
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            ChatTopBar(
                participants = participants,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            ChatInput(
                text = messageText,
                onTextChange = { 
                    messageText = it
                    chatViewModel.setTyping(it.isNotEmpty())
                },
                onSendClick = {
                    if (messageText.isNotBlank()) {
                        chatViewModel.sendMessage(messageText)
                        messageText = ""
                    }
                },
                onAttachmentClick = {
                    attachFile()
                },
                onMenuClick = { /* Implementing menu options */ }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (chatState) {
                is ChatUIState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ChatUIState.Error -> {
                    ErrorView(
                        error = (chatState as ChatUIState.Error).message,
                        onRetryClick = { chatViewModel.retryConnection() }
                    )
                }
                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Connection status bar logic
                        AnimatedVisibility(
                            visible = chatState is ChatUIState.Disconnected || 
                                     chatState is ChatUIState.Reconnecting,
                            enter = slideInVertically() + fadeIn(),
                            exit = slideOutVertically() + fadeOut()
                        ) {
                            ConnectionStatusBar(chatState)
                        }

                        // Messages
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(
                                items = messages,
                                key = { it.id }
                            ) { message ->
                                MessageItem(message = message, isSentByUser = message.senderId == chatViewModel.userId)
                            }
                        }

                        // Typing indicator logic
                        AnimatedVisibility(
                            visible = typingUsers.isNotEmpty(),
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            TypingIndicator(typingUsers)
                        }
                    }
                }
            }
        }
    }
}

// Other functions continue here as needed...
