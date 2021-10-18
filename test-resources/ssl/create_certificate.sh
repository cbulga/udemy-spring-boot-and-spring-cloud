echo "Self-signed certificate for enabling SSL"
keytool -genkeypair -alias testhttps -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore ssl-server.jks -validity 3650
