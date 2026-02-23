package com.salim.android.di

import javax.inject.Qualifier

/** Qualifies the [okhttp3.OkHttpClient] used for Retrofit HTTP calls. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HttpClient

/** Qualifies the [okhttp3.OkHttpClient] used for WebSocket connections. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WsClient
