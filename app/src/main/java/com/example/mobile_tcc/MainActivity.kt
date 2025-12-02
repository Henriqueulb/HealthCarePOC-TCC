package com.example.mobile_tcc

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                NavegacaoPrincipal()
            }
        }
    }
}

// --- CONTROLE DE NAVEGAÇÃO ---
@Composable
fun NavegacaoPrincipal() {
    // Estados: "login", "cadastro", "home", "rotina"
    var telaAtual by remember { mutableStateOf("login") }

    // Estado para guardar o nome do usuário logado
    var nomeUsuarioLogado by remember { mutableStateOf("Usuário") }

    when (telaAtual) {
        "login" -> TelaLogin(
            onNavegarCadastro = { telaAtual = "cadastro" },
            onLoginSucesso = { nome ->
                // 1. Recebe o nome vindo do Login
                nomeUsuarioLogado = nome
                // 2. Muda para a Home
                telaAtual = "home"
            }
        )
        "cadastro" -> TelaCadastro(
            onCadastroSucesso = { telaAtual = "login" },
            onVoltar = { telaAtual = "login" }
        )
        "home" -> TelaHome(
            nomeUsuario = nomeUsuarioLogado, // Passa o nome para a Home
            onLogout = { telaAtual = "login" },
            onNavegarRotina = { telaAtual = "rotina" } // <--- VAI PARA A ROTINA
        )
        "rotina" -> TelaRotina(
            onVoltar = { telaAtual = "home" } // <--- VOLTA PARA A HOME
        )
    }
}

// --- TELA DE LOGIN ---
@Composable
fun TelaLogin(onNavegarCadastro: () -> Unit, onLoginSucesso: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bem-vindo", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { novo ->
                // Filtro para não aceitar espaços
                email = novo.filter { !it.isWhitespace() }
            },
            label = { Text("E-mail") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botão de Entrar
        Button(
            onClick = {
                scope.launch {
                    try {
                        // Limpa o email antes de enviar
                        val emailValidated = email.trim()
                        val res = RetrofitClient.api.login(LoginRequest(emailValidated, senha))

                        if (res.isSuccessful) {
                            val resposta = res.body()
                            if (resposta?.sucesso == true) {
                                // Pega o nome da resposta da API (ou usa padrão se vier nulo)
                                val nomeRecebido = resposta.nomeUsuario ?: "Paciente"

                                Toast.makeText(context, "Olá, $nomeRecebido!", Toast.LENGTH_SHORT).show()

                                // Chama a função de sucesso passando o nome
                                onLoginSucesso(nomeRecebido)
                            }
                        } else {
                            val erroJson = res.errorBody()?.string()
                            Toast.makeText(context, "E-mail ou senha incorretos", Toast.LENGTH_SHORT).show()
                            println("Erro detalhado: $erroJson")
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Sem conexão: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavegarCadastro) {
            Text("Não tem conta? Cadastre-se")
        }
    }
}