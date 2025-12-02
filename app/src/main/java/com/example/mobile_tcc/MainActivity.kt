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
            // tema
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                NavegacaoPrincipal()
            }
        }
    }
}

// CONTROLE DE NAVEGACAO
@Composable
fun NavegacaoPrincipal() {
    // Estados
    var telaAtual by remember { mutableStateOf("login") }

    // Dados do Usuário Logado
    var nomeUsuarioLogado by remember { mutableStateOf("Usuário") }
    var emailUsuarioLogado by remember { mutableStateOf("") }

    when (telaAtual) {
        "login" -> TelaLogin(
            onNavegarCadastro = { telaAtual = "cadastro" },
            onLoginSucesso = { nome, email ->
                // Salva os dados do usuário logado
                nomeUsuarioLogado = nome
                emailUsuarioLogado = email
                telaAtual = "home"
            }
        )
        "cadastro" -> TelaCadastro(
            onCadastroSucesso = { telaAtual = "login" },
            onVoltar = { telaAtual = "login" }
        )
        "home" -> TelaHome(
            nomeUsuario = nomeUsuarioLogado,
             emailUsuario = emailUsuarioLogado,
            onLogout = {
                // Limpa dados ao sair
                nomeUsuarioLogado = "Usuário"
                emailUsuarioLogado = ""
                telaAtual = "login"
            },
            onNavegarRotina = { telaAtual = "rotina" } // Vai para a lista de rotina
        )
        "rotina" -> TelaRotina(
            emailUsuario = emailUsuarioLogado, // Passa o email para buscar a lista certa
            onVoltar = { telaAtual = "home" },
            onAdicionarNovo = { telaAtual = "nova_rotina" } // Vai para o formulario de adicionar
        )
        "nova_rotina" -> TelaAdicionarRotina(
            emailUsuario = emailUsuarioLogado, // Passa o email logado para salvar no banco certo
            onVoltar = { telaAtual = "rotina" },
            onSalvarSucesso = { telaAtual = "rotina" } // Volta para a lista ao salvar
        )
    }
}

// --- TELA DE LOGIN ---
@Composable
fun TelaLogin(onNavegarCadastro: () -> Unit, onLoginSucesso: (String, String) -> Unit) { // Recebe (Email, senha)
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
                        val emailValidated = email.trim()
                        val res = RetrofitClient.api.login(LoginRequest(emailValidated, senha))

                        if (res.isSuccessful) {
                            val resposta = res.body()
                            if (resposta?.sucesso == true) {
                                val nomeRecebido = resposta.nomeUsuario ?: "Paciente"

                                Toast.makeText(context, "Olá, $nomeRecebido!", Toast.LENGTH_SHORT).show()

                                // Passa o nome E o email para a navegacao central
                                onLoginSucesso(nomeRecebido, emailValidated)
                            }
                        } else {
                            // ler a mensagem de erro do servidor
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