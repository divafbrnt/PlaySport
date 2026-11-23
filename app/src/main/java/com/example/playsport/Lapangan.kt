package com.example.playsport

import androidx.annotation.DrawableRes
import java.text.NumberFormat
import java.util.Locale

// Data lapangan di-hardcode (seed). CRUD difokuskan ke tabel Booking.
data class Lapangan(
    val id: Int,
    val nama: String,
    val jenis: String,
    val hargaPerJam: Int,
    val lokasi: String,
    val fasilitas: String,
    @DrawableRes val gambar: Int
)

object DataLapangan {
    val daftar = listOf(
        Lapangan(1, "Badminton Court A", "Badminton", 45000, "GOR Senayan, Jakarta", "Lantai karpet, AC, kursi penonton", R.drawable.lapangan_badminton),
        Lapangan(2, "Badminton Court B", "Badminton", 40000, "GOR Senayan, Jakarta", "Lantai kayu, kipas angin", R.drawable.lapangan_badminton),
        Lapangan(3, "Futsal Arena 1", "Futsal", 150000, "Futsal Center Kemang", "Rumput sintetis, ruang ganti, kantin", R.drawable.lapangan_futsal),
        Lapangan(4, "Padel Court Premium", "Padel", 200000, "Padel Club PIK", "Dinding kaca, lampu LED, raket sewa", R.drawable.lapangan_padel),
        Lapangan(5, "Basket Indoor", "Basket", 120000, "GOR Bulungan", "Lantai parket, papan skor digital", R.drawable.lapangan_basket),
        Lapangan(6, "Tenis Court Clay", "Tenis", 100000, "Tennis Club Menteng", "Lapangan tanah liat, lampu malam", R.drawable.lapangan_tenis)
    )

    fun byId(id: Int): Lapangan? = daftar.firstOrNull { it.id == id }
}

fun formatRupiah(angka: Int): String =
    "Rp " + NumberFormat.getInstance(Locale("id", "ID")).format(angka)
