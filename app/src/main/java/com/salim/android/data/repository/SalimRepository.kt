package com.salim.android.data.repository

import com.salim.android.data.api.ApiServiceFactory
import com.salim.android.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * All calls go through [factory.api] rather than a fixed [SalimApi] reference,
 * so changing the server URL (via [ApiServiceFactory.updateUrl]) takes effect
 * immediately for every subsequent HTTP request without restarting the app.
 */
@Singleton
class SalimRepository @Inject constructor(private val factory: ApiServiceFactory) {

    // Always returns the api pointed at the current URL
    private val api get() = factory.api

    suspend fun getStatus()      = runCatching { api.getStatus().body() }
    suspend fun getChats()       = runCatching { api.getChats().body() ?: emptyList() }
    suspend fun getMessages(jid: String) = runCatching { api.getMessages(jid).body() ?: emptyList() }
    suspend fun sendMessage(jid: String, text: String) =
        runCatching { api.sendMessage(SendMessageRequest(jid, text)).body() }
    suspend fun requestPairing(phone: String) =
        runCatching { api.requestPairing(PairRequest(phone)).body() }
    suspend fun getConfig()      = runCatching { api.getConfig().body() }
    suspend fun saveConfig(map: Map<String, String>) =
        runCatching { api.saveConfig(map).body() }
    suspend fun getHistory(search: String = "") =
        runCatching { api.getHistory(search = search).body() }
    suspend fun getKnowledge()   = runCatching { api.getKnowledge().body() ?: emptyList() }
    suspend fun addKnowledge(title: String, content: String, category: String) =
        runCatching { api.addKnowledge(AddKnowledgeRequest(title, content, category)).body() }
    suspend fun deleteKnowledge(id: Int) = runCatching { api.deleteKnowledge(id).body() }
    suspend fun generateStatus(topic: String) =
        runCatching { api.generateStatus(GenerateStatusRequest(topic)).body() }
    suspend fun postStatus(text: String) =
        runCatching { api.postStatus(mapOf("text" to text)).body() }

    /** Health-check used to detect Render cold-start warm-up */
    suspend fun healthCheck() = runCatching { api.health().isSuccessful }
}
