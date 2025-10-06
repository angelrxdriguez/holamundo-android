package com.orto.crud

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class InsertOneBody(
    val dataSource: String,
    val database: String,
    val collection: String,
    val document: Map<String, Any?>
)

data class InsertOneRes(val insertedId: String? = null)

interface MongoDataApi {
    @Headers(
        "Content-Type: application/json",
        "api-key: TU_API_KEY"
    )
    @POST("action/insertOne")
    suspend fun insertOne(@Body body: InsertOneBody): InsertOneRes
}

object ApiMongo {
    private const val BASE_URL =
        "https://data.mongodb-api.com/app/TU_APP_ID/endpoint/data/v1/"

    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // útil en debug
            }).build()
    }

    val api: MongoDataApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MongoDataApi::class.java)
    }
}
