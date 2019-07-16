#!/bin/bash
if [ $# -ne 1 ]; then
  echo "Please, specify release version";
  exit 1;
fi
gradle build
echo "var dependencies = {links:[{\"source\":\"Class A\",\"dest\":\"Class B\"},{\"source\":\"Class C\",\"dest\":\"Class B\"},]};" > gui/analyzed.js
zip -u -x .DS_Store -r "apk-dependency-graph-scripts-$1.zip" build/jar/apk-dependency-graph.jar build.xml gui/* lib/* filters/* run.bat run.sh  
