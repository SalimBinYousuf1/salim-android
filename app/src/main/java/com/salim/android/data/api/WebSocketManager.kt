package com.salim.android.data.api

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

data class WsEvent(val event: String, val data: JSONObject)

@Singleton
class WebSocketManager @Inject constructor(private val client: OkHttpClient) {
    private var ws: WebSocket? = null
    private val _events = MutableSharedFlow<WsEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<WsEvent> = _events

    fun connect(serverUrl: String) {
        ws?.cancel()
        val wsUrl = serverUrl
            .replace("https://", "wss://")
            .replace("http://", "ws://")
            .trimEnd('/') + "/ws"
        ws = client.newWebSocket(Request.Builder().url(wsUrl).build(), object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val json = JSONObject(text)
                    _events.tryEmit(
                        WsEvent(
                            json.optString("event"),
                            json.optJSONObject("data") ?: JSONObject()
                        )
                    )
                } catch (_: Exception) {}
            }
        })
    }

    fun disconnect() {
        ws?.cancel()
        ws = null
    }
}
