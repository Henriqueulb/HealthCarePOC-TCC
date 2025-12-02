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

// --- MODELO DE DADOS LOCAL PARA A TELA ---
data class ItemRotina(
    val id: Int,
    val horario: String,
    val titulo: String,
    val descricao: String,
    var realizado: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaRotina(onVoltar: () -> Unit) {
    // Dados de exemplo (Mock)
    // Futuramente isso virá do Backend (ApiService.getRotina)
    val listaRotina = remember {
        mutableStateListOf(
            ItemRotina(1, "08:00", "Losartana", "50mg - 1 Comprimido", true),
            ItemRotina(2, "08:30", "Café da Manhã", "Evitar açúcar", true),
            ItemRotina(3, "10:00", "Medir Glicose", "Anotar o valor", false),
            ItemRotina(4, "12:00", "Almoço", "Prato equilibrado", false),
            ItemRotina(5, "14:00", "Dipirona", "Se houver dor de cabeça", false),
            ItemRotina(6, "17:00", "Caminhada", "30 minutos leve", false),
            ItemRotina(7, "20:00", "Insulina", "Aplicar conforme tabela", false)
        )
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
                onClick = { /* Ação para adicionar novo cuidado (Futuro) */ },
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
            val feitos = listaRotina.count { it.realizado }
            val progresso = if (total > 0) feitos.toFloat() / total else 0f

            CardProgressoRotina(feitos, total, progresso)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Cronograma", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp) // Espaço para o FAB não cobrir o último item
            ) {
                items(listaRotina) { item ->
                    CardItemRotina(
                        item = item,
                        onCheckChange = { novoStatus ->
                            // Atualiza a lista para refletir a mudança na UI
                            val index = listaRotina.indexOf(item)
                            listaRotina[index] = item.copy(realizado = novoStatus)
                        }
                    )
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

            // Círculo de progresso ou Texto
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
fun CardItemRotina(item: ItemRotina, onCheckChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.realizado) Color(0xFFF0F9EB) else FundoCinzaClaro // Verde claro se feito
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Coluna do Horário
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(50.dp)
            ) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.horario, fontWeight = FontWeight.Bold, color = AzulPrincipal)
            }

            // Divisória vertical
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.LightGray))
            Spacer(modifier = Modifier.width(12.dp))

            // Conteúdo
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.titulo,
                    fontWeight = FontWeight.SemiBold,
                    color = if (item.realizado) Color.Gray else Color.Black,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = item.descricao,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Checkbox
            IconButton(
                onClick = { onCheckChange(!item.realizado) },
                modifier = Modifier
                    .background(
                        if (item.realizado) AzulPrincipal else Color.White,
                        CircleShape
                    )
                    .size(32.dp)
                    .padding(4.dp)
            ) {
                if (item.realizado) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}