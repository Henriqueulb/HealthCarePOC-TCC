package com.example.mobile_tcc

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar

object AgendadorNotificacoes {

    fun agendarAlarme(context: Context, idItem: Int, horario: String, nomeRemedio: String) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val partes = horario.split(":")
            val hora = partes[0].toInt()
            val minuto = partes[1].toInt()
            val calendario = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hora)
                set(Calendar.MINUTE, minuto)
                set(Calendar.SECOND, 0)
            }

            if (calendario.timeInMillis < System.currentTimeMillis()) {
                calendario.add(Calendar.DAY_OF_YEAR, 1)
            }

            val intent = Intent(context, NotificacaoReceiver::class.java).apply {
                putExtra("titulo", "Hora de tomar: $nomeRemedio")
                putExtra("mensagem", "Abra o app para registrar seu cuidado.")
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                idItem,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Define o alarme exato
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendario.timeInMillis,
                pendingIntent
            )

            Log.d("Agendador", "Alarme definido para $nomeRemedio as $horario")

        } catch (e: Exception) {
            Log.e("Agendador", "Erro ao agendar: ${e.message}")
        }
    }
}