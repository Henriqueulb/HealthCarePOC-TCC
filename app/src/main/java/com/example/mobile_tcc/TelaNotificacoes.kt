package com.example.mobile_tcc

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaNotificacoes(navController: NavController, emailUsuario: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var notificacoesAtivas by remember { mutableStateOf(true) }
    var somAtivo by remember { mutableStateOf(true) }
    var carregando by remember { mutableStateOf(true) }

    // Carregar configuracoes do Back-end
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getConfigNotificacao(emailUsuario)
                if (response.isSuccessful) {
                    val config = response.body()
                    if (config != null) {
                        notificacoesAtivas = config.ativo
                        somAtivo = config.som
                    }
                }
            } catch (e: Exception) {
            } finally {
                carregando = false
            }
        }
    }

    fun salvarConfig() {
        scope.launch {
            try {
                val dto = NotificacaoConfigDTO(emailUsuario, notificacoesAtivas, somAtivo)
                RetrofitClient.api.salvarConfigNotificacao(dto)
                Toast.makeText(context, "Configurações salvas", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            } catch (e: Exception) {
                Toast.makeText(context, "Erro ao salvar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificações", color = Color.White) },
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
                .padding(24.dp)
        ) {
            Text("Preferências de Alerta", style = MaterialTheme.typography.titleMedium, color = Color(0xFF0D47A1))
            Spacer(modifier = Modifier.height(16.dp))

            // Ativar Notificações
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Receber Lembretes", style = MaterialTheme.typography.bodyLarge)
                    Text("Alertar nos horários da rotina", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Switch(
                    checked = notificacoesAtivas,
                    onCheckedChange = { notificacoesAtivas = it },
                    colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF0D47A1))
                )
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Som
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Emitir Som", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = somAtivo,
                    onCheckedChange = { somAtivo = it },
                    enabled = notificacoesAtivas,
                    colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF0D47A1))
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { salvarConfig() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))
            ) {
                Text("SALVAR PREFERÊNCIAS")
            }
        }
    }
}