package com.salim.android.data.model

data class StatusResponse(
    val ok: Boolean = false,
    val status: String = "disconnected",
    val phone: String? = null,
    val qr: String? = null
)

data class Chat(
    val id: Int = 0,
    val jid: String = "",
    val name: String = "",
    val is_group: Int = 0,
    val last_message: String = "",
    val last_timestamp: Long = 0,
    val unread_count: Int = 0
)

data class Message(
    val id: Int = 0,
    val message_id: String = "",
    val jid: String = "",
    val from_me: Int = 0,
    val sender: String? = null,
    val text: String = "",
    val media_type: String? = null,
    val timestamp: Long = 0
)

data class AIInteraction(
    val id: Int = 0,
    val jid: String = "",
    val sender: String? = null,
    val user_message: String = "",
    val ai_response: String = "",
    val model_used: String = "",
    val tokens_used: Int = 0,
    val timestamp: Long = 0
)

data class HistoryResponse(
    val total: Int = 0,
    val today: Int = 0,
    val interactions: List<AIInteraction> = emptyList()
)

data class KnowledgeItem(
    val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val category: String = "general",
    val created_at: Long = 0
)

data class Config(
    val agent_name: String = "Salim",
    val personality_tone: String = "friendly",
    val humor_level: String = "medium",
    val custom_instructions: String = "",
    val auto_reply: String = "true",
    val group_reactions: String = "true",
    val temperature: String = "0.8",
    val max_tokens: String = "1024"
)

data class SendMessageRequest(val jid: String, val text: String)
data class PairRequest(val phone: String)
data class PairResponse(val code: String?)
data class GenerateStatusRequest(val topic: String = "")
data class GenerateStatusResponse(val status: String = "")
data class OkResponse(val ok: Boolean = false)
data class AddKnowledgeRequest(val title: String, val content: String, val category: String = "general")
