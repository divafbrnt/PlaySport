@echo off
setlocal
cd /d "%~dp0"
echo Menyiapkan Gradle Wrapper 8.13 resmi...
powershell.exe -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $url='https://raw.githubusercontent.com/gradle/gradle/v8.13.0/gradle/wrapper/gradle-wrapper.jar'; $target='gradle/wrapper/gradle-wrapper.jar'; $sha='81a82aaea5abcc8ff68b3dfcb58b3c3c429378efd98e7433460610fecd7ae45f'; Invoke-WebRequest -Uri $url -OutFile $target; if ((Get-FileHash $target -Algorithm SHA256).Hash.ToLower() -ne $sha) { Remove-Item $target -Force; throw 'Checksum tidak cocok. File dihapus.' }; Write-Host 'Gradle Wrapper berhasil terunduh dan checksum valid.'"
if errorlevel 1 (
  echo Gagal download. Pastikan ada koneksi internet. Tutup jendela ini dan coba lagi.
  pause
  exit /b 1
)
echo Sekarang buka folder proyek di Android Studio, lalu Sync Project.
pause
