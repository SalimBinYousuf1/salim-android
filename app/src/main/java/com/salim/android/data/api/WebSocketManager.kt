package com.salim.android.data.api

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

data class WsEvent(val event: String, val data: JSONObject)

class WebSocketManager constructor(private val client: OkHttpClient) {
    // @Volatile ensures visibility across threads; the ws field is written from
    // coroutine dispatchers (potentially different threads) so this is required.
    @Volatile private var ws: WebSocket? = null
    @Volatile private var currentWsUrl: String? = null

    private val _events = MutableSharedFlow<WsEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<WsEvent> = _events

    @Synchronized
    fun connect(serverUrl: String) {
        val wsUrl = serverUrl
            .replace("https://", "wss://")
            .replace("http://", "ws://")
            .trimEnd('/') + "/ws"

        // Avoid creating a redundant new connection if already connected to the same URL
        if (wsUrl == currentWsUrl && ws != null) return

        ws?.cancel()
        currentWsUrl = wsUrl
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

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                // Clear currentWsUrl on failure so the next connect() call re-establishes
                if (webSocket === ws) currentWsUrl = null
            }
        })
    }

    @Synchronized
    fun disconnect() {
        ws?.cancel()
        ws = null
        currentWsUrl = null
    }
}
