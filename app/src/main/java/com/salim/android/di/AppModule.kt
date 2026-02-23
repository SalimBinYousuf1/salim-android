package com.salim.android.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.salim.android.data.api.ApiServiceFactory
import com.salim.android.data.api.WebSocketManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// Must be top-level so the delegate is a true singleton backed by a single DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "salim_prefs")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore

    /**
     * Shared OkHttpClient used for all REST (HTTP) calls via Retrofit.
     * WebSocket gets its own client below so they don't share a connection pool
     * and a saturated HTTP pool can't block WebSocket frames (or vice-versa).
     */
    @Provides
    @Singleton
    @HttpClient
    fun provideHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    /**
     * Dedicated OkHttpClient for WebSocket connections.
     * Longer read timeout (0 = no timeout) is standard for persistent WS connections.
     */
    @Provides
    @Singleton
    @WsClient
    fun provideWsClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.SECONDS)   // no read timeout for persistent WebSocket
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    /**
     * [ApiServiceFactory] replaces the old @Singleton Retrofit + SalimApi pair.
     * It rebuilds the Retrofit instance on-the-fly when the server URL changes,
     * so HTTP calls always hit the URL the user has configured.
     */
    @Provides
    @Singleton
    fun provideApiServiceFactory(@HttpClient httpClient: OkHttpClient): ApiServiceFactory =
        ApiServiceFactory(httpClient)

    @Provides
    @Singleton
    fun provideWebSocketManager(@WsClient wsClient: OkHttpClient): WebSocketManager =
        WebSocketManager(wsClient)

}
