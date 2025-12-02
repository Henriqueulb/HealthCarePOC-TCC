package com.example.mobile_tcc

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// Mascara para Hora (HH:MM)
class MascaraHora : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(4)
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1) out += ":"
        }

        val numberOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 4) return offset + 1
                return 5
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 4) return offset - 1
                return 4
            }
        }
        return TransformedText(AnnotatedString(out), numberOffsetTranslator)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAdicionarRotina(emailUsuario: String, onVoltar: () -> Unit, onSalvarSucesso: () -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var horario by remember { mutableStateOf("") }
    var dose by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var salvando by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Cuidado") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("O que você precisa fazer?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = AzulPrincipal)

            Spacer(modifier = Modifier.height(24.dp))

            // Campo Titulo
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Nome do Medicamento ou Ação") },
                placeholder = { Text("Ex: Losartana") },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                // Campo Horario
                OutlinedTextField(
                    value = horario,
                    onValueChange = {
                        if (it.length <= 4) horario = it.filter { char -> char.isDigit() }
                    },
                    label = { Text("Horário") },
                    placeholder = { Text("08:00") },
                    visualTransformation = MascaraHora(), // Aplica a mascara
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Campo Dose
                OutlinedTextField(
                    value = dose,
                    onValueChange = { dose = it },
                    label = { Text("Dose") },
                    placeholder = { Text("Ex: 50mg") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Descrição
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Instruções (Opcional)") },
                placeholder = { Text("Ex: Tomar em jejum") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (titulo.isBlank() || horario.length < 4) {
                        Toast.makeText(context, "Preencha o nome e o horário completo (HH:MM)", Toast.LENGTH_SHORT).show()
                    } else {
                        // Formata o horário
                        val horarioFormatado = "${horario.substring(0,2)}:${horario.substring(2,4)}"

                        salvando = true
                        scope.launch {
                            try {
                                val req = NovaRotinaRequest(
                                    emailUsuario = emailUsuario,
                                    titulo = titulo,
                                    horario = horarioFormatado,
                                    dose = dose,
                                    descricao = descricao
                                )
                                val res = RetrofitClient.api.criarRotina(req)

                                if (res.isSuccessful && res.body()?.sucesso == true) {
                                    Toast.makeText(context, "Salvo com sucesso!", Toast.LENGTH_SHORT).show()
                                    onSalvarSucesso()
                                } else {
                                    Toast.makeText(context, "Erro: ${res.body()?.mensagem}", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erro de conexão: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                salvando = false
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
                enabled = !salvando
            ) {
                if (salvando) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Salvar na Rotina", fontSize = 18.sp)
                }
            }
        }
    }
}