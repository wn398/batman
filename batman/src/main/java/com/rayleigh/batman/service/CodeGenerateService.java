package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Module;
import com.rayleigh.batman.model.Project;

import java.io.File;
import java.util.Map;

public interface CodeGenerateService {
    //生成模块标准部分代码并打包发布
    void produceModuleStandardJar(String generateBasePath, String moduleId);
    //生成项目全部 standard部分代码
    void produceProjectStandard(String generatorBasePath, Project project);
    //生成项目全部extend部分代码文件
    void produceProjectExtend(String generatorBasePath, Project project);
    //生成项目全部文件
    void produceProjectAll(String generatorBasePath, Project project,String port,String root);
    //生成项目全部文件,标准部分采用jar方式
    void produceProjectAllWithStandardJar(String generatorBasePath, Project project,String port,String root);
    //生成项目下某一模块的除了standard,extend外的配置文件，包括配置文件
    void produceModuleOtherFiles(String generatorBasePath, Project project, Module module,String port,String root);
    //生成某一模块标准部分代码standard
    void produceModuleStandardAllFiles(String generatorBasePath, Project project, Module module);
    //生成某一模块扩展代码extend
    void produceModuleExtendAllFiles(String generatorBasePath, Project project, Module module);
    //根据是否需要生成最新的文件去生成文件
    void generateLastFileByTemplate(File file, String templateFileName, Map map, boolean isNewGenerate)throws Exception;
    //判断是否需要重新生成文件
    boolean checkIsNewGenerate(File file, Long projectUpdateTime, Long moduleUpdateTime, Long entityTime);



}