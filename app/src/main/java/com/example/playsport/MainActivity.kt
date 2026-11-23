package com.example.playsport

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playsport.ui.theme.PlaySportTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlaySportTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DaftarLapanganScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun DaftarLapanganScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val jenisList = listOf("Semua") + DataLapangan.daftar.map { it.jenis }.distinct()

    // rememberSaveable: filter tidak reset saat layar diputar
    var filter by rememberSaveable { mutableStateOf("Semua") }
    val tampil = if (filter == "Semua") DataLapangan.daftar
    else DataLapangan.daftar.filter { it.jenis == filter }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PlaySport",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            // Explicit Intent -> RiwayatActivity
            Button(onClick = {
                context.startActivity(Intent(context, RiwayatActivity::class.java))
            }) { Text("Booking Saya") }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(jenisList) { jenis ->
                FilterChip(
                    selected = filter == jenis,
                    onClick = { filter = jenis },
                    label = { Text(jenis) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tampil, key = { it.id }) { lapangan ->
                LapanganCard(
                    lapangan = lapangan,
                    onBooking = {
                        // Explicit Intent + kirim data (id lapangan) ke BookingActivity
                        val intent = Intent(context, BookingActivity::class.java)
                            .putExtra(BookingActivity.EXTRA_LAPANGAN_ID, lapangan.id)
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun LapanganCard(lapangan: Lapangan, onBooking: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column {
            // Box menumpuk gambar dari drawable dan label jenis olahraga.
            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                Image(
                    painter = painterResource(id = lapangan.gambar),
                    contentDescription = lapangan.nama,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = lapangan.jenis,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(Color(0xB8000000))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(lapangan.nama, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("${lapangan.jenis} • ${lapangan.lokasi}", fontSize = 13.sp)
                Text(lapangan.fasilitas, fontSize = 12.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${formatRupiah(lapangan.hargaPerJam)} / jam",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Button(onClick = onBooking) { Text("Booking") }
                }
            }
        }
    }
}
