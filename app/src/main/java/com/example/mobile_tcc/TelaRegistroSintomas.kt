package com.example.mobile_tcc

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaRegistroSintomas(navController: NavController, emailUsuario: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Estados dos Sliders (Iniciam no meio: 5)
    var bemEstar by remember { mutableStateOf(5f) }
    var sintomas by remember { mutableStateOf(5f) }
    var carregando by remember { mutableStateOf(false) }

    fun salvarSintomas() {
        scope.launch {
            carregando = true
            try {
                val dto = NovoSintomaDTO(
                    emailUsuario = emailUsuario,
                    bemEstar = bemEstar.toInt(),
                    sintomas = sintomas.toInt()
                )

                val response = RetrofitClient.api.registrarSintoma(dto)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Registrado com sucesso!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack() // Volta para a Home
                } else {
                    Toast.makeText(context, "Erro ao registrar.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Erro de conexão.", Toast.LENGTH_SHORT).show()
            } finally {
                carregando = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diário de Bem-Estar", color = Color.White) },
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
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Como você está hoje?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D47A1),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // --- ESCALA BEM-ESTAR ---
            Text("Nível de Bem-Estar Geral", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Face, "Rosto", tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${bemEstar.toInt()}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if(bemEstar < 4) Color.Red else Color(0xFF2E7D32)
                )
                Text("/10", fontSize = 16.sp, color = Color.Gray)
            }
            Slider(
                value = bemEstar,
                onValueChange = { bemEstar = it },
                valueRange = 0f..10f,
                steps = 9, // Passos discretos (0,1,2...10)
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF0D47A1),
                    activeTrackColor = Color(0xFF0D47A1)
                )
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Péssimo", fontSize = 12.sp, color = Color.Gray)
                Text("Ótimo", fontSize = 12.sp, color = Color.Gray)
            }

            Divider(modifier = Modifier.padding(vertical = 24.dp))

            // --- ESCALA SINTOMAS ---
            Text("Intensidade dos Sintomas", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, "Alerta", tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${sintomas.toInt()}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if(sintomas > 6) Color.Red else Color(0xFF2E7D32)
                )
                Text("/10", fontSize = 16.sp, color = Color.Gray)
            }
            Slider(
                value = sintomas,
                onValueChange = { sintomas = it },
                valueRange = 0f..10f,
                steps = 9,
                colors = SliderDefaults.colors(
                    thumbColor = if(sintomas > 6) Color.Red else Color(0xFFFFA000),
                    activeTrackColor = if(sintomas > 6) Color.Red else Color(0xFFFFA000)
                )
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Nenhum", fontSize = 12.sp, color = Color.Gray)
                Text("Insuportável", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { salvarSintomas() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                enabled = !carregando
            ) {
                if (carregando) CircularProgressIndicator(color = Color.White) else Text("REGISTRAR DIÁRIO")
            }
        }
    }
}