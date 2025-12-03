package com.example.mobile_tcc

object Mascaras {

    fun formatarTelefone(texto: String): String {
        val numeros = texto.filter { it.isDigit() }
        val limitado = if (numeros.length > 11) numeros.substring(0, 11) else numeros

        return when {
            limitado.length > 10 -> {
                "(${limitado.substring(0, 2)}) ${limitado.substring(2, 7)}-${limitado.substring(7)}"
            }
            limitado.length > 6 -> {
                "(${limitado.substring(0, 2)}) ${limitado.substring(2, 6)}-${limitado.substring(6)}"
            }
            limitado.length > 2 -> {
                "(${limitado.substring(0, 2)}) ${limitado.substring(2)}"
            }
            else -> limitado
        }
    }

    fun formatarHora(texto: String): String {
        val numeros = texto.filter { it.isDigit() }
        val limitado = if (numeros.length > 4) numeros.substring(0, 4) else numeros

        return when {
            limitado.length >= 3 -> {
                "${limitado.substring(0, 2)}:${limitado.substring(2)}"
            }
            else -> limitado
        }
    }
}