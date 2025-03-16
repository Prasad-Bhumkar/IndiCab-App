 package com.example.indicab.viewmodels
 
 import androidx.lifecycle.ViewModel
 import androidx.lifecycle.ViewModelProvider
 import androidx.lifecycle.viewModelScope
 import com.example.indicab.models.*
 import com.example.indicab.services.ChatService
 import kotlinx.coroutines.flow.*
 import kotlinx.coroutines.launch
 import java.time.LocalDateTime
 
 class ChatViewModel(
     private val chatService: ChatService,
     private val userId: String,
     private val bookingId: String
 ) : ViewModel() {
 
     private val _chatState = MutableStateFlow<ChatUIState>(ChatUIState.Loading)
     val chatState = _chatState.asStateFlow()
 
     private val _messages = MutableStateFlow<List<Message>>(emptyList())
     val messages = _messages.asStateFlow()
 
     private val _participants = MutableStateFlow<List<ChatParticipant>>(emptyList())
     val participants = _participants.asStateFlow()
 
     private val _typingUsers = MutableStateFlow<Set<String>>(emptySet())
     val typingUsers = _typingUsers.asStateFlow()
 
     private var chatRoom: ChatRoom? = null
     private var typingJob: kotlinx.coroutines.Job? = null
 
     init {
         initializeChat()
     }
 
     private fun initializeChat() {
         viewModelScope.launch {
             try {
                 _chatState.value = ChatUIState.Loading
 
                 // Get or create chat room
                 chatRoom = chatService.getChatRoomForBooking(bookingId)
                     .firstOrNull()
                     ?: createNewChatRoom()
 
                 // Observe messages
                 launch {
                     chatService.getMessages(chatRoom!!.id)
                         .collect { messages ->
                             _messages.value = messages
                         }
                 }
 
                 // Observe participants
                 launch {
                     chatService.getChatParticipants(chatRoom!!.id)
                         .collect { participants ->
                             _participants.value = participants
                             _typingUsers.value = participants
                                 .filter { it.isTyping && it.userId != userId }
                                 .map { it.userId }
                                 .toSet()
                         }
                 }
 
                 // Observe chat state
                 launch {
                     chatService.chatState
                         .collect { state ->
                             _chatState.value = when {
                                 state.error != null -> ChatUIState.Error(state.error)
                                 state.isReconnecting -> ChatUIState.Reconnecting
                                 state.isConnected -> ChatUIState.Connected
                                 else -> ChatUIState.Disconnected
                             }
                         }
                 }
 
                 _chatState.value = ChatUIState.Connected
             } catch (e: Exception) {
                 _chatState.value = ChatUIState.Error(e.message ?: "Failed to initialize chat")
             }
         }
     }
 
     private suspend fun createNewChatRoom(): ChatRoom {
         // In a real app, you would get the driver ID from the booking
         val driverId = "DRIVER_${bookingId}"
         return chatService.createChatRoom(
             bookingId = bookingId,
             riderId = userId,
             driverId = driverId
         )
     }
 
     fun sendMessage(content: String, type: MessageType = MessageType.TEXT) {
         viewModelScope.launch {
             try {
                 chatRoom?.let { room ->
                     chatService.sendMessage(
                         chatRoomId = room.id,
                         senderId = userId,
                         content = content,
                         type = type
                     )
                 }
             } catch (e: Exception) {
                 _chatState.value = ChatUIState.Error("Failed to send message: ${e.message}")
             }
         }
     }
 
     fun sendLocation(latitude: Double, longitude: Double, address: String) {
         viewModelScope.launch {
             try {
                 chatRoom?.let { room ->
                     chatService.sendMessage(
                         chatRoomId = room.id,
                         senderId = userId,
                         content = "Shared location: $address",
                         type = MessageType.LOCATION,
                         metadata = MessageMetadata(
                             latitude = latitude,
                             longitude = longitude,
                             locationAddress = address
                         )
                     )
                 }
             } catch (e: Exception) {
                 _chatState.value = ChatUIState.Error("Failed to send location: ${e.message}")
             }
         }
     }
 
     fun markMessageAsRead(messageId: String) {
         viewModelScope.launch {
             try {
                 chatService.markMessageAsRead(messageId)
             } catch (e: Exception) {
                 // Silently fail as this is not critical
             }
         }
     }
 
     fun setTyping(isTyping: Boolean) {
         typingJob?.cancel()
         typingJob = viewModelScope.launch {
             try {
                 chatRoom?.let { room ->
                     chatService.updateTypingStatus(room.id, userId, isTyping)
                 }
             } catch (e: Exception) {
                 // Silently fail as this is not critical
             }
         }
     }
 
     fun retryConnection() {
         viewModelScope.launch {
             try {
                 chatRoom?.let { room ->
                     chatService.disconnect()
                     initializeChat()
                 }
             } catch (e: Exception) {
                 _chatState.value = ChatUIState.Error("Failed to reconnect: ${e.message}")
             }
         }
     }
 
     fun clearChat() {
         viewModelScope.launch {
             try {
                 chatRoom?.let { room ->
                     chatService.clearChat(room.id)
                 }
             } catch (e: Exception) {
                 _chatState.value = ChatUIState.Error("Failed to clear chat: ${e.message}")
             }
         }
     }
 
     override fun onCleared() {
         super.onCleared()
         viewModelScope.launch {
             chatRoom?.let { room ->
                 chatService.archiveChatRoom(room.id)
             }
         }
     }
 
     class Factory(
         private val chatService: ChatService,
         private val userId: String,
         private val bookingId: String
     ) : ViewModelProvider.Factory {
         @Suppress("UNCHECKED_CAST")
         override fun <T : ViewModel> create(modelClass: Class<T>): T {
             if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                 return ChatViewModel(chatService, userId, bookingId) as T
             }
             throw IllegalArgumentException("Unknown ViewModel class")
         }
     }
 }
 
 sealed class ChatUIState {
     object Loading : ChatUIState()
     object Connected : ChatUIState()
     object Disconnected : ChatUIState()
     object Reconnecting : ChatUIState()
     data class Error(val message: String) : ChatUIState()
 }
