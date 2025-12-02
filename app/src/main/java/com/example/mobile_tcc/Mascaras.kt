package com.example.mobile_tcc

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

// Classe reutilizável para Máscara de Telefone (XX) XXXXX-XXXX
class MascaraTelefone : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // 1. Limpa tudo que não for número
        val trimmed = text.text.take(11) // Limita a 11 dígitos
        var out = ""

        // 2. Aplica a formatação caractere por caractere
        for (i in trimmed.indices) {
            out += when (i) {
                0 -> "("
                2 -> ") "
                7 -> "-"
                else -> ""
            }
            out += trimmed[i]
        }

        // 3. Mapeamento de cursor (para o cursor não pular errado ao apagar)
        val numberOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return offset
                if (offset <= 2) return offset + 1
                if (offset <= 7) return offset + 3
                if (offset <= 11) return offset + 4
                return 15
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return offset
                if (offset <= 2) return offset - 1
                if (offset <= 7) return offset - 3
                if (offset <= 11) return offset - 4
                return 11
            }
        }

        return TransformedText(AnnotatedString(out), numberOffsetTranslator)
    }
}