package com.example.examen1

data class Solicitud (
    val curp: String,
    val nombre: String,
    val apellidos: String,
    val domicilio: String,
    val cantidadIngreso: Double,
    val tipoPrestamo: String
)