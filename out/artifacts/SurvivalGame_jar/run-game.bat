@echo off
REM Executa SurvivalGame com runtime customizado (forçando software rendering)
set JAVA_RUNTIME=%~dp0runtime
"%JAVA_RUNTIME%\bin\java.exe" -Dprism.order=sw -Dprism.verbose=true -Dprism.forceGPU=false --module-path "%JAVA_RUNTIME%\bin" --add-modules com.survival.survivalgame -m com.survival.survivalgame/com.survival.survivalgame.Game
pause