@echo off
setlocal

set argCount=0
for %%x in (%*) do (
   set /A argCount+=1
)
if not "%~2"=="" if "%~3"=="" goto main
    echo This script requires the next parameters:
    echo - absolute path to apk file
    echo - absolute path to the filters file
    echo Examples:
    echo %~nx0 full\path\to\the\apk\app-release.apk full\path\to\the\filters.json
    exit /b

:main

Set filename=%1
For %%A in ("%filename%") do (
    Set Folder=%%~dpA
    Set Name=%%~nxA
)

Set outPath=%~dp0\output\%Name:~0,-4%
Set jsonPath=%~dp0\gui\analyzed.js

java -jar %~dp0\build\libs\apk-dependency-graph.jar -i %outPath% -o %jsonPath% -a %1 -f %2
