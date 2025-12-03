package com.example.mobile_tcc

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.livedata.observeAsState
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaRotina(navController: NavController, emailUsuario: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var listaTarefas by remember { mutableStateOf<List<ItemRotinaDTO>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }

    // Verifica se a tela anterior mandou um pedido de "refresh"
    val precisaAtualizar = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Boolean>("refresh")
        ?.observeAsState()

    // Funcao que busca
    fun carregarDados() {
        scope.launch {
            try {
                carregando = true
                val response = RetrofitClient.api.getHome(emailUsuario)

                if (response.isSuccessful) {
                    val dados = response.body()
                    if (dados != null) {
                        listaTarefas = dados.tarefas

                        dados.tarefas.forEach { item ->
                            if (!item.feita) {
                                AgendadorNotificacoes.agendarAlarme(
                                    context,
                                    item.id,
                                    item.horario,
                                    item.titulo
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Erro de conexão", Toast.LENGTH_SHORT).show()
            } finally {
                carregando = false
            }
        }
    }

    // Carrega na primeira vez
    LaunchedEffect(Unit) {
        carregarDados()
    }

    // Carrega se receber o sinal de "refresh"
    LaunchedEffect(precisaAtualizar?.value) {
        if (precisaAtualizar?.value == true) {
            carregarDados()
            navController.currentBackStackEntry?.savedStateHandle?.set("refresh", false)
        }
    }

    // Funcoes de Acao
    fun deletarItem(idItem: Int) {
        scope.launch {
            try {
                val response = RetrofitClient.api.deletarRotina(idItem)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Item removido!", Toast.LENGTH_SHORT).show()
                    listaTarefas = listaTarefas.filter { it.id != idItem }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Erro ao deletar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun atualizarStatus(item: ItemRotinaDTO, novoStatus: Boolean) {
        scope.launch {
            try {
                listaTarefas = listaTarefas.map { if (it.id == item.id) it.copy(feita = novoStatus) else it }

                val hoje = LocalDate.now().toString()
                val dto = StatusRotinaDTO(item.id, novoStatus, hoje)
                RetrofitClient.api.atualizarStatus(dto)
            } catch (e: Exception) {
                // Reverte em caso de erro
                listaTarefas = listaTarefas.map { if (it.id == item.id) it.copy(feita = !novoStatus) else it }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minha Rotina", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("adicionar_rotina/$emailUsuario") },
                containerColor = Color(0xFF0D47A1),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (carregando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (listaTarefas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma tarefa pendente!", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listaTarefas) { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4F8)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Checkbox(
                                        checked = item.feita,
                                        onCheckedChange = { isChecked -> atualizarStatus(item, isChecked) },
                                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0D47A1))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = item.titulo,
                                            fontWeight = FontWeight.Bold,
                                            color = if(item.feita) Color.Gray else Color.Black
                                        )
                                        Text(
                                            text = "${item.horario} - ${item.dose ?: ""}",
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                IconButton(onClick = { deletarItem(item.id) }) {
                                    Icon(Icons.Default.Delete, "Apagar", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}