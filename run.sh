#!/bin/bash
if [ $# -eq 0 ]; then echo "You must set full apk path as parameter"; exit 1; fi
if [ $# -eq 1 ]; then echo "You must set filter as the second parameter. We suggest to use your package name (com.example.test). If you don't want to use filter, just pass 'nofilter'"; exit 1; fi
fileName="$1"
xbase=${fileName##*/}
xpref=${xbase%.*}

dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
outPath=${dir}"/"${xpref}
jsonPath=${dir}"/analyzed.js" 

eval "java -jar ${dir}'/apktool_2.2.0.jar' d ${fileName} -o ${outPath} -f"
eval "java -jar ${dir}'/apk_dependency_graph_0.0.2.jar' -i ${outPath} -o ${jsonPath} -f $2"