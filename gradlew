#!/bin/sh
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
WRAPPER="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
if [ ! -f "$WRAPPER" ]; then
  echo "Gradle Wrapper belum ada. Download resmi di:" >&2
  echo "https://raw.githubusercontent.com/gradle/gradle/v8.13.0/gradle/wrapper/gradle-wrapper.jar" >&2
  echo "Simpan sebagai $WRAPPER" >&2
  echo "Cek SHA-256: 81a82aaea5abcc8ff68b3dfcb58b3c3c429378efd98e7433460610fecd7ae45f" >&2
  exit 1
fi
exec java -Xmx64m -Xms64m -cp "$WRAPPER" org.gradle.wrapper.GradleWrapperMain "$@"
