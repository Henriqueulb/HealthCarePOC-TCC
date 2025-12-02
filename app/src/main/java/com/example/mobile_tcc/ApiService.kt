package com.example.mobile_tcc // CONFIRA SE O PACOTE ESTÁ CORRETO COM O SEU PROJETO

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// --- 1. MODELOS DE DADOS (DTOs) ---
// Usamos @SerializedName para garantir que o JSON tenha as chaves exatas que o Backend espera,
// independente de como o código for minificado ou alterado no futuro.

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("senha") val senha: String
)

data class CadastroRequest(
    @SerializedName("email") val email: String,
    @SerializedName("senha") val senha: String,
    @SerializedName("telefone") val telefone: String,
    @SerializedName("nome") val nome: String,         // Novo campo: Nome
)

data class RespostaApi(
    @SerializedName("mensagem") val mensagem: String,
    @SerializedName("sucesso") val sucesso: Boolean,
    @SerializedName("nomeUsuario") val nomeUsuario: String? = null // Recebe o nome vindo do Login
)

// DTOs para a Tela Home (Usados na rota GET /home)
data class Tarefa(
    val id: Int,
    val titulo: String,
    val horario: String,
    val feita: Boolean
)

data class ResumoHome(
    val progresso: Float,
    val tarefas: List<Tarefa>
)

// --- 2. INTERFACE DA API (ROTAS) ---
// Define os endpoints que o aplicativo pode chamar

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<RespostaApi>

    @POST("cadastro")
    suspend fun cadastro(@Body request: CadastroRequest): Response<RespostaApi>

    @GET("home")
    suspend fun getHome(): Response<ResumoHome>
}

// --- 3. CONEXÃO RETROFIT (CLIENTE) ---

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}