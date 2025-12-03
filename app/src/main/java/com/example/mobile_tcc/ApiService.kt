package com.example.mobile_tcc

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

// --- DTOs (Modelos de Dados) ---

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

// DTOs Rotina

data class NovaRotinaDTO(
    @SerializedName("emailUsuario") val emailUsuario: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("horario") val horario: String,
    @SerializedName("dose") val dose: String?,
    @SerializedName("descricao") val descricao: String?
)

data class ItemRotinaDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("horario") val horario: String,
    @SerializedName("dose") val dose: String?,
    @SerializedName("feita") var feita: Boolean
)

// --- AQUI ESTAVA FALTANDO O CAMPO ---
data class HomeResumoDTO(
    @SerializedName("progresso") val progresso: Float,
    @SerializedName("tarefas") val tarefas: List<ItemRotinaDTO>,
    @SerializedName("nomeUsuario") val nomeUsuario: String // <--- ADICIONE ESTE CAMPO
)

data class StatusRotinaDTO(
    @SerializedName("idItem") val idItem: Int,
    @SerializedName("feito") val feito: Boolean,
    @SerializedName("data") val data: String
)

data class NovoSintomaDTO(
    @SerializedName("emailUsuario") val emailUsuario: String,
    @SerializedName("bemEstar") val bemEstar: Int,
    @SerializedName("sintomas") val sintomas: Int
)

// --- INTERFACE ---

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<RespostaApi>

    @POST("cadastro")
    suspend fun cadastro(@Body request: CadastroRequest): Response<RespostaApi>

    @POST("rotina")
    suspend fun criarRotina(@Body request: NovaRotinaDTO): Response<RespostaApi>

    @GET("home")
    suspend fun getHome(@Query("email") email: String): Response<HomeResumoDTO>

    @POST("rotina/status")
    suspend fun atualizarStatus(@Body status: StatusRotinaDTO): Response<RespostaApi>

    @DELETE("rotina/{id}")
    suspend fun deletarRotina(@Path("id") id: Int): Response<RespostaApi>

    @POST("sintomas")
    suspend fun registrarSintoma(@Body request: NovoSintomaDTO): Response<RespostaApi>
}

// --- CLIENTE RETROFIT ---

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