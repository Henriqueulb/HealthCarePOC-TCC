package com.example.mobile_tcc

import android.widget.Toast
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

@Composable
fun TelaCadastro(onCadastroSucesso: () -> Unit, onVoltar: () -> Unit) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Criar Nova Conta", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { novo ->
                nome = novo.replace(Regex("[\\r\\n\\t]"), "")
            },
            label = { Text("Nome completo") },
            placeholder = { Text("Ex: João Silva Souza") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // CAMPO DE E-MAIL-
        OutlinedTextField(
            value = email,
            onValueChange = { novoTexto ->
                email = novoTexto.filter { !it.isWhitespace() }
            },
            label = { Text("E-mail") },
            placeholder = { Text("exemplo@email.com") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha (Mín 8 caracteres)") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // CAMPO DE TELEFONE
        OutlinedTextField(
            value = telefone,
            onValueChange = { novoTexto ->
                val apenasNumeros = novoTexto.filter { it.isDigit() }
                if (apenasNumeros.length <= 11) {
                    telefone = apenasNumeros
                }
            },
            label = { Text("Telefone Celular") },
            placeholder = { Text("(99) 99999-9999") },
            // Aplica a formatacao
            visualTransformation = MascaraTelefone(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        // ----------------------------------------------

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Validacoes antes de enviar
                if (nome.trim().length < 5 || !nome.trim().contains(" ")) {
                    Toast.makeText(context, "Informe o nome completo", Toast.LENGTH_SHORT).show()
                } else if (telefone.length < 11) {
                    Toast.makeText(context, "Preencha o telefone completo com DDD", Toast.LENGTH_SHORT).show()
                } else if (!email.contains("@") || !email.contains(".")) {
                    Toast.makeText(context, "E-mail inválido", Toast.LENGTH_SHORT).show()
                } else if (senha.length < 8) {
                    Toast.makeText(context, "A senha deve ter no mínimo 8 caracteres", Toast.LENGTH_SHORT).show()
                } else {
                    scope.launch {
                        try {
                            val req = CadastroRequest(nome.trim(), email.trim(), senha, telefone)
                            val res = RetrofitClient.api.cadastro(req)

                            if (res.isSuccessful && res.body()?.sucesso == true) {
                                Toast.makeText(context, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                                onCadastroSucesso()
                            } else {
                                val erro = res.errorBody()?.string() ?: res.body()?.mensagem ?: "Resposta vazia"
                                Toast.makeText(context, "Erro: $erro", Toast.LENGTH_LONG).show()
                                println("ERRO CADASTRO: $erro")

                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Erro de Conexão: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar")
        }

        TextButton(onClick = onVoltar) {
            Text("Já tenho conta. Fazer Login")
        }
    }
}