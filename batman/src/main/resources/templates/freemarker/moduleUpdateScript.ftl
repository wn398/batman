#!/bin/sh

git fetch

git merge

moduleId=${module.id}
ip=${ip}
port=${port}
context=${root}


curl http://$ip:$port/$context/codeGeneratorCtl/generateModuleStandardJar/$moduleId

curl http://$ip:$port/$context/codeGeneratorCtl/generateModuleExtend/$moduleId -o ${module.name}_extend.zip

unzip -n ${module.name}_extend.zip

rm -rf ${module.name}_extend.zip

mvn clean package