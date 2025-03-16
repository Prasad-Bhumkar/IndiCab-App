 package com.example.indicab.models
 
 import androidx.room.Entity
 import androidx.room.PrimaryKey
 import java.time.LocalDateTime
 
 @Entity(tableName = "chat_rooms")
 data class ChatRoom(
     @PrimaryKey
     val id: String = "CR" + System.currentTimeMillis(),
     val bookingId: String,
     val riderId: String,
     val driverId: String,
     val lastMessage: String? = null,
     val lastMessageTime: LocalDateTime? = null,
     val unreadCount: Int = 0,
     val status: ChatRoomStatus = ChatRoomStatus.ACTIVE,
     val createdAt: LocalDateTime = LocalDateTime.now(),
     val updatedAt: LocalDateTime = LocalDateTime.now()
 )
 
 enum class ChatRoomStatus {
     ACTIVE,     // Chat is active during ride
     ARCHIVED,   // Chat preserved after ride completion
     DELETED     // Soft delete
 }
 
 @Entity(tableName = "messages")
 data class Message(
     @PrimaryKey
     val id: String = "MSG" + System.currentTimeMillis(),
     val chatRoomId: String,
     val senderId: String,
     val content: String,
     val type: MessageType = MessageType.TEXT,
     val status: MessageStatus = MessageStatus.SENT,
     val metadata: MessageMetadata? = null,
     val createdAt: LocalDateTime = LocalDateTime.now(),
     val updatedAt: LocalDateTime = LocalDateTime.now()
 )
 
 enum class MessageType {
     TEXT,           // Regular text message
     LOCATION,       // Shared location
     IMAGE,          // Image message
     SYSTEM,         // System notification
     QUICK_REPLY     // Predefined quick reply
 }
 
 enum class MessageStatus {
     SENDING,    // Message is being sent
     SENT,       // Message sent to server
     DELIVERED,  // Message delivered to recipient
     READ,       // Message read by recipient
     FAILED      // Failed to send message
 }
 
 data class MessageMetadata(
     // Location data
     val latitude: Double? = null,
     val longitude: Double? = null,
     val locationAddress: String? = null,
 
     // Image data
     val imageUrl: String? = null,
     val thumbnailUrl: String? = null,
     val imageSize: Long? = null,
 
     // System message data
     val systemMessageType: SystemMessageType? = null,
     val actionData: Map<String, String>? = null,
 
     // Quick reply data
     val quickReplyOptions: List<String>? = null,
     val selectedOption: String? = null
 )
 
 enum class SystemMessageType {
     RIDE_STARTED,
     RIDE_COMPLETED,
     DRIVER_ARRIVED,
     APPROACHING_DESTINATION,
     SAFETY_ALERT,
     PAYMENT_REMINDER
 }
 
 data class QuickReply(
     val id: String,
     val message: String,
     val context: QuickReplyContext,
     val isEnabled: Boolean = true
 )
 
 enum class QuickReplyContext {
     GREETING,
     ARRIVAL,
     DELAY,
     CONFIRMATION,
     FAREWELL
 }
 
 // Chat participant for managing user states
 @Entity(tableName = "chat_participants")
 data class ChatParticipant(
     @PrimaryKey
     val id: String,
     val chatRoomId: String,
     val userId: String,
     val role: ParticipantRole,
     val lastSeen: LocalDateTime? = null,
     val isTyping: Boolean = false,
     val status: ParticipantStatus = ParticipantStatus.ACTIVE,
     val createdAt: LocalDateTime = LocalDateTime.now(),
     val updatedAt: LocalDateTime = LocalDateTime.now()
 )
 
 enum class ParticipantRole {
     RIDER,
     DRIVER
 }
 
 enum class ParticipantStatus {
     ACTIVE,     // Participant is in the chat
     INACTIVE,   // Participant has left the chat
     BLOCKED     // Participant is blocked
 }
 
 // Chat settings for user preferences
 @Entity(tableName = "chat_settings")
 data class ChatSettings(
     @PrimaryKey
     val userId: String,
     val notificationsEnabled: Boolean = true,
     val soundEnabled: Boolean = true,
     val vibrationEnabled: Boolean = true,
     val showTypingIndicator: Boolean = true,
     val showReadReceipts: Boolean = true,
     val archiveAfterDays: Int = 30,
     val createdAt: LocalDateTime = LocalDateTime.now(),
     val updatedAt: LocalDateTime = LocalDateTime.now()
 )
 
 // Real-time chat state
 data class ChatState(
     val isConnected: Boolean = false,
     val isReconnecting: Boolean = false,
     val lastSyncTime: LocalDateTime? = null,
     val error: String? = null
 )
 
 // Chat notification
 data class ChatNotification(
     val id: String = "NOTIF" + System.currentTimeMillis(),
     val chatRoomId: String,
     val messageId: String,
     val userId: String,
     val title: String,
     val body: String,
     val type: NotificationType,
     val metadata: Map<String, String>? = null,
     val createdAt: LocalDateTime = LocalDateTime.now()
 )
 
 enum class NotificationType {
     NEW_MESSAGE,
     MENTION,
     SYSTEM_ALERT
 }
