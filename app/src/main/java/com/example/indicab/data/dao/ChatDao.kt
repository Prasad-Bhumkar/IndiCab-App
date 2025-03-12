package com.example.indicab.data.dao

import androidx.room.*
import com.example.indicab.models.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ChatRoomDao {
    @Query("SELECT * FROM chat_rooms WHERE riderId = :userId OR driverId = :userId ORDER BY updatedAt DESC")
    fun getChatRooms(userId: String): Flow<List<ChatRoom>>

    @Query("SELECT * FROM chat_rooms WHERE bookingId = :bookingId LIMIT 1")
    fun getChatRoomForBooking(bookingId: String): Flow<ChatRoom?>

    @Query("SELECT * FROM chat_rooms WHERE id = :chatRoomId")
    suspend fun getChatRoomById(chatRoomId: String): ChatRoom?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatRoom(chatRoom: ChatRoom)

    @Update
    suspend fun updateChatRoom(chatRoom: ChatRoom)

    @Query("""
        UPDATE chat_rooms 
        SET lastMessage = :message,
            lastMessageTime = :timestamp,
            updatedAt = :timestamp
        WHERE id = :chatRoomId
    """)
    suspend fun updateLastMessage(
        chatRoomId: String,
        message: String,
        timestamp: LocalDateTime = LocalDateTime.now()
    )

    @Query("""
        UPDATE chat_rooms 
        SET unreadCount = unreadCount + 1
        WHERE id = :chatRoomId
    """)
    suspend fun incrementUnreadCount(chatRoomId: String)

    @Query("""
        UPDATE chat_rooms 
        SET unreadCount = 0
        WHERE id = :chatRoomId
    """)
    suspend fun clearUnreadCount(chatRoomId: String)

    @Query("""
        UPDATE chat_rooms 
        SET status = :status,
            updatedAt = :timestamp
        WHERE id = :chatRoomId
    """)
    suspend fun updateStatus(
        chatRoomId: String,
        status: ChatRoomStatus,
        timestamp: LocalDateTime = LocalDateTime.now()
    )
}

@Dao
interface MessageDao {
    @Query("""
        SELECT * FROM messages 
        WHERE chatRoomId = :chatRoomId 
        ORDER BY createdAt DESC 
        LIMIT :limit OFFSET :offset
    """)
    fun getMessages(
        chatRoomId: String,
        limit: Int = 50,
        offset: Int = 0
    ): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: String): Message?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Update
    suspend fun updateMessage(message: Message)

    @Query("""
        UPDATE messages 
        SET status = :status,
            updatedAt = :timestamp
        WHERE id = :messageId
    """)
    suspend fun updateMessageStatus(
        messageId: String,
        status: MessageStatus,
        timestamp: LocalDateTime = LocalDateTime.now()
    )

    @Query("""
        SELECT * FROM messages 
        WHERE chatRoomId = :chatRoomId 
        AND type = :type 
        ORDER BY createdAt DESC
    """)
    fun getMessagesByType(chatRoomId: String, type: MessageType): Flow<List<Message>>

    @Query("""
        SELECT * FROM messages 
        WHERE status = :status 
        AND createdAt <= :timeout
    """)
    suspend fun getFailedMessages(
        status: MessageStatus = MessageStatus.FAILED,
        timeout: LocalDateTime = LocalDateTime.now().minusMinutes(5)
    ): List<Message>

    @Query("DELETE FROM messages WHERE chatRoomId = :chatRoomId")
    suspend fun deleteMessages(chatRoomId: String)
}

@Dao
interface ChatParticipantDao {
    @Query("SELECT * FROM chat_participants WHERE chatRoomId = :chatRoomId")
    fun getChatParticipants(chatRoomId: String): Flow<List<ChatParticipant>>

    @Query("""
        SELECT * FROM chat_participants 
        WHERE chatRoomId = :chatRoomId 
        AND userId = :userId 
        LIMIT 1
    """)
    suspend fun getParticipant(chatRoomId: String, userId: String): ChatParticipant?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: ChatParticipant)

    @Update
    suspend fun updateParticipant(participant: ChatParticipant)

    @Query("""
        UPDATE chat_participants 
        SET lastSeen = :timestamp,
            updatedAt = :timestamp
        WHERE chatRoomId = :chatRoomId 
        AND userId = :userId
    """)
    suspend fun updateLastSeen(
        chatRoomId: String,
        userId: String,
        timestamp: LocalDateTime = LocalDateTime.now()
    )

    @Query("""
        UPDATE chat_participants 
        SET isTyping = :isTyping,
            updatedAt = :timestamp
        WHERE chatRoomId = :chatRoomId 
        AND userId = :userId
    """)
    suspend fun updateTypingStatus(
        chatRoomId: String,
        userId: String,
        isTyping: Boolean,
        timestamp: LocalDateTime = LocalDateTime.now()
    )

    @Query("""
        UPDATE chat_participants 
        SET status = :status,
            updatedAt = :timestamp
        WHERE chatRoomId = :chatRoomId 
        AND userId = :userId
    """)
    suspend fun updateStatus(
        chatRoomId: String,
        userId: String,
        status: ParticipantStatus,
        timestamp: LocalDateTime = LocalDateTime.now()
    )
}

@Dao
interface ChatSettingsDao {
    @Query("SELECT * FROM chat_settings WHERE userId = :userId")
    fun getChatSettings(userId: String): Flow<ChatSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatSettings(settings: ChatSettings)

    @Update
    suspend fun updateChatSettings(settings: ChatSettings)

    @Query("""
        UPDATE chat_settings 
        SET notificationsEnabled = :enabled,
            updatedAt = :timestamp
        WHERE userId = :userId
    """)
    suspend fun updateNotificationSettings(
        userId: String,
        enabled: Boolean,
        timestamp: LocalDateTime = LocalDateTime.now()
    )

    @Query("""
        UPDATE chat_settings 
        SET soundEnabled = :enabled,
            updatedAt = :timestamp
        WHERE userId = :userId
    """)
    suspend fun updateSoundSettings(
        userId: String,
        enabled: Boolean,
        timestamp: LocalDateTime = LocalDateTime.now()
    )

    @Query("""
        UPDATE chat_settings 
        SET vibrationEnabled = :enabled,
            updatedAt = :timestamp
        WHERE userId = :userId
    """)
    suspend fun updateVibrationSettings(
        userId: String,
        enabled: Boolean,
        timestamp: LocalDateTime = LocalDateTime.now()
    )
}
