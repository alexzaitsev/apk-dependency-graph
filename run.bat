@echo off
setlocal

set argCount=0
for %%x in (%*) do (
   set /A argCount+=1
)
if not "%~3"=="" if "%~4"=="" goto main
    echo This script requires the next parameters:
    echo - absolute path to apk file
    echo - filter ^(can be a package name or 'nofilter' string^)
    echo - true or false ^(where true means that you want to see inner classes on your graph^)
    echo Examples:
    echo %~nx0 full\path\to\the\apk\app-release.apk com.example.test true
    echo %~nx0 full\path\to\the\apk\app-release.apk nofilter false
    exit /b

:main

Set filename=%1
For %%A in ("%filename%") do (
    Set Folder=%%~dpA
    Set Name=%%~nxA
)

Set outPath=%~dp0\output\%Name:~0,-4%
Set jsonPath=%~dp0\gui\analyzed.js

java -jar %~dp0\lib\apktool_2.3.4.jar d %1 -o %outPath% -f
java -jar %~dp0\build\jar\apk-dependency-graph.jar -i %outPath% -o %jsonPath% -f %2 -d %3
