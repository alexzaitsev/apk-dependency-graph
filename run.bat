@echo off
if [%1]==[] (
	echo You must set full apk path as parameter
	exit /B
)
if [%2]==[] (
	echo You must set filter as the second parameter. We suggest to use your package name ^(com.example.test^). If you don't want to use filter, just pass 'nofilter'
	exit /B
)
Set filename=%1
For %%A in ("%filename%") do (
    Set Folder=%%~dpA
    Set Name=%%~nxA
)

Set outPath=%~dp0\%Name:~0,-4%
Set jsonPath=%~dp0\analyzed.js

java -jar %~dp0\apktool_2.2.0.jar d %1 -o %outPath% -f
java -jar %~dp0\apk_dependency_graph_0.0.5.jar -i %outPath% -o %jsonPath% -f %2