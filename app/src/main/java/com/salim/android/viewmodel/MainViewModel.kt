package com.salim.android.viewmodel

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salim.android.data.api.WebSocketManager
import com.salim.android.data.model.*
import com.salim.android.data.repository.SalimRepository
import com.salim.android.di.dataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: SalimRepository,
    private val wsManager: WebSocketManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        val SERVER_URL_KEY = stringPreferencesKey("server_url")
        const val DEFAULT_URL = "https://salim-bot-mn7c.onrender.com"
    }

    private val _serverUrl = MutableStateFlow(DEFAULT_URL)
    val serverUrl: StateFlow<String> = _serverUrl

    private val _connectionStatus = MutableStateFlow("disconnected")
    val connectionStatus: StateFlow<String> = _connectionStatus

    private val _qrCode = MutableStateFlow<String?>(null)
    val qrCode: StateFlow<String?> = _qrCode

    private val _phone = MutableStateFlow<String?>(null)
    val phone: StateFlow<String?> = _phone

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _history = MutableStateFlow<HistoryResponse?>(null)
    val history: StateFlow<HistoryResponse?> = _history

    private val _knowledge = MutableStateFlow<List<KnowledgeItem>>(emptyList())
    val knowledge: StateFlow<List<KnowledgeItem>> = _knowledge

    private val _config = MutableStateFlow<Config?>(null)
    val config: StateFlow<Config?> = _config

    private val _pairingCode = MutableStateFlow<String?>(null)
    val pairingCode: StateFlow<String?> = _pairingCode

    private val _generatedStatus = MutableStateFlow<String?>(null)
    val generatedStatus: StateFlow<String?> = _generatedStatus

    private val _toast = MutableSharedFlow<String>()
    val toast: SharedFlow<String> = _toast

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadServerUrl()
        observeWebSocket()
        startPolling()
    }

    private fun loadServerUrl() {
        viewModelScope.launch {
            context.dataStore.data.collect { prefs ->
                val url = prefs[SERVER_URL_KEY] ?: DEFAULT_URL
                _serverUrl.value = url
                wsManager.connect(url)
                refreshStatus()
            }
        }
    }

    fun setServerUrl(url: String) {
        viewModelScope.launch {
            context.dataStore.edit { it[SERVER_URL_KEY] = url }
            wsManager.connect(url)
            refreshStatus()
        }
    }

    private fun observeWebSocket() {
        viewModelScope.launch {
            wsManager.events.collect { event ->
                when (event.event) {
                    "status" -> {
                        _connectionStatus.value = event.data.optString("status", "disconnected")
                        _phone.value = event.data.optString("phone").takeIf { it.isNotBlank() }
                        val qr = event.data.optString("qr").takeIf { it.isNotBlank() }
                        if (qr != null) _qrCode.value = qr
                    }
                    "qr" -> _qrCode.value = event.data.optString("qr").takeIf { it.isNotBlank() }
                    "new_message" -> refreshChats()
                    "chats_updated" -> refreshChats()
                }
            }
        }
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                delay(10000)
                refreshStatus()
            }
        }
    }

    fun refreshStatus() {
        viewModelScope.launch {
            repo.getStatus().onSuccess { st ->
                if (st != null) {
                    _connectionStatus.value = st.status
                    _phone.value = st.phone
                    if (st.qr != null) _qrCode.value = st.qr
                    if (st.status == "connected") _qrCode.value = null
                }
            }
        }
    }

    fun refreshChats() {
        viewModelScope.launch {
            repo.getChats().onSuccess { _chats.value = it }
        }
    }

    fun loadMessages(jid: String) {
        viewModelScope.launch {
            repo.getMessages(jid).onSuccess { _messages.value = it }
        }
    }

    fun sendMessage(jid: String, text: String) {
        viewModelScope.launch {
            repo.sendMessage(jid, text).onSuccess { refreshChats() }
                .onFailure { _toast.emit("Failed to send: ${it.message}") }
        }
    }

    fun requestPairing(phone: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repo.requestPairing(phone)
                .onSuccess { _pairingCode.value = it?.code }
                .onFailure { _toast.emit("Pairing failed: ${it.message}") }
            _isLoading.value = false
        }
    }

    fun loadConfig() {
        viewModelScope.launch {
            repo.getConfig().onSuccess { _config.value = it }
        }
    }

    fun saveConfig(map: Map<String, String>) {
        viewModelScope.launch {
            repo.saveConfig(map).onSuccess { _toast.emit("Config saved ✓") }
                .onFailure { _toast.emit("Save failed: ${it.message}") }
        }
    }

    fun loadHistory(search: String = "") {
        viewModelScope.launch {
            repo.getHistory(search).onSuccess { _history.value = it }
        }
    }

    fun loadKnowledge() {
        viewModelScope.launch {
            repo.getKnowledge().onSuccess { _knowledge.value = it }
        }
    }

    fun addKnowledge(title: String, content: String, category: String) {
        viewModelScope.launch {
            repo.addKnowledge(title, content, category)
                .onSuccess { loadKnowledge(); _toast.emit("Added ✓") }
                .onFailure { _toast.emit("Failed: ${it.message}") }
        }
    }

    fun deleteKnowledge(id: Int) {
        viewModelScope.launch {
            repo.deleteKnowledge(id).onSuccess { loadKnowledge(); _toast.emit("Deleted") }
        }
    }

    fun generateStatus(topic: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repo.generateStatus(topic).onSuccess { _generatedStatus.value = it?.status }
                .onFailure { _toast.emit("Failed: ${it.message}") }
            _isLoading.value = false
        }
    }

    fun postStatus(text: String) {
        viewModelScope.launch {
            repo.postStatus(text)
                .onSuccess { _toast.emit("Status posted ✓") }
                .onFailure { _toast.emit("Failed: ${it.message}") }
        }
    }
}
