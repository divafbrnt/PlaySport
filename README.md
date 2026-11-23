# PlaySport — Proyek Android Studio Lengkap

Aplikasi pemesanan lapangan olahraga menggunakan **Kotlin + Jetpack Compose + Room Database**.
Ini lanjutan dari source PlaySport awal, yang sebelumnya hanya berisi file `.kt` dan gambar tanpa konfigurasi proyek.

## Cara membuka (Windows / Android Studio)

1. Ekstrak file `PlaySport_Lengkap.zip` terlebih dahulu. Jangan buka ZIP langsung di Android Studio.
2. Pada **Windows**, klik dua kali **`SIAPKAN_GRADLE_WINDOWS.bat`** saat internet aktif; ini menyiapkan Gradle Wrapper untuk Sync pertama.
3. Buka **Android Studio** → **File > Open** → pilih folder **PlaySport_Lengkap** yang berisi `settings.gradle.kts` (jangan hanya pilih `app`).
4. Jika muncul **Trust Project**, pilih **Trust Project**. Tunggu **Gradle Sync** dan izinkan unduhan dependensi (perlu internet).
5. Bila SDK Android API 36 belum tersedia, buka **Tools > SDK Manager** lalu instal **Android API 36**.
6. Pastikan **Gradle JDK = 17** (Settings > Build, Execution, Deployment > Build Tools > Gradle).
7. Pilih emulator atau ponsel Android, lalu klik **Run ▶** untuk menjalankan aplikasi.

**PENTING mengenai Gradle Wrapper:** paket ini menyertakan file konfigurasi wrapper. Jika `gradle/wrapper/gradle-wrapper.jar` belum tersedia, jalankan `SIAPKAN_GRADLE_WINDOWS.bat` sekali (klik dua kali) saat komputer terhubung internet. Script akan mengunduh **wrapper resmi Gradle 8.13** dan memeriksa checksum SHA-256. Alternatifnya buat wrapper dari instalasi Gradle resmi dengan perintah `gradle wrapper --gradle-version 8.13`.

## Cara memakai

1. Pilih jenis olahraga pada beranda; kartu memuat foto dari folder `res/drawable`.
2. Klik **Booking** pada salah satu lapangan.
3. Isi nama, nomor HP (10–13 digit dimulai `08`), tanggal (`dd/MM/yyyy`), jam mulai (7–21), durasi (1–5 jam). Klik **Simpan Booking**.
4. Ketuk **Booking Saya** untuk membaca daftar pemesanan tersimpan.
5. Klik **Edit** untuk mengubah pemesanan, atau **Batalkan** untuk menghapus setelah konfirmasi.
6. Coba memesan lapangan yang sama, tanggal sama, jam bertabrakan: akan muncul pesan validasi.
7. Putar layar ketika form terisi: isian tetap terjaga dengan `rememberSaveable`.

## Pemetaan poin tugas dosen

| Poin | Bukti implementasi |
|---|---|
| a. Column, Row, Box dan Text/OutlinedTextField/Button/Image | `MainActivity.kt`, `BookingActivity.kt`, `RiwayatActivity.kt` |
| b. Gambar dari folder drawable | `app/src/main/res/drawable/lapangan_*.png` dipanggil di `MainActivity.kt` dan `BookingActivity.kt` |
| c. Minimal 2 layar, Explicit Intent dan kirim data | **3 Activity**; `MainActivity` → `BookingActivity` dengan `EXTRA_LAPANGAN_ID`; `RiwayatActivity` → `BookingActivity` dengan `EXTRA_BOOKING_ID` |
| d. Validasi, Defensive Programming, Activity Recreation / Ephemeral State Loss, Reactive UI | Validasi form, `toIntOrNull()`, `try/catch`, `rememberSaveable`, total harga berubah saat durasi berubah, `Toast` dan pesan error |
| e. CRUD | Simpan, tampilkan, ubah, dan batalkan booking pada `BookingDao.kt` |
| f. Database lokal | Room/SQLite (`BookingEntity.kt`, `BookingDao.kt`, `AppDatabase.kt`) |
| g. Aplikasi dapat dijalankan | Konfigurasi `settings.gradle.kts`, `build.gradle.kts`, manifest, theme telah disiapkan. **Build dan run aktual tetap perlu dilakukan di Android Studio milikmu**. |

## File penting

- `MainActivity.kt`: halaman daftar lapangan + filter jenis olahraga.
- `BookingActivity.kt`: halaman form booking sekaligus edit, validasi dan harga reaktif.
- `RiwayatActivity.kt`: daftar booking, edit/hapus, dialog konfirmasi.
- `Lapangan.kt`: data contoh lapangan dan harga.
- `BookingEntity.kt`, `BookingDao.kt`, `AppDatabase.kt`: database lokal Room.
- `ui/theme/Theme.kt`: tema Material 3 (`PlaySportTheme`), memperbaiki error import theme pada kode awal.
- `AndroidManifest.xml`: mendaftarkan ketiga Activity.

## Kalau kode masih merah

- Periksa apakah proses **Gradle Sync** selesai dan internet tersambung.
- Pastikan **buka folder proyek**, bukan cuma `.kt` atau ZIP-nya.
- Lihat **Build Output**, bukan hanya warna merah di editor.
- Pastikan **Android API 36** tersedia dan **Gradle JDK 17** dipilih.
- Jika belum terselesaikan, salin **error pertama dari Build Output** agar sumbernya bisa diketahui.

## Catatan keamanan pengumpulan

Jangan upload `local.properties`, `.gradle/`, `.idea/`, atau `build/` ke GitHub. Sudah tersedia `.gitignore`.
Paket tidak menyimpan identitas, password, atau API key.
