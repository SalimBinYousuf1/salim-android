package com.salim.android.data.repository

import com.salim.android.data.api.SalimApi
import com.salim.android.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalimRepository @Inject constructor(private val api: SalimApi) {
    suspend fun getStatus() = runCatching { api.getStatus().body() }
    suspend fun getChats() = runCatching { api.getChats().body() ?: emptyList() }
    suspend fun getMessages(jid: String) = runCatching { api.getMessages(jid).body() ?: emptyList() }
    suspend fun sendMessage(jid: String, text: String) = runCatching { api.sendMessage(SendMessageRequest(jid, text)).body() }
    suspend fun requestPairing(phone: String) = runCatching { api.requestPairing(PairRequest(phone)).body() }
    suspend fun getConfig() = runCatching { api.getConfig().body() }
    suspend fun saveConfig(map: Map<String, String>) = runCatching { api.saveConfig(map).body() }
    suspend fun getHistory(search: String = "") = runCatching { api.getHistory(search = search).body() }
    suspend fun getKnowledge() = runCatching { api.getKnowledge().body() ?: emptyList() }
    suspend fun addKnowledge(title: String, content: String, category: String) = runCatching { api.addKnowledge(AddKnowledgeRequest(title, content, category)).body() }
    suspend fun deleteKnowledge(id: Int) = runCatching { api.deleteKnowledge(id).body() }
    suspend fun generateStatus(topic: String) = runCatching { api.generateStatus(GenerateStatusRequest(topic)).body() }
    suspend fun postStatus(text: String) = runCatching { api.postStatus(mapOf("text" to text)).body() }
}
