package com.example.mobile_tcc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaRotina(emailUsuario: String, onVoltar: () -> Unit, onAdicionarNovo: () -> Unit) {
    var listaRotina by remember { mutableStateOf<List<Tarefa>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }
    var erro by remember { mutableStateOf<String?>(null) }

    // Carregar dados
    LaunchedEffect(Unit) {
        try {
            val res = RetrofitClient.api.getHome(emailUsuario)
            if (res.isSuccessful && res.body() != null) {
                listaRotina = res.body()!!.tarefas
            } else {
                erro = "Erro ao carregar rotina"
            }
        } catch (e: Exception) {
            erro = "Sem conexão: ${e.message}"
        } finally {
            carregando = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rotina de Hoje", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = AzulPrincipal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAdicionarNovo,
                containerColor = AzulPrincipal,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Cuidado")
            }
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Barra de progresso do dia
            val total = listaRotina.size
            val feitos = listaRotina.count { it.feita }
            val progresso = if (total > 0) feitos.toFloat() / total else 0f

            CardProgressoRotina(feitos, total, progresso)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Cronograma", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            if (carregando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AzulPrincipal)
                }
            } else if (erro != null) {
                Text("Erro: $erro", color = Color.Red)
            } else if (listaRotina.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(bottom = 100.dp), contentAlignment = Alignment.Center) {
                    Text("Nenhuma tarefa para hoje! Adicione (+)", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(listaRotina) { item ->
                        CardItemRotina(
                            item = item,
                            onCheckChange = {
                                // TODO: Implementar lógica de marcar como feito no banco
                                // Por enquanto só atualiza visualmente localmente
                                listaRotina = listaRotina.map {
                                    if (it.id == item.id) it.copy(feita = !it.feita) else it
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CardProgressoRotina(feitos: Int, total: Int, progresso: Float) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Progresso Diário", fontWeight = FontWeight.Bold, color = AzulPrincipal)
                Text("$feitos de $total tarefas concluídas", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progresso },
                    modifier = Modifier.size(50.dp),
                    color = AzulPrincipal,
                    trackColor = Color.White,
                )
                Text(
                    text = "${(progresso * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AzulPrincipal
                )
            }
        }
    }
}

@Composable
fun CardItemRotina(item: Tarefa, onCheckChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.feita) Color(0xFFF0F9EB) else FundoCinzaClaro
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(50.dp)
            ) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.horario, fontWeight = FontWeight.Bold, color = AzulPrincipal)
            }

            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.LightGray))
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.titulo,
                    fontWeight = FontWeight.SemiBold,
                    color = if (item.feita) Color.Gray else Color.Black,
                    style = MaterialTheme.typography.bodyLarge
                )
                // Exibe a Dose
                if (!item.dose.isNullOrBlank()) {
                    Text(
                        text = "Dose: ${item.dose}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }

            IconButton(
                onClick = { onCheckChange(!item.feita) },
                modifier = Modifier
                    .background(
                        if (item.feita) AzulPrincipal else Color.White,
                        CircleShape
                    )
                    .size(32.dp)
                    .padding(4.dp)
            ) {
                if (item.feita) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}