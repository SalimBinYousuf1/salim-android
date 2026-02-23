package com.salim.android.data.api

import com.salim.android.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface SalimApi {
    @GET("health")
    suspend fun health(): Response<StatusResponse>

    @GET("api/status")
    suspend fun getStatus(): Response<StatusResponse>

    @POST("api/pair")
    suspend fun requestPairing(@Body req: PairRequest): Response<PairResponse>

    @POST("api/send")
    suspend fun sendMessage(@Body req: SendMessageRequest): Response<OkResponse>

    @GET("api/chats")
    suspend fun getChats(@Query("limit") limit: Int = 50): Response<List<Chat>>

    @GET("api/messages/{jid}")
    suspend fun getMessages(@Path("jid") jid: String, @Query("limit") limit: Int = 50): Response<List<Message>>

    @POST("api/status/generate")
    suspend fun generateStatus(@Body req: GenerateStatusRequest): Response<GenerateStatusResponse>

    @POST("api/status/post")
    suspend fun postStatus(@Body req: Map<String, String>): Response<OkResponse>

    @GET("api/config")
    suspend fun getConfig(): Response<Config>

    @POST("api/config")
    suspend fun saveConfig(@Body config: Map<String, String>): Response<OkResponse>

    @GET("api/history")
    suspend fun getHistory(@Query("limit") limit: Int = 100, @Query("search") search: String = ""): Response<HistoryResponse>

    @GET("api/knowledge")
    suspend fun getKnowledge(): Response<List<KnowledgeItem>>

    @POST("api/knowledge")
    suspend fun addKnowledge(@Body req: AddKnowledgeRequest): Response<OkResponse>

    @DELETE("api/knowledge/{id}")
    suspend fun deleteKnowledge(@Path("id") id: Int): Response<OkResponse>
}
