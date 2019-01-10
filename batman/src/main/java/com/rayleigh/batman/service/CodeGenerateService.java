package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Module;
import com.rayleigh.batman.model.Project;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

public interface CodeGenerateService {
    /**
     * 生成模块标准部分代码并打包发布
     * @param generateBasePath
     * @param module
     * @param project
     */
    void produceModuleStandardJar(String generateBasePath, Module module,Project project);

    /**
     * 生成模块标准部分代码，只是标准代码，不包括一些配置文件
     * @param generateBasePath
     * @param moduleId
     */
    void produceModuleStandard(String generateBasePath, String moduleId);

    /**
     * 生成项目全部 standard部分代码
     * @param generatorBasePath
     * @param project
     */
    void produceProjectStandard(String generatorBasePath, Project project);
    //生成项目全部extend部分代码文件
    void produceProjectExtend(String generatorBasePath, Project project);
    //生成项目全部文件
    void produceProjectAll(String generatorBasePath, Project project,String port,String root);
    //生成项目全部文件,标准部分采用jar方式
    void produceProjectAllWithStandardJar(String generatorBasePath, Project project,String port,String root);
    //生成项目下某一模块的除了standard,extend外的配置文件，包括配置文件
    void produceModuleOtherFiles(String generatorBasePath, Project project, Module module,String port,String root);
    //生成某一模块标准部分代码standard，包括配置文件
    void produceModuleStandardAllFiles(String generatorBasePath, Project project, Module module);
    //生成某一模块扩展代码extend，包括配置文件
    void produceModuleExtendAllFiles(String generatorBasePath, Project project, Module module);
    //判断是否需要重新生成文件
    boolean checkIsNewGenerate(File file, Long projectUpdateTime, Long moduleUpdateTime);



}
