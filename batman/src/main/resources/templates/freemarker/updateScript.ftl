#!/bin/sh

git fetch

git merge

projectId=${project.id}
ip=${ip}
port=${port}
context=${root}

#获取所有model名字
allModelName=$(curl -s http://$ip:$port/$context/projectCtl/getModelNames/$projectId)
echo "all modelNames: "$allModelName


OLD_IFS="$IFS"
IFS=":"
arr=($allModelName)

IFS="$OLD_IFS"
for s in ${r'${arr[@]}'}
do
echo "$s"
rm -rf "$s/src/main/java/${project.packageName ?replace(".","/")}/standard"
done

#curl http://$ip:$port/$context/codeGeneratorCtl/generateProjectStandard/$projectId -o ${project.name}_standard.zip

#unzip ${project.name}_standard.zip

curl http://$ip:$port/$context/codeGeneratorCtl/generateProjectStandardJar/$projectId

rm -rf ${project.name}_standard.zip

curl http://$ip:$port/$context/codeGeneratorCtl/generateProjectExtend/$projectId -o ${project.name}_extend.zip

unzip -n ${project.name}_extend.zip

rm -rf ${project.name}_extend.zip

mvn clean package