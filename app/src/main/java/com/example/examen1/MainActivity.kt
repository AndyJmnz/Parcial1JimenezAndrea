package com.example.examen1

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val CHANNEL_ID = "prestamo_canal"
    val listaSolicitantes = mutableListOf<Solicitud>()
    lateinit var CURP: EditText
    lateinit var Nombre: EditText
    lateinit var Apellidos: EditText
    lateinit var Domicilio: EditText
    lateinit var CantidadIngreso: EditText
    lateinit var TipoPrestamo: Spinner
    lateinit var Validar: Button
    lateinit var Limpiar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CURP = findViewById(R.id.editTextCURP)
        Nombre = findViewById(R.id.editTextNombre)
        Apellidos = findViewById(R.id.editTextApellidos)
        Domicilio = findViewById(R.id.editTextDomicilio)
        CantidadIngreso = findViewById(R.id.editTextCantidadIngreso)
        TipoPrestamo = findViewById(R.id.spinnerTipoPrestamo)
        Validar = findViewById(R.id.buttonValidar)
        Limpiar = findViewById(R.id.buttonLimpiar)


        val spinner: Spinner = findViewById(R.id.spinnerTipoPrestamo)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.tipo_prestamo_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        Validar.setOnClickListener {
            registrarSolicitud()
        }
        Limpiar.setOnClickListener {
            limpiarcampos()
        }
    }

    private fun limpiarcampos() {
        CURP.text.clear()
        Nombre.text.clear()
        Apellidos.text.clear()
        Domicilio.text.clear()
        CantidadIngreso.text.clear()
        TipoPrestamo.setSelection(0)
    }

    private fun registrarSolicitud() {
        val curp = CURP.text.toString()
        val nombre = Nombre.text.toString()
        val apellidos = Apellidos.text.toString()
        val domicilio = Domicilio.text.toString()
        val cantidadIngreso = CantidadIngreso.text.toString()
        val tipoPrestamo = TipoPrestamo.selectedItem.toString()

        if (curp.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || domicilio.isEmpty() || cantidadIngreso.isEmpty()) {
            Toast.makeText(this, "No puede haber campos vacios", Toast.LENGTH_SHORT).show()
            return
        }

        val Ingreso = cantidadIngreso.toDoubleOrNull() ?: run {
            Toast.makeText(this, "Cantidad de ingreso inválida", Toast.LENGTH_SHORT).show()
            return
        }

        val solicitante = Solicitud(curp, nombre, apellidos, domicilio, Ingreso, tipoPrestamo)
        listaSolicitantes.add(solicitante)

        if (validarIngreso(Ingreso, tipoPrestamo)) {
            crearCanalDeNotificacion()
            lanzarNotificacionAprobada()
        } else {
            crearCanalDeNotificacion()
            lanzarNotificacionDenegada()
        }

    }

    private fun validarIngreso(ingreso: Double, tipoPrestamo: String): Boolean {
        if (tipoPrestamo == "Personal") {
            return ingreso >= 20000.0 && ingreso <= 40000.0
        } else if (tipoPrestamo == "Negocio") {
            return ingreso > 40000.0 && ingreso <= 60000.0
        } else if (tipoPrestamo == "Vivienda") {
            return ingreso >= 15000.0 && ingreso <= 35000.0
        } else {
            return false
        }
    }

    private fun lanzarNotificacionAprobada() {
        val intentCita = Intent(this, CitaActivity::class.java)
        val pendingIntentCita = PendingIntent.getActivity(
            this,
            0,
            intentCita,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val intentPrestamos = Intent(this, PrestamosActivity::class.java)
        val pendingIntentPrestamos = PendingIntent.getActivity(
            this,
            0,
            intentPrestamos,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.campana)
            .setContentTitle("Aplicacion Solicitud")
            .setContentText("La solicitud ha sido aprobada")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.cita, "Cita", pendingIntentCita)
            .addAction(R.drawable.prestamo, "Préstamos", pendingIntentPrestamos)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {return}
            notify(1, builder.build())
        }
    }

    private fun lanzarNotificacionDenegada() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.campana)
            .setContentTitle("Aplicación Solicitud")
            .setContentText("La solicitud ha sido denegada")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {return}
            notify(2, builder.build())
        }
    }

    private fun crearCanalDeNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Canal de Préstamos"
            val descriptionText = "Notificaciones para préstamos aprobados"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}