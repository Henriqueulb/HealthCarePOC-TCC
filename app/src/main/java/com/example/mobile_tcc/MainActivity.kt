package com.example.mobile_tcc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mobile_tcc.ui.theme.Mobile_TCCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Mobile_TCCTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    // Cria o controlador de navegação (o "cérebro" das rotas)
    val navController = rememberNavController()

    // Define o mapa de navegação, começando pelo login
    NavHost(navController = navController, startDestination = "login") {

        // --- TELA DE LOGIN ---
        composable("login") {
            TelaLogin(navController)
        }

        // --- TELA DE CADASTRO ---
        composable("cadastro") {
            TelaCadastro(navController)
        }

        // --- TELA HOME (Recebe o email do usuário logado) ---
        composable(
            route = "home/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            // Recupera o email passado na rota
            val email = backStackEntry.arguments?.getString("email") ?: ""
            TelaHome(navController, email)
        }

        // --- TELA DE ROTINA (Lista de Cuidados) ---
        composable(
            route = "rotina/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            TelaRotina(navController, email)
        }

        // --- TELA DE ADICIONAR ITEM NA ROTINA ---
        composable(
            route = "adicionar_rotina/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            TelaAdicionarRotina(navController, email)
        }
    }
}