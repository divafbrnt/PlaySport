package com.example.playsport

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playsport.ui.theme.PlaySportTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RiwayatActivity : ComponentActivity() {

    // Dinaikkan di onResume agar daftar dimuat ulang setelah kembali dari BookingActivity
    private var refreshKey by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlaySportTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RiwayatScreen(
                        refreshKey = refreshKey,
                        modifier = Modifier.padding(innerPadding),
                        onKembali = { finish() }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshKey++
    }
}

@Composable
fun RiwayatScreen(refreshKey: Int, modifier: Modifier = Modifier, onKembali: () -> Unit) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).bookingDao() }
    val scope = rememberCoroutineScope()

    val daftar = remember { mutableStateListOf<BookingEntity>() }
    var idHapus by rememberSaveable { mutableStateOf<Int?>(null) }

    // READ dari Room setiap refreshKey berubah
    LaunchedEffect(refreshKey) {
        try {
            val data = withContext(Dispatchers.IO) { dao.getAllBooking() }
            daftar.clear()
            daftar.addAll(data)
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal memuat riwayat booking", Toast.LENGTH_SHORT).show()
        }
    }

    // Dialog konfirmasi hapus
    idHapus?.let { id ->
        val target = daftar.firstOrNull { it.id == id }
        AlertDialog(
            onDismissRequest = { idHapus = null },
            title = { Text("Batalkan Booking") },
            text = { Text("Yakin membatalkan booking ${target?.namaLapangan ?: ""} atas nama ${target?.namaPemesan ?: ""}?") },
            confirmButton = {
                TextButton(onClick = {
                    idHapus = null
                    scope.launch {
                        try {
                            // DELETE di database lokal, lalu refresh layar.
                            withContext(Dispatchers.IO) { dao.deleteById(id) }
                            val data = withContext(Dispatchers.IO) { dao.getAllBooking() }
                            daftar.clear()
                            daftar.addAll(data)
                            Toast.makeText(context, "Booking dibatalkan", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Gagal membatalkan booking", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) { Text("Ya, Batalkan", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { idHapus = null }) { Text("Tidak") } }
        )
    }

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
                text = "Booking Saya (${daftar.size})",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            OutlinedButton(onClick = onKembali) { Text("Kembali") }
        }

        if (daftar.isEmpty()) {
            Text("Belum ada booking", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(daftar, key = { it.id }) { booking ->
                    BookingCard(
                        booking = booking,
                        onEdit = {
                            // Explicit Intent + kirim id lapangan & id booking
                            val intent = Intent(context, BookingActivity::class.java)
                                .putExtra(BookingActivity.EXTRA_LAPANGAN_ID, booking.lapanganId)
                                .putExtra(BookingActivity.EXTRA_BOOKING_ID, booking.id)
                            context.startActivity(intent)
                        },
                        onDelete = { idHapus = booking.id }
                    )
                }
            }
        }
    }
}

@Composable
fun BookingCard(booking: BookingEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val onColor = MaterialTheme.colorScheme.onPrimaryContainer
            Text(booking.namaLapangan, fontWeight = FontWeight.Bold, color = onColor)
            Text("Atas nama ${booking.namaPemesan} (${booking.noHp})", fontSize = 13.sp, color = onColor)
            Text(
                "${booking.tanggal} • %02d:00 - %02d:00 (${booking.durasi} jam)"
                    .format(booking.jamMulai, booking.jamMulai + booking.durasi),
                fontSize = 13.sp, color = onColor
            )
            Text(
                "Total: ${formatRupiah(booking.totalHarga)}",
                fontWeight = FontWeight.SemiBold, color = onColor
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEdit) { Text("Edit") }
                TextButton(onClick = onDelete) {
                    Text("Batalkan", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
