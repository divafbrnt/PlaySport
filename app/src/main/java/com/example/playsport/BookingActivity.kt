package com.example.playsport

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playsport.ui.theme.PlaySportTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookingActivity : ComponentActivity() {

    companion object {
        const val EXTRA_LAPANGAN_ID = "extra_lapangan_id"
        const val EXTRA_BOOKING_ID = "extra_booking_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Terima data dari Intent (Defensive Programming: beri default -1)
        val lapangan = DataLapangan.byId(intent.getIntExtra(EXTRA_LAPANGAN_ID, -1))
        val bookingId = intent.getIntExtra(EXTRA_BOOKING_ID, -1)

        if (lapangan == null) {
            Toast.makeText(this, "Data lapangan tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContent {
            PlaySportTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BookingScreen(
                        lapangan = lapangan,
                        bookingId = bookingId,
                        modifier = Modifier.padding(innerPadding),
                        onSelesai = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun BookingScreen(
    lapangan: Lapangan,
    bookingId: Int,
    modifier: Modifier = Modifier,
    onSelesai: () -> Unit
) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).bookingDao() }
    val scope = rememberCoroutineScope()
    val modeEdit = bookingId != -1

    // STATE FORM: rememberSaveable agar tidak hilang saat rotasi layar
    var nama by rememberSaveable { mutableStateOf("") }
    var noHp by rememberSaveable { mutableStateOf("") }
    var tanggal by rememberSaveable { mutableStateOf("") }
    var jamMulai by rememberSaveable { mutableStateOf("") }
    var durasi by rememberSaveable { mutableStateOf("") }

    var namaError by rememberSaveable { mutableStateOf<String?>(null) }
    var noHpError by rememberSaveable { mutableStateOf<String?>(null) }
    var tanggalError by rememberSaveable { mutableStateOf<String?>(null) }
    var jamError by rememberSaveable { mutableStateOf<String?>(null) }
    var durasiError by rememberSaveable { mutableStateOf<String?>(null) }

    // Mode edit: isi form dari database SEKALI saja (tidak menimpa input saat rotasi)
    var sudahDimuat by rememberSaveable { mutableStateOf(!modeEdit) }
    LaunchedEffect(Unit) {
        if (!sudahDimuat) {
            val data = withContext(Dispatchers.IO) { dao.getById(bookingId) }
            if (data == null) {
                Toast.makeText(context, "Data booking tidak ditemukan", Toast.LENGTH_SHORT).show()
                onSelesai()
            } else {
                nama = data.namaPemesan
                noHp = data.noHp
                tanggal = data.tanggal
                jamMulai = data.jamMulai.toString()
                durasi = data.durasi.toString()
                sudahDimuat = true
            }
        }
    }

    // REACTIVE UI: total harga berubah langsung saat durasi diketik
    val durasiInt = durasi.toIntOrNull()
    val total = if (durasiInt != null && durasiInt > 0) lapangan.hargaPerJam * durasiInt else null

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = if (modeEdit) "Edit Booking" else "Form Booking",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Image(
            painter = painterResource(id = lapangan.gambar),
            contentDescription = lapangan.nama,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        )
        Text(lapangan.nama, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        Text("${lapangan.jenis} • ${formatRupiah(lapangan.hargaPerJam)} / jam", fontSize = 13.sp)
        HorizontalDivider()

        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it; namaError = null },
            label = { Text("Nama Pemesan") },
            isError = namaError != null,
            supportingText = { namaError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = noHp,
            onValueChange = { noHp = it; noHpError = null },
            label = { Text("No. HP (08xxxxxxxxxx)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = noHpError != null,
            supportingText = { noHpError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = tanggal,
            onValueChange = { tanggal = it; tanggalError = null },
            label = { Text("Tanggal (dd/MM/yyyy)") },
            isError = tanggalError != null,
            supportingText = { tanggalError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = jamMulai,
            onValueChange = { jamMulai = it; jamError = null; durasiError = null },
            label = { Text("Jam Mulai (7 - 21)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = jamError != null,
            supportingText = { jamError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = durasi,
            onValueChange = { durasi = it; durasiError = null },
            label = { Text("Durasi (jam, 1 - 5)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = durasiError != null,
            supportingText = { durasiError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = if (total != null) "Total: ${formatRupiah(total)}" else "Total: -",
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    // ---------- VALIDASI CLIENT-SIDE ----------
                    var valid = true
                    val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                        isLenient = false
                    }

                    if (nama.isBlank()) {
                        namaError = "Nama wajib diisi!"; valid = false
                    }
                    if (!noHp.matches(Regex("^08\\d{8,11}$"))) {
                        noHpError = "No HP harus diawali 08 dan 10-13 digit!"; valid = false
                    }

                    // Defensive: parse tanggal aman, tidak boleh tanggal lampau
                    val tgl: Date? = try {
                        fmt.parse(tanggal.trim())
                    } catch (e: ParseException) {
                        null
                    }
                    val hariIni: Date? = fmt.parse(fmt.format(Date()))
                    if (tgl == null) {
                        tanggalError = "Format tanggal harus dd/MM/yyyy!"; valid = false
                    } else if (hariIni != null && tgl.before(hariIni)) {
                        tanggalError = "Tanggal tidak boleh sudah lewat!"; valid = false
                    }

                    // Defensive: toIntOrNull() agar tidak crash
                    val jam = jamMulai.toIntOrNull()
                    if (jam == null || jam !in 7..21) {
                        jamError = "Jam mulai harus angka 7 sampai 21!"; valid = false
                    }
                    val dur = durasi.toIntOrNull()
                    if (dur == null || dur !in 1..5) {
                        durasiError = "Durasi harus angka 1 sampai 5 jam!"; valid = false
                    } else if (jam != null && jam + dur > 22) {
                        durasiError = "Melebihi jam tutup (22:00)!"; valid = false
                    }

                    if (!valid || tgl == null || jam == null || dur == null) return@Button

                    // Salin ke variabel lokal sebelum pindah thread
                    val tanggalFinal = fmt.format(tgl)
                    val namaFinal = nama.trim()
                    val noHpFinal = noHp.trim()
                    val totalFinal = lapangan.hargaPerJam * dur

                    scope.launch {
                        // Cek slot bentrok (di background thread)
                        val bentrok = withContext(Dispatchers.IO) {
                            dao.hitungBentrok(
                                lapangan.id, tanggalFinal, jam, jam + dur,
                                if (modeEdit) bookingId else -1
                            )
                        }
                        if (bentrok > 0) {
                            jamError = "Slot sudah terisi pada jam tersebut!"
                            return@launch
                        }

                        withContext(Dispatchers.IO) {
                            val data = BookingEntity(
                                id = if (modeEdit) bookingId else 0,
                                lapanganId = lapangan.id,
                                namaLapangan = lapangan.nama,
                                namaPemesan = namaFinal,
                                noHp = noHpFinal,
                                tanggal = tanggalFinal,
                                jamMulai = jam,
                                durasi = dur,
                                totalHarga = totalFinal
                            )
                            if (modeEdit) dao.updateBooking(data) else dao.insertBooking(data)
                        }
                        Toast.makeText(
                            context,
                            if (modeEdit) "Booking diperbarui" else "Booking berhasil",
                            Toast.LENGTH_SHORT
                        ).show()
                        onSelesai()
                    }
                }
            ) {
                Text(if (modeEdit) "Perbarui Booking" else "Simpan Booking")
            }
            OutlinedButton(onClick = onSelesai) { Text("Batal") }
        }
    }
}
