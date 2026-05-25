# =============================================================
# Script de arranque del backend en modo desarrollo
#
# Copia este archivo como `run-dev.ps1` y reemplaza los valores
# con TUS credenciales reales. El archivo run-dev.ps1 NO debe
# commitearse (esta en .gitignore).
#
# Uso:
#   .\run-dev.ps1
#
# Si PowerShell bloquea la ejecucion del script, ejecuta una vez:
#   Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
# =============================================================

# ---- Gmail (verificacion de email) ----
# Genera el App Password en https://myaccount.google.com/apppasswords
$env:MAIL_USERNAME = "tucorreo@gmail.com"
$env:MAIL_PASSWORD = "abcdefghijklmnop"

# ---- Stripe (pasarela de pagos) ----
# Obtenla en https://dashboard.stripe.com/test/apikeys
$env:STRIPE_SECRET_KEY = "sk_test_REEMPLAZAME"

Write-Host "Variables seteadas. Arrancando back..."
Write-Host "  MAIL_USERNAME    = $env:MAIL_USERNAME"
Write-Host "  STRIPE_SECRET_KEY = $($env:STRIPE_SECRET_KEY.Substring(0, 12))..."
Write-Host ""

.\mvnw spring-boot:run
