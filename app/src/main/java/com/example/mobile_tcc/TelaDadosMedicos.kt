package com.example.mobile_tcc

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDadosMedicos(navController: NavController, emailUsuario: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Estados
    var alergias by remember { mutableStateOf("") }
    var medicacoes by remember { mutableStateOf("") }
    var comorbidades by remember { mutableStateOf("") }
    var carregando by remember { mutableStateOf(true) }

    // Carregar dados iniciais
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getFichaMedica(emailUsuario)
                if (response.isSuccessful) {
                    val dados = response.body()
                    if (dados != null) {
                        alergias = dados.alergias
                        medicacoes = dados.medicacoes
                        comorbidades = dados.comorbidades
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Sem conexão", Toast.LENGTH_SHORT).show()
            } finally {
                carregando = false
            }
        }
    }

    fun salvarDados() {
        scope.launch {
            carregando = true
            try {
                val dto = FichaMedicaDTO(
                    emailUsuario = emailUsuario,
                    alergias = alergias,
                    medicacoes = medicacoes,
                    comorbidades = comorbidades
                )

                val response = RetrofitClient.api.salvarFichaMedica(dto)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Ficha atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                } else {
                    Toast.makeText(context, "Erro ao salvar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Erro de conexão", Toast.LENGTH_SHORT).show()
            } finally {
                carregando = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dados Médicos", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1))
            )
        }
    ) { paddingValues ->
        if (carregando && alergias.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()), // Permite rolar
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text("Preencha as informações clínicas importantes para casos de emergência ou consulta.")

                // Alergias
                OutlinedTextField(
                    value = alergias,
                    onValueChange = { alergias = it },
                    label = { Text("Alergias") },
                    placeholder = { Text("Ex: Dipirona, Iodo, Frutos do mar...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5
                )

                // Comorbidades
                OutlinedTextField(
                    value = comorbidades,
                    onValueChange = { comorbidades = it },
                    label = { Text("Comorbidades / Doenças Pré-existentes") },
                    placeholder = { Text("Ex: Diabetes Tipo 2, Hipertensão, Asma...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5
                )

                // Medicações Contínuas
                OutlinedTextField(
                    value = medicacoes,
                    onValueChange = { medicacoes = it },
                    label = { Text("Medicações de Uso Contínuo") },
                    placeholder = { Text("Ex: Losartana 50mg (Manhã)...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { salvarDados() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                    enabled = !carregando
                ) {
                    if (carregando) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Icon(Icons.Default.MedicalServices, null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SALVAR DADOS MÉDICOS")
                    }
                }
            }
        }
    }
}