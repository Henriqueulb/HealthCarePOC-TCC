package com.example.mobile_tcc

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// DTOs

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("senha") val senha: String
)

data class CadastroRequest(
    @SerializedName("nome") val nome: String,
    @SerializedName("email") val email: String,
    @SerializedName("senha") val senha: String,
    @SerializedName("telefone") val telefone: String
)

data class RespostaApi(
    @SerializedName("mensagem") val mensagem: String,
    @SerializedName("sucesso") val sucesso: Boolean,
    @SerializedName("nomeUsuario") val nomeUsuario: String? = null
)

// DTOs PARA ROTINA

data class NovaRotinaRequest(
    @SerializedName("emailUsuario") val emailUsuario: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("horario") val horario: String,
    @SerializedName("dose") val dose: String?,
    @SerializedName("descricao") val descricao: String?
)

data class Tarefa(
    @SerializedName("id") val id: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("horario") val horario: String,
    @SerializedName("dose") val dose: String?,
    @SerializedName("feita") val feita: Boolean
)

data class ResumoHome(
    @SerializedName("progresso") val progresso: Float,
    @SerializedName("tarefas") val tarefas: List<Tarefa>
)

// INTERFACE

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<RespostaApi>

    @POST("cadastro")
    suspend fun cadastro(@Body request: CadastroRequest): Response<RespostaApi>

    // Rota para criar nova rotina
    @POST("rotina")
    suspend fun criarRotina(@Body request: NovaRotinaRequest): Response<RespostaApi>

    // Rota para buscar os dados da Home (lista de tarefas)
    @GET("home")
    suspend fun getHome(@Query("email") email: String): Response<ResumoHome>
}

object RetrofitClient {
    // Endereço do PC visto pelo emulador Android
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}