package com.example.mobile_tcc

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaConfiguracoes(navController: NavController, emailUsuario: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var mostrarDialogExclusao by remember { mutableStateOf(false) }
    var carregando by remember { mutableStateOf(false) }

    fun deletarConta() {
        scope.launch {
            carregando = true
            try {
                val response = RetrofitClient.api.deletarConta(emailUsuario)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Conta excluída.", Toast.LENGTH_LONG).show()
                    // Volta para o login e limpa tudo
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                } else {
                    Toast.makeText(context, "Erro ao excluir conta.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Erro de conexão.", Toast.LENGTH_SHORT).show()
            } finally {
                carregando = false
                mostrarDialogExclusao = false
            }
        }
    }

    // UI PRINCIPAL
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações", color = Color.White) },
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
                .padding(16.dp)
        ) {

            // Trocar Senha
            ItemConfiguracao(
                icone = Icons.Default.Lock,
                titulo = "Trocar Senha",
                onClick = { navController.navigate("trocar_senha/$emailUsuario") }
            )

            Divider()

            // Notificações
            ItemConfiguracao(
                icone = Icons.Default.Notifications,
                titulo = "Notificações",
                onClick = { Toast.makeText(context, "Em breve", Toast.LENGTH_SHORT).show() }
            )

            Divider()

            Spacer(modifier = Modifier.height(32.dp))

            // Deletar Conta
            Button(
                onClick = { mostrarDialogExclusao = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
            ) {
                Icon(Icons.Default.DeleteForever, null, tint = Color.Red)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Deletar Minha Conta", color = Color.Red)
            }
        }
    }

    //  DIALOG DE CONFIRMAÇÃO
    if (mostrarDialogExclusao) {
        AlertDialog(
            onDismissRequest = { mostrarDialogExclusao = false },
            title = { Text("Tem certeza?") },
            text = { Text("Essa ação apagará todos os seus dados, histórico e rotinas permanentemente. Não é possível desfazer.") },
            confirmButton = {
                TextButton(
                    onClick = { deletarConta() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("SIM, DELETAR")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogExclusao = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ItemConfiguracao(icone: ImageVector, titulo: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icone, null, tint = Color(0xFF0D47A1))
        Spacer(modifier = Modifier.width(16.dp))
        Text(titulo, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.Gray)
    }
}