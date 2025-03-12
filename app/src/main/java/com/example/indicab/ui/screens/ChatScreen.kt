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
    chatViewModel: ChatViewModel = viewModel()
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
                    // TODO: Implement attachment handling
                },
                modifier = Modifier.navigationBarsPadding()
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
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ChatUIState.Error -> {
                    ErrorView(
                        error = (chatState as ChatUIState.Error).message,
                        onRetryClick = { chatViewModel.retryConnection() }
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Connection status bar
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
                            reverseLayout = false,
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(
                                items = messages,
                                key = { it.id }
                            ) { message ->
                                MessageItem(
                                    message = message,
                                    isSentByUser = message.senderId == "USER_ID", // TODO: Get actual user ID
                                    onMessageClick = { /* Handle message click */ }
                                )
                            }
                        }

                        // Typing indicator
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    participants: List<ChatParticipant>,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Chat",
                    style = MaterialTheme.typography.titleMedium
                )
                if (participants.isNotEmpty()) {
                    Text(
                        text = participants.joinToString { it.userId },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
        },
        actions = {
            IconButton(onClick = { /* TODO: Show menu */ }) {
                Icon(Icons.Default.MoreVert, "More options")
            }
        }
    )
}

@Composable
private fun ChatInput(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onAttachmentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        tonalElevation = 2.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onAttachmentClick) {
                Icon(Icons.Default.AttachFile, "Attach")
            }
            
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message") },
                maxLines = 4
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            IconButton(
                onClick = onSendClick,
                enabled = text.isNotBlank()
            ) {
                Icon(Icons.Default.Send, "Send")
            }
        }
    }
}

@Composable
private fun MessageItem(
    message: Message,
    isSentByUser: Boolean,
    onMessageClick: () -> Unit
) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isSentByUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isSentByUser) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .widthIn(max = 300.dp)
                .padding(vertical = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                when (message.type) {
                    MessageType.TEXT -> {
                        Text(message.content)
                    }
                    MessageType.LOCATION -> {
                        LocationMessageContent(message)
                    }
                    MessageType.SYSTEM -> {
                        SystemMessageContent(message)
                    }
                    else -> {
                        Text(message.content)
                    }
                }
                
                Text(
                    text = message.createdAt.format(timeFormatter),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun LocationMessageContent(message: Message) {
    Column {
        // TODO: Add map preview
        Text(message.content)
        message.metadata?.let { metadata ->
            if (metadata.latitude != null && metadata.longitude != null) {
                Text(
                    text = "Location: ${metadata.latitude}, ${metadata.longitude}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun SystemMessageContent(message: Message) {
    Text(
        text = message.content,
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun ConnectionStatusBar(state: ChatUIState) {
    Surface(
        color = when (state) {
            is ChatUIState.Disconnected -> MaterialTheme.colorScheme.errorContainer
            is ChatUIState.Reconnecting -> MaterialTheme.colorScheme.tertiaryContainer
            else -> MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (state) {
                    is ChatUIState.Disconnected -> "Connection lost"
                    is ChatUIState.Reconnecting -> "Reconnecting..."
                    else -> ""
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun TypingIndicator(typingUsers: Set<String>) {
    if (typingUsers.isNotEmpty()) {
        Text(
            text = when {
                typingUsers.size == 1 -> "${typingUsers.first()} is typing..."
                typingUsers.size == 2 -> "${typingUsers.first()} and ${typingUsers.last()} are typing..."
                else -> "Several people are typing..."
            },
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun ErrorView(
    error: String,
    onRetryClick: () -> Unit
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
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetryClick) {
            Text("Retry")
        }
    }
}
