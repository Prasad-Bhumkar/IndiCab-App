package com.example.indicab.services

import com.example.indicab.models.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class MockChatSocket @Inject constructor() : ChatSocket {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val messageChannel = Channel<Message>(Channel.BUFFERED)
    private val participantChannel = Channel<ChatParticipant>(Channel.BUFFERED)
    private val connectedRooms = mutableSetOf<String>()

    private val _connectionState = MutableStateFlow(false)
    override val connectionState: Flow<Boolean> = _connectionState.asStateFlow()

    override val incomingMessages: Flow<Message> = messageChannel.receiveAsFlow()
    override val participantUpdates: Flow<ChatParticipant> = participantChannel.receiveAsFlow()

    private var connectionJob: Job? = null
    private var simulatedDelayRange = 100L..1000L
    private var disconnectProbability = 0.01 // 1% chance of random disconnect
    private var messageLossProbability = 0.05 // 5% chance of message loss

    override suspend fun connect() {
        connectionJob?.cancel()
        connectionJob = scope.launch {
            _connectionState.value = true
            
            // Simulate periodic connection checks
            while (isActive) {
                delay(5000) // Check every 5 seconds
                if (Random.nextDouble() < disconnectProbability) {
                    _connectionState.value = false
                    delay(2000) // Wait 2 seconds before reconnecting
                    _connectionState.value = true
                }
            }
        }
    }

    override suspend fun disconnect() {
        connectionJob?.cancel()
        _connectionState.value = false
        connectedRooms.clear()
    }

    override suspend fun joinRoom(roomId: String) {
        delay(Random.nextLong(simulatedDelayRange.first, simulatedDelayRange.last))
        connectedRooms.add(roomId)
        
        // Simulate system message for joining
        scope.launch {
            messageChannel.send(
                Message(
                    chatRoomId = roomId,
                    senderId = "SYSTEM",
                    content = "Connected to chat room",
                    type = MessageType.SYSTEM,
                    metadata = MessageMetadata(
                        systemMessageType = SystemMessageType.RIDE_STARTED
                    )
                )
            )
        }
    }

    override suspend fun leaveRoom(roomId: String) {
        delay(Random.nextLong(simulatedDelayRange.first, simulatedDelayRange.last))
        connectedRooms.remove(roomId)
    }

    override suspend fun sendMessage(message: Message) {
        if (!_connectionState.value) {
            throw Exception("Not connected")
        }

        if (!connectedRooms.contains(message.chatRoomId)) {
            throw Exception("Not joined to room ${message.chatRoomId}")
        }

        delay(Random.nextLong(simulatedDelayRange.first, simulatedDelayRange.last))

        if (Random.nextDouble() < messageLossProbability) {
            throw Exception("Message delivery failed")
        }

        // Simulate message delivery and status updates
        scope.launch {
            // Simulate delivery status
            delay(500)
            messageChannel.send(message.copy(status = MessageStatus.DELIVERED))

            // Simulate read receipt after some time
            if (Random.nextBoolean()) {
                delay(2000)
                messageChannel.send(message.copy(status = MessageStatus.READ))
            }
        }
    }

    override suspend fun sendReadReceipt(messageId: String) {
        if (!_connectionState.value) {
            throw Exception("Not connected")
        }

        delay(Random.nextLong(simulatedDelayRange.first, simulatedDelayRange.last))

        if (Random.nextDouble() < messageLossProbability) {
            throw Exception("Read receipt delivery failed")
        }
    }

    override suspend fun sendTypingStatus(roomId: String, userId: String, isTyping: Boolean) {
        if (!_connectionState.value) {
            throw Exception("Not connected")
        }

        if (!connectedRooms.contains(roomId)) {
            throw Exception("Not joined to room $roomId")
        }

        delay(Random.nextLong(simulatedDelayRange.first, simulatedDelayRange.last))

        // Simulate participant update
        participantChannel.send(
            ChatParticipant(
                id = "P$userId",
                chatRoomId = roomId,
                userId = userId,
                role = ParticipantRole.RIDER, // Mock role
                isTyping = isTyping,
                lastSeen = LocalDateTime.now()
            )
        )
    }

    // Test helper methods
    fun simulateIncomingMessage(
        chatRoomId: String,
        senderId: String,
        content: String,
        type: MessageType = MessageType.TEXT
    ) {
        scope.launch {
            if (_connectionState.value && connectedRooms.contains(chatRoomId)) {
                messageChannel.send(
                    Message(
                        chatRoomId = chatRoomId,
                        senderId = senderId,
                        content = content,
                        type = type
                    )
                )
            }
        }
    }

    fun simulateParticipantUpdate(
        chatRoomId: String,
        userId: String,
        isTyping: Boolean = false,
        status: ParticipantStatus = ParticipantStatus.ACTIVE
    ) {
        scope.launch {
            if (_connectionState.value && connectedRooms.contains(chatRoomId)) {
                participantChannel.send(
                    ChatParticipant(
                        id = "P$userId",
                        chatRoomId = chatRoomId,
                        userId = userId,
                        role = ParticipantRole.RIDER, // Mock role
                        isTyping = isTyping,
                        status = status,
                        lastSeen = LocalDateTime.now()
                    )
                )
            }
        }
    }

    fun simulateConnectionIssue() {
        scope.launch {
            _connectionState.value = false
            delay(2000)
            _connectionState.value = true
        }
    }

    fun setSimulatedDelayRange(range: LongRange) {
        simulatedDelayRange = range
    }

    fun setDisconnectProbability(probability: Double) {
        disconnectProbability = probability.coerceIn(0.0, 1.0)
    }

    fun setMessageLossProbability(probability: Double) {
        messageLossProbability = probability.coerceIn(0.0, 1.0)
    }
}
