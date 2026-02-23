package com.salim.android.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A singleton factory that holds the current [SalimApi] instance.
 *
 * Problem solved: Retrofit bakes the base URL at construction time, so a
 * @Singleton Retrofit/SalimApi would forever point to the hardcoded URL even
 * after the user changes it in Settings.  This factory rebuilds the Retrofit
 * instance whenever [updateUrl] is called, so every subsequent API call
 * automatically uses the new URL.
 *
 * Thread-safety: [api] and [currentBaseUrl] are @Volatile so reads from any
 * thread always see the latest value written by [updateUrl].
 */
class ApiServiceFactory constructor(
    private val httpClient: OkHttpClient
) {
    companion object {
        const val DEFAULT_URL = "https://salim-bot-mn7c.onrender.com"
    }

    @Volatile
    private var currentBaseUrl: String = DEFAULT_URL

    @Volatile
    var api: SalimApi = buildApi(DEFAULT_URL)
        private set

    /**
     * Rebuilds the [SalimApi] to point at [newUrl].
     * Safe to call from any thread; subsequent [api] reads will use the new instance.
     */
    @Synchronized
    fun updateUrl(newUrl: String) {
        val normalised = newUrl.trimEnd('/')
        if (normalised == currentBaseUrl.trimEnd('/')) return   // no-op if same
        currentBaseUrl = normalised
        api = buildApi(normalised)
    }

    fun currentUrl(): String = currentBaseUrl

    private fun buildApi(baseUrl: String): SalimApi {
        val url = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return Retrofit.Builder()
            .baseUrl(url)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SalimApi::class.java)
    }
}
