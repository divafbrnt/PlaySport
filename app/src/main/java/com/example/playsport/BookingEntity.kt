package com.example.playsport

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "booking")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val lapanganId: Int,
    val namaLapangan: String,
    val namaPemesan: String,
    val noHp: String,
    val tanggal: String,   // format dd/MM/yyyy
    val jamMulai: Int,     // 7..21
    val durasi: Int,       // jam
    val totalHarga: Int
)
