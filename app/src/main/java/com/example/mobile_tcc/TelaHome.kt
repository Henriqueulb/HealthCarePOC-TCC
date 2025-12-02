package com.example.mobile_tcc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// Cores do Projeto (Design Azul)
val AzulPrincipal = Color(0xFF2962FF)
val FundoCinzaClaro = Color(0xFFF5F5F5)

// --- TELA PRINCIPAL ---
@Composable
fun TelaHome(nomeUsuario: String, onLogout: () -> Unit, onNavegarRotina: () -> Unit) { // <--- NOVO PARÂMETRO
    // Estado para guardar os dados (Simulação de API)
    var resumo by remember { mutableStateOf<ResumoHome?>(null) }
    var carregando by remember { mutableStateOf(true) }

    // Simula carregamento de dados ao abrir a tela
    LaunchedEffect(Unit) {
        delay(1000) // Tempo fake de espera (1 segundo)
        resumo = ResumoHome(
            progresso = 0.65f, // 65% de Aderência
            tarefas = listOf(
                Tarefa(1, "Tomar Losartana", "08:00", true),
                Tarefa(2, "Medir Glicose", "10:00", true),
                Tarefa(3, "Almoço Saudável", "12:00", false),
                Tarefa(4, "Caminhada Leve", "17:00", false)
            )
        )
        carregando = false
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            // Barra de Navegação Flutuante
            BarraNavegacaoInferior(onClicarRotina = onNavegarRotina)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- CABEÇALHO (Mostra o nome dinâmico) ---
            CabecalhoUsuario(nome = nomeUsuario)

            Spacer(modifier = Modifier.height(24.dp))

            if (carregando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AzulPrincipal)
                }
            } else {
                // Conteúdo da tela (Gráficos e Listas)
                resumo?.let { dados ->
                    ConteudoPrincipal(dados)
                }
            }
        }
    }
}

// --- COMPONENTES VISUAIS ---

@Composable
fun CabecalhoUsuario(nome: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Foto e Saudação
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Placeholder para foto
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Color.LightGray
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Olá,",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = nome, // Nome exibido aqui
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Botões de Ação (Sino e Config)
        Row {
            BotaoIcone(Icons.Default.Notifications)
            Spacer(modifier = Modifier.width(8.dp))
            BotaoIcone(Icons.Default.Settings)
        }
    }
}

@Composable
fun BotaoIcone(icone: ImageVector) {
    IconButton(
        onClick = { },
        modifier = Modifier
            .size(40.dp)
            .background(Color(0xFFE3F2FD), CircleShape)
    ) {
        Icon(icone, contentDescription = null, tint = AzulPrincipal)
    }
}

@Composable
fun BarraNavegacaoInferior(onClicarRotina: () -> Unit) { // Recebe a função de clique
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(64.dp),
        shape = RoundedCornerShape(50), // Borda redonda estilo "cápsula"
        color = AzulPrincipal,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconeNavegacao(Icons.Default.Home, true, onClick = {})
            IconeNavegacao(Icons.Default.Email, false, onClick = {})
            IconeNavegacao(Icons.Default.Person, false, onClick = {})
            // O ícone de calendário chama a navegação!
            IconeNavegacao(Icons.Default.DateRange, false, onClick = onClicarRotina)
        }
    }
}

@Composable
fun IconeNavegacao(icone: ImageVector, selecionado: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icone,
            contentDescription = null,
            tint = if (selecionado) Color.White else Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun ConteudoPrincipal(dados: ResumoHome) {
    LazyColumn {
        // Card de Progresso
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Progresso Diário", fontWeight = FontWeight.SemiBold, color = AzulPrincipal)
                        Text("${(dados.progresso * 100).toInt()}%", fontWeight = FontWeight.Bold, color = AzulPrincipal)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { dados.progresso },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = AzulPrincipal,
                        trackColor = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Botões de Atalho
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                // Atalhos com ícones padrão (Favorite = Coração, List = Lista)
                CardAtalho("Ficha Médica", Icons.Default.Favorite, Color(0xFFFFEBEE), Color(0xFFD32F2F))
                CardAtalho("Relatórios", Icons.Default.List, Color(0xFFE8F5E9), Color(0xFF388E3C))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Lista de Tarefas
        item {
            Text("Próximos Cuidados", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(dados.tarefas) { tarefa ->
            ItemTarefaHome(tarefa)
        }

        // Espaço extra para a barra de navegação não cobrir o último item
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ItemTarefaHome(tarefa: Tarefa) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = FundoCinzaClaro),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (tarefa.feita) AzulPrincipal else Color.White)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                if (tarefa.feita) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = tarefa.titulo,
                    fontWeight = FontWeight.SemiBold,
                    color = if (tarefa.feita) Color.Gray else Color.Black
                )
                Text(
                    text = tarefa.horario,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun CardAtalho(titulo: String, icone: ImageVector, corFundo: Color, corIcone: Color) {
    Card(
        modifier = Modifier
            .width(165.dp)
            .height(90.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = corFundo)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icone, contentDescription = null, tint = corIcone, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(titulo, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = corIcone)
        }
    }
}