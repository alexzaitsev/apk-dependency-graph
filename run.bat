@echo off
if [%1]==[] (
	echo You must set full apk path as parameter
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

if [%2]==[] (
	java -jar %~dp0\apk_dependency_graph_0.0.1.jar -i %outPath% -o %jsonPath%
) else ( 
	java -jar %~dp0\apk_dependency_graph_0.0.1.jar -i %outPath% -o %jsonPath% -f %2
)