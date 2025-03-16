 package com.example.indicab.models
 
 data class ChatMessage(
     val id: String = "",
     val senderId: String = "",
     val receiverId: String = "",
     val message: String = "",
     val timestamp: Long = System.currentTimeMillis(),
     val isRead: Boolean = false
 ) 