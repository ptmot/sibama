package com.sibama2024ai

data class ListDrainase(
    val id_drainase: String,
    val id_jalan: String,
    val lat_awal: String,
    val long_awal: String,
    val lat_akhir: String,
    val long_akhir: String,
    val drainase: String,
    val panjang_saluran: String,
    val slope: String,
    val catchment_area: String,
    val tinggi_saluran: String,
    val lebar_saluran: String,
    val luas_penampung: String,
    val keliling_penampung: String
)