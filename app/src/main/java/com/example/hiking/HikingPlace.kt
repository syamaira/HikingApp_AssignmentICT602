package com.example.hiking

data class HikingPlace(
    val name: String = "",
    val location: String = "",
    val difficulty: String = "",
    val lat: Double = 0.0, // Tambah latitude
    val lng: Double = 0.0  // Tambah longitude
)