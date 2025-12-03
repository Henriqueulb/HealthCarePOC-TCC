package com.example.mobile_tcc

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAdicionarRotina(navController: NavController, emailUsuario: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var nomeMedicamento by remember { mutableStateOf("") }
    var dose by remember { mutableStateOf("") }
    var horario by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var carregando by remember { mutableStateOf(false) }

    fun salvarRotina() {
        if (nomeMedicamento.isBlank() || horario.length < 5) { // Validação simples HH:mm
            Toast.makeText(context, "Preencha Nome e Horário completo", Toast.LENGTH_SHORT).show()
            return
        }

        scope.launch {
            carregando = true
            try {
                val dto = NovaRotinaDTO(
                    emailUsuario = emailUsuario,
                    titulo = nomeMedicamento,
                    horario = horario, // Já vai estar formatado
                    dose = dose,
                    descricao = descricao
                )

                val response = RetrofitClient.api.criarRotina(dto)

                if (response.isSuccessful) {
                    Toast.makeText(context, "Salvo com sucesso!", Toast.LENGTH_SHORT).show()

                    // Avisa a tela anterior para atualizar a lista
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("refresh", true)

                    navController.popBackStack()
                } else {
                    Toast.makeText(context, "Erro ao salvar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Erro de conexão: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                carregando = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adicionar Item", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nomeMedicamento,
                onValueChange = { nomeMedicamento = it },
                label = { Text("Nome do Medicamento") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dose,
                onValueChange = { dose = it },
                label = { Text("Dose (ex: 1cp, 50mg)") },
                modifier = Modifier.fillMaxWidth()
            )

            // CAMPO HORARIO
            OutlinedTextField(
                value = horario,
                onValueChange = { novoTexto ->
                    if (novoTexto.length <= 5) {
                        horario = Mascaras.formatarHora(novoTexto)
                    }
                },
                label = { Text("Horário (HH:mm)") },
                placeholder = { Text("08:00") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Observação") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { salvarRotina() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                enabled = !carregando
            ) {
                if (carregando) CircularProgressIndicator(color = Color.White) else Text("SALVAR")
            }
        }
    }
}