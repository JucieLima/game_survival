# Caminhos do projeto
$projectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$srcDir     = Join-Path $projectDir "src"
$modsDir    = Join-Path $projectDir "mods"
$outDir     = $projectDir
$runtimeDir = Join-Path $outDir "runtime"

# Caminho para seu JDK e JavaFX
$jdkHome      = "C:\Program Files\Eclipse Adoptium\jdk-21.0.6.7-hotspot"
$javafxSdkDir = "C:\Program Files\Java\javafx-sdk-21.0.7"

# 1. Limpar pastas antigas
Write-Host "Limpando build anterior..."
Remove-Item -Recurse -Force $modsDir -ErrorAction Ignore
Remove-Item -Recurse -Force $runtimeDir -ErrorAction Ignore

# 2. Compilar o projeto
Write-Host "Compilando o projeto..."
& "$jdkHome\bin\javac.exe" --module-path "$javafxSdkDir\lib" `
    -d $modsDir `
    (Get-ChildItem -Recurse $srcDir -Filter *.java | ForEach-Object { $_.FullName })

if ($LASTEXITCODE -ne 0) {
    Write-Error "Erro na compilação."
    exit 1
}

# 3. Criar runtime customizado
Write-Host "Gerando runtime customizado..."
& "$jdkHome\bin\jlink.exe" `
    --module-path "$modsDir;$javafxSdkDir\lib" `
    --add-modules com.survival.survivalgame,javafx.controls,javafx.fxml,javafx.graphics `
    --strip-debug --compress=2 --no-header-files --no-man-pages `
    --output $runtimeDir

if ($LASTEXITCODE -ne 0) {
    Write-Error "Falha na criação do runtime customizado."
    exit 1
}

# 4. Copiar DLLs nativas do JavaFX
Write-Host "Copiando DLLs do JavaFX para runtime/bin..."
$binDir = Join-Path $runtimeDir "bin"
Copy-Item -Path "$javafxSdkDir\bin\*.dll" -Destination $binDir -Force -ErrorAction SilentlyContinue
Copy-Item -Path "$javafxSdkDir\lib\*.dll" -Destination $binDir -Force -ErrorAction SilentlyContinue

# 5. Criar run-game.bat SEM BOM
Write-Host "Criando run-game.bat..."
$batFile = Join-Path $outDir "run-game.bat"
$batContent = @'
@echo off
set JAVA_RUNTIME=%~dp0runtime
set PATH=%JAVA_RUNTIME%\bin;%PATH%
"%JAVA_RUNTIME%\bin\java.exe" --module-path "%JAVA_RUNTIME%\mods" --add-modules com.survival.survivalgame -m com.survival.survivalgame/com.survival.survivalgame.Game
pause
'@

# Salvar em ASCII puro (sem BOM)
[System.IO.File]::WriteAllText($batFile, $batContent, (New-Object System.Text.ASCIIEncoding))

Write-Host "Build concluido com sucesso!"
Write-Host "Para rodar o jogo, execute: $batFile"
