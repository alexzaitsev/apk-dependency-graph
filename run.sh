#!/bin/bash
if [ $# -lt 2 ]; then
  echo "This script requires the next parameters:";
  echo "- absolute path to apk file";
  echo "- absolute path to the filters file";
  echo "Example:";
  echo "./run.sh full/path/to/the/apk/app-release.apk full/path/to/the/filters.json";
  exit 1;
fi
fileName="$1"
xbase=${fileName##*/}
xpref=${xbase%.*}

dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
outPath=${dir}"/output/"${xpref}
jsonPath=${dir}"/gui/analyzed.js"

eval "java -jar ${dir}'/build/libs/apk-dependency-graph.jar' -i ${outPath} -o ${jsonPath} -a $1 -f $2"
