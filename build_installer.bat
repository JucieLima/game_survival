@echo off
setlocal

:: ----------------------------------------------------
:: CONFIGURAÇÕES DO PROJETO
:: ----------------------------------------------------
set JDK_BIN="C:\Program Files\Java\jdk-21\bin"
set JPACKAGE_EXE=%JDK_BIN%\jpackage.exe

set MAIN_JAR=SurvivalGame-1.0-SNAPSHOT.jar
set MAIN_CLASS=com.survival.survivalgame.Game
set APP_NAME="Survival Game"
set VENDOR_NAME="com.survival"
set RUNTIME_IMAGE=target/SurvivalGame-runtime
set OUTPUT_DEST=target/jpackage-output
set PACKAGE_TYPE=msi
:: Para gerar um instalador .exe, basta trocar acima para: set PACKAGE_TYPE=exe

echo.
echo =======================================================
echo 1. COMPILANDO PROJETO E GERANDO RUNTIME (Maven + JLink)
echo =======================================================
call mvn clean package -e
if errorlevel 1 goto error_maven

if not exist %RUNTIME_IMAGE% (
    echo.
    echo ERRO: O runtime %RUNTIME_IMAGE% nao foi encontrado.
    echo Verifique o pom.xml e se o maven-jlink-plugin esta configurado.
    goto end
)

echo.
echo =======================================================
echo 2. CRIANDO INSTALADOR COM JPACKAGE (%PACKAGE_TYPE%)
echo =======================================================
%JPACKAGE_EXE% ^
--input target ^
--name %APP_NAME% ^
--vendor %VENDOR_NAME% ^
--main-jar %MAIN_JAR% ^
--main-class %MAIN_CLASS% ^
--runtime-image %RUNTIME_IMAGE% ^
--type %PACKAGE_TYPE% ^
--dest %OUTPUT_DEST%

if errorlevel 1 goto error_jpackage

echo.
echo =======================================================
echo SUCESSO!
echo Instalador criado em: %OUTPUT_DEST%
echo =======================================================
goto end

:error_maven
echo.
echo ERRO: Falha ao rodar o Maven. Verifique o pom.xml e os logs.
goto end

:error_jpackage
echo.
echo ERRO: Falha ao rodar o JPackage. Verifique se o WiX Toolset esta instalado e no PATH.
goto end

:end
endlocal
pause
