package com.example.indicab.services

import com.example.indicab.data.dao.*
import com.example.indicab.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatService @Inject constructor(
    private val chatRoomDao: ChatRoomDao,
    private val messageDao: MessageDao,
    private val participantDao: ChatParticipantDao,
    private val settingsDao: ChatSettingsDao,
    private val chatSocket: ChatSocket,
    private val notificationService: NotificationService
) {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()

    init {
        observeSocketConnection()
        observeIncomingMessages()
        observeParticipantUpdates()
    }

    private fun observeSocketConnection() {
        serviceScope.launch {
            chatSocket.connectionState.collect { isConnected ->
                _chatState.update { it.copy(
                    isConnected = isConnected,
                    isReconnecting = !isConnected && it.isConnected
                ) }
            }
        }
    }

    private fun observeIncomingMessages() {
        serviceScope.launch {
            chatSocket.incomingMessages.collect { message ->
                handleIncomingMessage(message)
            }
        }
    }

    private fun observeParticipantUpdates() {
        serviceScope.launch {
            chatSocket.participantUpdates.collect { update ->
                handleParticipantUpdate(update)
            }
        }
    }

    suspend fun createChatRoom(
        bookingId: String,
        riderId: String,
        driverId: String
    ): ChatRoom {
        val chatRoom = ChatRoom(
            bookingId = bookingId,
            riderId = riderId,
            driverId = driverId
        )
        chatRoomDao.insertChatRoom(chatRoom)

        // Add participants
        participantDao.insertParticipant(
            ChatParticipant(
                id = "P${System.currentTimeMillis()}-R",
                chatRoomId = chatRoom.id,
                userId = riderId,
                role = ParticipantRole.RIDER
            )
        )
        participantDao.insertParticipant(
            ChatParticipant(
                id = "P${System.currentTimeMillis()}-D",
                chatRoomId = chatRoom.id,
                userId = driverId,
                role = ParticipantRole.DRIVER
            )
        )

        // Connect to chat socket
        chatSocket.joinRoom(chatRoom.id)
        return chatRoom
    }

    suspend fun sendMessage(
        chatRoomId: String,
        senderId: String,
        content: String,
        type: MessageType = MessageType.TEXT,
        metadata: MessageMetadata? = null
    ): Message {
        val message = Message(
            chatRoomId = chatRoomId,
            senderId = senderId,
            content = content,
            type = type,
            metadata = metadata
        )
        messageDao.insertMessage(message)

        try {
            chatSocket.sendMessage(message)
            messageDao.updateMessageStatus(message.id, MessageStatus.SENT)
            updateChatRoomLastMessage(chatRoomId, content)
        } catch (e: Exception) {
            messageDao.updateMessageStatus(message.id, MessageStatus.FAILED)
            throw e
        }

        return message
    }

    suspend fun sendSystemMessage(
        chatRoomId: String,
        type: SystemMessageType,
        content: String,
        actionData: Map<String, String>? = null
    ) {
        val message = Message(
            chatRoomId = chatRoomId,
            senderId = "SYSTEM",
            content = content,
            type = MessageType.SYSTEM,
            metadata = MessageMetadata(
                systemMessageType = type,
                actionData = actionData
            )
        )
        messageDao.insertMessage(message)
        updateChatRoomLastMessage(chatRoomId, content)
    }

    suspend fun markMessageAsRead(messageId: String) {
        messageDao.updateMessageStatus(messageId, MessageStatus.READ)
        chatSocket.sendReadReceipt(messageId)
    }

    suspend fun updateTypingStatus(
        chatRoomId: String,
        userId: String,
        isTyping: Boolean
    ) {
        participantDao.updateTypingStatus(chatRoomId, userId, isTyping)
        chatSocket.sendTypingStatus(chatRoomId, userId, isTyping)
    }

    private suspend fun handleIncomingMessage(message: Message) {
        messageDao.insertMessage(message)
        updateChatRoomLastMessage(message.chatRoomId, message.content)
        chatRoomDao.incrementUnreadCount(message.chatRoomId)

        // Send notification if settings allow
        val settings = settingsDao.getChatSettings(message.senderId).firstOrNull()
        if (settings?.notificationsEnabled == true) {
            notificationService.showMessageNotification(
                ChatNotification(
                    chatRoomId = message.chatRoomId,
                    messageId = message.id,
                    userId = message.senderId,
                    title = "New Message",
                    body = message.content,
                    type = NotificationType.NEW_MESSAGE
                )
            )
        }
    }

    private suspend fun handleParticipantUpdate(participant: ChatParticipant) {
        participantDao.updateParticipant(participant)
    }

    private suspend fun updateChatRoomLastMessage(chatRoomId: String, content: String) {
        chatRoomDao.updateLastMessage(chatRoomId, content)
    }

    fun getChatRoom(chatRoomId: String): Flow<ChatRoom?> =
        chatRoomDao.getChatRoomById(chatRoomId)?.let { Flow { emit(it) } } ?: flowOf(null)

    fun getChatRoomForBooking(bookingId: String): Flow<ChatRoom?> =
        chatRoomDao.getChatRoomForBooking(bookingId)

    fun getMessages(
        chatRoomId: String,
        limit: Int = 50,
        offset: Int = 0
    ): Flow<List<Message>> =
        messageDao.getMessages(chatRoomId, limit, offset)

    fun getChatParticipants(chatRoomId: String): Flow<List<ChatParticipant>> =
        participantDao.getChatParticipants(chatRoomId)

    suspend fun archiveChatRoom(chatRoomId: String) {
        chatRoomDao.updateStatus(chatRoomId, ChatRoomStatus.ARCHIVED)
        chatSocket.leaveRoom(chatRoomId)
    }

    suspend fun clearChat(chatRoomId: String) {
        messageDao.deleteMessages(chatRoomId)
        chatRoomDao.updateLastMessage(chatRoomId, "")
    }

    suspend fun updateChatSettings(settings: ChatSettings) {
        settingsDao.updateChatSettings(settings)
    }

    fun disconnect() {
        chatSocket.disconnect()
    }
}

// Interface for WebSocket implementation
interface ChatSocket {
    val connectionState: Flow<Boolean>
    val incomingMessages: Flow<Message>
    val participantUpdates: Flow<ChatParticipant>

    suspend fun connect()
    suspend fun disconnect()
    suspend fun joinRoom(roomId: String)
    suspend fun leaveRoom(roomId: String)
    suspend fun sendMessage(message: Message)
    suspend fun sendReadReceipt(messageId: String)
    suspend fun sendTypingStatus(roomId: String, userId: String, isTyping: Boolean)
}

// Interface for notification service
interface NotificationService {
    suspend fun showMessageNotification(notification: ChatNotification)
}
