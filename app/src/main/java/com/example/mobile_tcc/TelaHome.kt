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
import kotlinx.coroutines.launch

// Cores do Projeto
val AzulPrincipal = Color(0xFF2962FF)
val FundoCinzaClaro = Color(0xFFF5F5F5)

// TELA PRINCIPAL
@Composable
fun TelaHome(nomeUsuario: String, emailUsuario: String, onLogout: () -> Unit, onNavegarRotina: () -> Unit) {

    var resumo by remember { mutableStateOf<ResumoHome?>(null) }
    var carregando by remember { mutableStateOf(true) }
    var erro by remember { mutableStateOf<String?>(null) }

    // Busca dados
    LaunchedEffect(Unit) {
        try {
            val res = RetrofitClient.api.getHome(emailUsuario)
            if (res.isSuccessful && res.body() != null) {
                resumo = res.body()
            } else {
                erro = "Não foi possível carregar sua rotina."
            }
        } catch (e: Exception) {
            erro = "Erro de conexão: ${e.message}"
        } finally {
            carregando = false
        }
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
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

            // Cabecalho com Nome
            CabecalhoUsuario(nome = nomeUsuario)

            Spacer(modifier = Modifier.height(24.dp))

            if (carregando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AzulPrincipal)
                }
            } else if (erro != null) {
                // Mensagem de erro
                Text(text = erro!!, color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                // Conteúdo Principal
                resumo?.let { dados ->
                    ConteudoPrincipal(dados)
                }
            }
        }
    }
}

// CABEÇALHO
@Composable
fun CabecalhoUsuario(nome: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
                    text = nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

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

// BARRA INFERIOR
@Composable
fun BarraNavegacaoInferior(onClicarRotina: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(64.dp),
        shape = RoundedCornerShape(50),
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
            // icone de Calendario leva para a Tela Rotina
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

// CONTEÚDO PRINCIPAL
@Composable
fun ConteudoPrincipal(dados: ResumoHome) {
    LazyColumn {
        // Progresso
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

        // Atalhos
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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

        if (dados.tarefas.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                    Text("Nenhuma tarefa para hoje. Adicione na aba Rotina!", color = Color.Gray)
                }
            }
        } else {
            items(dados.tarefas) { tarefa ->
                ItemTarefaHome(tarefa)
            }
        }

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
            // Checkbox visual
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
                // Exibe Horario e Dose
                val detalhes = if (tarefa.dose.isNullOrBlank()) {
                    tarefa.horario
                } else {
                    "${tarefa.horario} • ${tarefa.dose}"
                }

                Text(
                    text = detalhes,
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