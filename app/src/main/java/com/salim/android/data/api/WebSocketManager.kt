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
        val wsUrl = serverUrl.replace("https://", "wss://").replace("http://", "ws://").trimEnd('/') + "/ws"
        val request = Request.Builder().url(wsUrl).build()
        ws = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val json = JSONObject(text)
                    val event = json.optString("event")
                    val data = json.optJSONObject("data") ?: JSONObject()
                    _events.tryEmit(WsEvent(event, data))
                } catch (_: Exception) {}
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                // auto reconnect handled externally
            }
        })
    }

    fun disconnect() { ws?.cancel(); ws = null }
}
