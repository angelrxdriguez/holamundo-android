package com.orto.crud

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
data class Carta(
    val _id: String?,
    val nombre: String?,
    val vida: Int?,
    val dano: Int?
)

data class CrearBody(
    val dataSource: String,      // lo enviaremos pero el server lo ignora
    val database: String,
    val collection: String,
    val document: Map<String, Any?>
)

data class CrearResponse(val id: String?)

interface ApiMongoService {
    @GET("listar")
    fun listar(
        @Query("database") database: String,
        @Query("collection") collection: String
    ): Call<List<Carta>>

    @POST("crear")
    fun crear(@Body body: CrearBody): Call<CrearResponse>
    @POST("borrar")
    fun borrar(@Body body: Map<String, String>): Call<Map<String, Any>>
    @POST("actualizar") //tenia any pero el retrofit no me va. apso a json y asi lee
    fun actualizar(@Body body: HashMap<String, Any>): Call<Map<String, Any>>

}

object ApiMongo {
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val api: ApiMongoService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiMongoService::class.java)
    }
}
