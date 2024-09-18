package com.example.examen1

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CitaActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var spinnerHorario: Spinner
    private lateinit var buttonAgendar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cita)

        calendarView = findViewById(R.id.Fecha)
        spinnerHorario = findViewById(R.id.Horario)
        buttonAgendar = findViewById(R.id.btnAgendar)

        val horarios = resources.getStringArray(R.array.horarios_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, horarios)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerHorario.adapter = adapter

        buttonAgendar.setOnClickListener {
            mostrarDatos()
        }
    }

    private fun mostrarDatos() {
        val fechaMillis = calendarView.date
        val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(fechaMillis))
        val horario = spinnerHorario.selectedItem.toString()

        Toast.makeText(this, "Cita agendada para el $fecha a las $horario", Toast.LENGTH_LONG).show()
    }
}