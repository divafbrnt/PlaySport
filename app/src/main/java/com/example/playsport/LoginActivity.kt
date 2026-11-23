package com.example.playsport

import android.app.Activity
import android.content.Intent
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playsport.ui.theme.PlaySportTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlaySportTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// Password tidak disimpan polos, tapi di-hash dengan SHA-256
fun hashPassword(password: String): String =
    MessageDigest.getInstance("SHA-256")
        .digest(password.toByteArray())
        .joinToString("") { "%02x".format(it) }

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).userDao() }
    val scope = rememberCoroutineScope()

    // rememberSaveable: input tidak hilang saat rotasi layar
    var modeDaftar by rememberSaveable { mutableStateOf(false) }
    var namaLengkap by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var konfirmasi by rememberSaveable { mutableStateOf("") }

    var namaError by rememberSaveable { mutableStateOf<String?>(null) }
    var usernameError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var konfirmasiError by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.lapangan_badminton),
            contentDescription = "Banner PlaySport",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )
        Text(
            text = "PlaySport",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(if (modeDaftar) "Buat akun baru" else "Masuk untuk booking lapangan")
        HorizontalDivider()

        if (modeDaftar) {
            OutlinedTextField(
                value = namaLengkap,
                onValueChange = { namaLengkap = it; namaError = null },
                label = { Text("Nama Lengkap") },
                isError = namaError != null,
                supportingText = { namaError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
        }
        OutlinedTextField(
            value = username,
            onValueChange = { username = it; usernameError = null },
            label = { Text("Username") },
            singleLine = true,
            isError = usernameError != null,
            supportingText = { usernameError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; passwordError = null },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = passwordError != null,
            supportingText = { passwordError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        if (modeDaftar) {
            OutlinedTextField(
                value = konfirmasi,
                onValueChange = { konfirmasi = it; konfirmasiError = null },
                label = { Text("Konfirmasi Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = konfirmasiError != null,
                supportingText = { konfirmasiError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // ---------- VALIDASI CLIENT-SIDE ----------
                var valid = true
                if (modeDaftar && namaLengkap.isBlank()) {
                    namaError = "Nama lengkap wajib diisi!"; valid = false
                }
                if (username.trim().length < 4) {
                    usernameError = "Username minimal 4 karakter!"; valid = false
                }
                if (password.length < 6) {
                    passwordError = "Password minimal 6 karakter!"; valid = false
                }
                if (modeDaftar && konfirmasi != password) {
                    konfirmasiError = "Konfirmasi password tidak sama!"; valid = false
                }
                if (!valid) return@Button

                val usernameFinal = username.trim().lowercase()
                val passwordHash = hashPassword(password)
                val namaFinal = namaLengkap.trim()

                scope.launch {
                    if (modeDaftar) {
                        // CREATE akun: cek username belum dipakai
                        val sudahAda = withContext(Dispatchers.IO) { dao.getByUsername(usernameFinal) }
                        if (sudahAda != null) {
                            usernameError = "Username sudah dipakai!"
                            return@launch
                        }
                        withContext(Dispatchers.IO) {
                            dao.insertUser(
                                UserEntity(
                                    username = usernameFinal,
                                    namaLengkap = namaFinal,
                                    passwordHash = passwordHash
                                )
                            )
                        }
                        Toast.makeText(context, "Registrasi berhasil, silakan login", Toast.LENGTH_SHORT).show()
                        modeDaftar = false
                        password = ""
                        konfirmasi = ""
                    } else {
                        // LOGIN: cocokkan username + hash password
                        val user = withContext(Dispatchers.IO) { dao.getByUsername(usernameFinal) }
                        if (user == null || user.passwordHash != passwordHash) {
                            passwordError = "Username atau password salah!"
                            return@launch
                        }
                        // Explicit Intent + kirim data nama user
                        val intent = Intent(context, MainActivity::class.java)
                            .putExtra(MainActivity.EXTRA_NAMA, user.namaLengkap)
                        context.startActivity(intent)
                        (context as? Activity)?.finish()
                    }
                }
            }
        ) {
            Text(if (modeDaftar) "Daftar" else "Login")
        }

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                modeDaftar = !modeDaftar
                namaError = null; usernameError = null
                passwordError = null; konfirmasiError = null
            }
        ) {
            Text(if (modeDaftar) "Sudah punya akun? Login" else "Belum punya akun? Daftar")
        }
    }
}
