package com.rayleigh.batman.util;

import com.rayleigh.batman.model.Module;
import com.rayleigh.batman.model.Project;
import com.rayleigh.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 构建项目 文件目录
 */
public class BuildProjectDirUtil {
    private static Logger logger = LoggerFactory.getLogger(BuildProjectDirUtil.class);

    //获取项目根路径
    public static String getProjectBasePath(String generatorBasePath,String projectName){
        return new StringBuilder(generatorBasePath).append("/").append(projectName).toString();
    }
    //获取模块根路径
    public static String getModuleBasePath(String generatorBasePath,String projectName,String moduleName){
        return new StringBuilder(generatorBasePath).append("/").append(projectName).append("/").append(moduleName).toString();
    }
    //生成代码资源路径
    public static String getStandardResourcePath(String generatorBasePath,String projectName,String moduleName){
        String projectBasePath = getProjectBasePath(generatorBasePath,projectName);
        if(StringUtil.isEmpty(moduleName)){
            return new StringBuilder(projectBasePath).append("/src/main/resources").toString();
        }else{
            return new StringBuilder(projectBasePath).append("/").append(moduleName).append("/src/main/resources").toString();
        }
    }
    //获取基础包根路径
    public static String getRelativePackagePath(String generatorBasePath,String projectName,String moduleName,String basePackageName){
        String projectBasePath = getProjectBasePath(generatorBasePath,projectName);
        String relativePackagePath = basePackageName.replaceAll("[.]","/");
        if(StringUtil.isEmpty(moduleName)){
            return new StringBuilder(projectBasePath).append("/src/main/java").append("/").append(relativePackagePath).toString();
        }else{
            return new StringBuilder(projectBasePath).append("/").append(moduleName).append("/src/main/java").append("/").append(relativePackagePath).toString();
        }
    }
    //获取基础包下filter根路径
    public static String getRelativeFilterPath(String generatorBasePath,String projectName,String moduleName,String basePackageName){
        String projectBasePath = getProjectBasePath(generatorBasePath,projectName);
        String relativePackagePath = basePackageName.replaceAll("[.]","/");
        if(StringUtil.isEmpty(moduleName)){
            return new StringBuilder(projectBasePath).append("/src/main/java").append("/").append(relativePackagePath).append("/filter").toString();
        }else{
            return new StringBuilder(projectBasePath).append("/").append(moduleName).append("/src/main/java").append("/").append(relativePackagePath).append("/filter").toString();
        }
    }
    //获取standard java根路径
    public static String getStandardJavaBasePackagePath(String generatorBasePath,String projectName,String moduleName,String basePackageName){
        String projectBasePath = getProjectBasePath(generatorBasePath,projectName);
        String relativePackagePath = basePackageName.replaceAll("[.]","/");
        if(StringUtil.isEmpty(moduleName)){
            return new StringBuilder(projectBasePath).append("/src/main/java").append("/").append(relativePackagePath).append("/standard").toString();
        }else{
            return new StringBuilder(projectBasePath).append("/").append(moduleName).append("/src/main/java").append("/").append(relativePackagePath).append("/standard").toString();
        }
    }

    //获取standard controller路径
    public static String getStandardControllerPath(String generatorBasePath,String projectName,String moduleName,String basePackageName){
        String standardJavaBasePackagePath = getStandardJavaBasePackagePath(generatorBasePath,projectName,moduleName,basePackageName);
        return new StringBuilder(standardJavaBasePackagePath).append("/controller").toString();
    }
    //获取standard methodIntercept路径
    public static String getStandardMethodInterceptPath(String generatorBasePath,String projectName,String moduleName,String basePackageName){
        String standardJavaBasePackagePath = getStandardJavaBasePackagePath(generatorBasePath,projectName,moduleName,basePackageName);
        return new StringBuilder(standardJavaBasePackagePath).append("/methodIntercept").toString();
    }
    //获取standard repository路径
    public static String getStandardRepositoryPath(String generatorBasePath,String projectName,String moduleName,String basePackageName){
        String standardJavaBasePackagePath = getStandardJavaBasePackagePath(generatorBasePath,projectName,moduleName,basePackageName);
        return new StringBuilder(standardJavaBasePackagePath).append("/repository").toString();
    }
    //获取standard model路径
    public static String getStandardModelPath(String generatorBasePath,String projectName,String moduleName,String basePackageName){
        String standardJavaBasePackagePath = getStandardJavaBasePackagePath(generatorBasePath,projectName,moduleName,basePackageName);
        return new StringBuilder(standardJavaBasePackagePath).append("/model").toString();
    }

    //获取standard service路径
    public static String getStandardServicePath(String generatorBasePath,String projectName,String moduleName,String basePackageName){
        String standardJavaBasePackagePath = getStandardJavaBasePackagePath(generatorBasePath,projectName,moduleName,basePackageName);
        return new StringBuilder(standardJavaBasePackagePath).append("/service").toString();
    }

    //获取standard serviceImpl路径
    public static String getStandardServiceImplPath(String generatorBasePath,String projectName,String moduleName,String basePackageName){
        String standardJavaBasePackagePath = getStandardJavaBasePackagePath(generatorBasePath,projectName,moduleName,basePackageName);
        return new StringBuilder(standardJavaBasePackagePath).append("/service/impl").toString();
    }
    //获取standard util路径
    public static String getStandardUtilPath(String generatorBasePath,String projectName,String moduleName,String basePackageName){
        String standardJavaBasePackagePath = getStandardJavaBasePackagePath(generatorBasePath,projectName,moduleName,basePackageName);
        return new StringBuilder(standardJavaBasePackagePath).append("/util").toString();
    }

    //获取方法的入参和结果类模型model
    public static String getStandardMethodModel(String generatorBasePath,String projectName,String moduleName,String basePackageName){
        String standardJavaBasePackagePath = getStandardJavaBasePackagePath(generatorBasePath,projectName,moduleName,basePackageName);
        return new StringBuilder(standardJavaBasePackagePath).append("/methodModel").toString();
    }

    //获取moduleRelation包路径
    public static String getModuleRelationDirPath(String generatorBasePath,String projectName,String moduleName,String basePackageName){
        String standardJavaBasePackagePath = getStandardJavaBasePackagePath(generatorBasePath,projectName,moduleName,basePackageName);
        return new StringBuilder(standardJavaBasePackagePath).append("/modelRelation").toString();
    }

    //获取extend基本路径
    public static String getExtendJavaBasePackagePath(String generatorBasePath,String projectName,String moduleName,String basePackageName){
        String projectBasePath = getProjectBasePath(generatorBasePath,projectName);
        String relativePackagePath = basePackageName.replaceAll("[.]","/");
        if(StringUtil.isEmpty(moduleName)){
            return new StringBuilder(projectBasePath).append("/src/main/java").append("/").append(relativePackagePath).append("/extend").toString();
        }else{
            return new StringBuilder(projectBasePath).append("/").append(moduleName).append("/src/main/java").append("/").append(relativePackagePath).append("/extend").toString();
        }
    }
    //获取extend controller路径
    public static String getExtendControllerPath(String generatorBasePath,String projectName,String moduleName,String basePackageName){
        String extendJavaBasePackagePath = getExtendJavaBasePackagePath(generatorBasePath,projectName,moduleName,basePackageName);
        return new StringBuilder(extendJavaBasePackagePath).append("/controller").toString();
    }
    //获取extend methodInterceptImpl路径
    public static String getExtendMethodInterceptImplPath(String generatorBasePath,String projectName,String moduleName,String basePackageName){
        String extendJavaBasePackagePath = getExtendJavaBasePackagePath(generatorBasePath,projectName,moduleName,basePackageName);
        return new StringBuilder(extendJavaBasePackagePath).append("/methodInterceptImpl").toString();
    }
    //获取 extend repository路径
    public static String getExtendRepositoryPath(String generatorBasePath,String projectName,String moduleName,String basePackageName){
        String extendJavaBasePackagePath = getExtendJavaBasePackagePath(generatorBasePath,projectName,moduleName,basePackageName);
        return new StringBuilder(extendJavaBasePackagePath).append("/repository").toString();
    }

    //获取extend service路径
    public static String getExtendServicePath(String generatorBasePath,String projectName,String moduleName,String basePackageName){
        String extendJavaBasePackagePath = getExtendJavaBasePackagePath(generatorBasePath,projectName,moduleName,basePackageName);
        return new StringBuilder(extendJavaBasePackagePath).append("/service").toString();
    }
    //获取extend serviceImpl路径
    public static String getExtendServiceImplPath(String generatorBasePath,String projectName,String moduleName,String basePackageName){
        String extendJavaBasePackagePath = getExtendJavaBasePackagePath(generatorBasePath,projectName,moduleName,basePackageName);
        return new StringBuilder(extendJavaBasePackagePath).append("/service/impl").toString();
    }

    //获取extend util路径
    public static String getExtendUtilPath(String generatorBasePath,String projectName,String moduleName,String basePackageName){
        String extendJavaBasePackagePath = getExtendJavaBasePackagePath(generatorBasePath,projectName,moduleName,basePackageName);
        return new StringBuilder(extendJavaBasePackagePath).append("/util").toString();
    }

    //构建项目基本路径文件夹 @generatorBasePath 生成器目录(上下文根路径 +生成器文件夹名)
    public static  void createDirForProject(String generatorBasePath,Project project){

        StringBuilder sb = new StringBuilder(generatorBasePath);
        //构建项目的根目录
        File projectDir = new File(sb.append("/").append(project.getName()).toString());
        //相对基本包路径
        String relativePackagePath = project.getPackageName().replaceAll("[.]","/");
        //相对standard包路径
        String standardRelativePackagePath = new StringBuilder(relativePackagePath).append("/standard").toString();
        //相对extend包路径
        String extendRelativePackagePath = new StringBuilder(relativePackagePath).append("/extend").toString();
        //构建模块目录结构
        for(Module module:project.getModules()){
            File moduleDir = new File(projectDir,module.getName());

            //生成代码生成的目录
            File moduleJava = new File(moduleDir,"src/main/java");
            File moduleResources = new File(moduleDir,"src/main/resources");
            if(!moduleResources.exists()) {
                boolean moduleResouceDir = moduleResources.mkdirs();
                if (!moduleResouceDir) {
                    logger.error(new StringBuilder("创建目录:").append(moduleResources.getAbsolutePath()).append(" 失败").toString());
                }
            }
            //java基本根路径
            File moduleJavaBasePackage = new File(moduleJava,standardRelativePackagePath);

            File moduleController = new File(moduleJavaBasePackage,"controller");
            File moduleModel = new File(moduleJavaBasePackage,"model");
            File moduleRepository = new File(moduleJavaBasePackage,"repository");
            File moduleService = new File(moduleJavaBasePackage,"service");
            File moduleServiceImpl = new File(moduleJavaBasePackage,"service/impl");
            File moduleUtil = new File(moduleJavaBasePackage,"util");
            File moduleMethodModel = new File(moduleJavaBasePackage,"methodModel");
            File moduleRelation = new File(moduleJavaBasePackage,"modelRelation");
            File moduleMethodIntercept = new File(moduleJavaBasePackage,"methodIntercept");
            if(!moduleController.exists()) {
                boolean moduleControllerDir = moduleController.mkdirs();
                if (!moduleControllerDir) {
                    logger.error(new StringBuilder("创建目录:").append(moduleController.getAbsolutePath()).append(" 失败").toString());
                }
            }

            if(!moduleModel.exists()) {
                boolean moduleModelDir = moduleModel.mkdirs();
                if (!moduleModelDir) {
                    logger.error(new StringBuilder("创建目录:").append(moduleModel.getAbsolutePath()).append(" 失败").toString());
                }
            }

            if(!moduleRepository.exists()) {
                boolean moduleRepositoryDir = moduleRepository.mkdirs();
                if (!moduleRepositoryDir) {
                    logger.error(new StringBuilder("创建目录:").append(moduleRepository.getAbsolutePath()).append(" 失败").toString());
                }
            }

            if(!moduleService.exists()) {
                boolean moduleServiceDir = moduleService.mkdirs();
                if (!moduleServiceDir) {
                    logger.error(new StringBuilder("创建目录:").append(moduleService.getAbsolutePath()).append(" 失败").toString());
                }
            }

            if(!moduleServiceImpl.exists()) {
                boolean moduleServiceImplDir = moduleServiceImpl.mkdirs();
                if (!moduleServiceImplDir) {
                    logger.error(new StringBuilder("创建目录:").append(moduleServiceImpl.getAbsolutePath()).append(" 失败").toString());
                }
            }

            if(!moduleUtil.exists()) {
                boolean moduleUtilDir = moduleUtil.mkdirs();
                if (!moduleUtilDir) {
                    logger.error(new StringBuilder("创建目录:").append(moduleUtil.getAbsolutePath()).append(" 失败").toString());
                }
            }

            if(!moduleMethodModel.exists()) {
                boolean moduleMethodModelDir = moduleMethodModel.mkdirs();
                if (!moduleMethodModelDir) {
                    logger.error(new StringBuilder("创建目录:").append(moduleMethodModel.getAbsolutePath()).append(" 失败").toString());
                }
            }


            if(!moduleRelation.exists()) {
                boolean moduleRelationDir = moduleRelation.mkdirs();
                if (!moduleRelationDir) {
                    logger.error(new StringBuilder("创建目录:").append(moduleRelation.getAbsolutePath()).append(" 失败").toString());
                }
            }

            if(!moduleMethodIntercept.exists()) {
                boolean moduleMethodInterceptDir = moduleMethodIntercept.mkdirs();
                if (!moduleMethodInterceptDir) {
                    logger.error(new StringBuilder("创建目录:").append(moduleMethodIntercept.getAbsolutePath()).append(" 失败").toString());
                }
            }

            File testModuleJava = new File(moduleDir,"src/test/java");
            //test基本根路径
            File testPackagePath = new File(testModuleJava,standardRelativePackagePath);

            if(!testPackagePath.exists()) {
                boolean testPackagePathDir = testPackagePath.mkdirs();
                if (!testPackagePathDir) {
                    logger.error(new StringBuilder("创建目录:").append(testPackagePath.getAbsolutePath()).append(" 失败").toString());
                }
            }

            /**扩展代码的目录**/
            File extendModuleJava = new File(moduleDir,"src/main/java");
            File extendJavaBasePackage = new File(extendModuleJava,extendRelativePackagePath);

            File extendModuleController = new File(extendJavaBasePackage,"controller");
            File extendModuleService = new File(extendJavaBasePackage,"service");
            File extendModuleServiceImpl = new File(extendJavaBasePackage,"service/impl");
            File extendModuleRepository = new File(extendJavaBasePackage,"repository");
            File extendModuleUtil = new File(extendJavaBasePackage,"util");
            File extendMethodIntercept = new File(extendJavaBasePackage,"methodInterceptImpl");

            if(!extendModuleController.exists()) {
                boolean extendModuleControllerDir = extendModuleController.mkdirs();
                if (!extendModuleControllerDir) {
                    logger.error(new StringBuilder("创建目录:").append(extendModuleController.getAbsolutePath()).append(" 失败").toString());
                }
            }

            if(!extendModuleRepository.exists()) {
                boolean extendModuleRepositoryDir = extendModuleRepository.mkdirs();
                if (!extendModuleRepositoryDir) {
                    logger.error(new StringBuilder("创建目录:").append(extendModuleRepository.getAbsolutePath()).append(" 失败").toString());
                }
            }

            if(!extendModuleService.exists()) {
                boolean extendModuleServiceDir = extendModuleService.mkdirs();
                if (!extendModuleServiceDir) {
                    logger.error(new StringBuilder("创建目录:").append(extendModuleService.getAbsolutePath()).append(" 失败").toString());
                }
            }

            if(!extendModuleServiceImpl.exists()) {
                boolean extendModuleServiceImplDir = extendModuleServiceImpl.mkdirs();
                if (!extendModuleServiceImplDir) {
                    logger.error(new StringBuilder("创建目录:").append(extendModuleServiceImpl.getAbsolutePath()).append(" 失败").toString());
                }
            }

            if(!extendModuleUtil.exists()) {
                boolean extendModuleUtilDir = extendModuleUtil.mkdirs();
                if (!extendModuleUtilDir) {
                    logger.error(new StringBuilder("创建目录:").append(extendModuleUtil.getAbsolutePath()).append(" 失败").toString());
                }
            }

            if(!extendMethodIntercept.exists()) {
                boolean extendMethodInterceptDir = extendMethodIntercept.mkdirs();
                if (!extendMethodInterceptDir) {
                    logger.error(new StringBuilder("创建目录:").append(extendMethodIntercept.getAbsolutePath()).append(" 失败").toString());
                }
            }

        }
    }

    public static void createDirForProjectModuleStandard(String generatorBasePath, Project project, Module module){
        StringBuilder sb = new StringBuilder(generatorBasePath);
        //构建项目的根目录
        File projectDir = new File(sb.append("/").append(project.getName()).toString());
        //相对基本包路径
        String relativePackagePath = project.getPackageName().replaceAll("[.]","/");
        //相对standard包路径
        String standardRelativePackagePath = new StringBuilder(relativePackagePath).append("/standard").toString();

        File moduleDir = new File(projectDir,module.getName());

        //生成代码生成的目录
        File moduleJava = new File(moduleDir,"src/main/java");

        File moduleResource = new File(getStandardModuleResourcePath(generatorBasePath,project.getName(),module.getName()));

        //java基本根路径
        File moduleJavaBasePackage = new File(moduleJava,standardRelativePackagePath);

        File moduleController = new File(moduleJavaBasePackage,"controller");
        File moduleModel = new File(moduleJavaBasePackage,"model");
        File moduleRepository = new File(moduleJavaBasePackage,"repository");
        File moduleService = new File(moduleJavaBasePackage,"service");
        File moduleServiceImpl = new File(moduleJavaBasePackage,"service/impl");
        File moduleUtil = new File(moduleJavaBasePackage,"util");
        File moduleMethodModel = new File(moduleJavaBasePackage,"methodModel");
        File moduleRelation = new File(moduleJavaBasePackage,"modelRelation");

        if(!moduleResource.exists()){
            boolean moduleResourceDir = moduleResource.mkdirs();
            if(!moduleResourceDir){
                logger.error(new StringBuilder("创建目录:").append(moduleResource.getAbsolutePath()).append(" 失败").toString());
            }
        }

        if(!moduleController.exists()) {
            boolean moduleControllerDir = moduleController.mkdirs();
            if (!moduleControllerDir) {
                logger.error(new StringBuilder("创建目录:").append(moduleController.getAbsolutePath()).append(" 失败").toString());
            }
        }

        if(!moduleModel.exists()) {
            boolean moduleModelDir = moduleModel.mkdirs();
            if (!moduleModelDir) {
                logger.error(new StringBuilder("创建目录:").append(moduleModel.getAbsolutePath()).append(" 失败").toString());
            }
        }

        if(!moduleRepository.exists()) {
            boolean moduleRepositoryDir = moduleRepository.mkdirs();
            if (!moduleRepositoryDir) {
                logger.error(new StringBuilder("创建目录:").append(moduleRepository.getAbsolutePath()).append(" 失败").toString());
            }
        }

        if(!moduleService.exists()) {
            boolean moduleServiceDir = moduleService.mkdirs();
            if (!moduleServiceDir) {
                logger.error(new StringBuilder("创建目录:").append(moduleService.getAbsolutePath()).append(" 失败").toString());
            }
        }

        if(!moduleServiceImpl.exists()) {
            boolean moduleServiceImplDir = moduleServiceImpl.mkdirs();
            if (!moduleServiceImplDir) {
                logger.error(new StringBuilder("创建目录:").append(moduleServiceImpl.getAbsolutePath()).append(" 失败").toString());
            }
        }

        if(!moduleUtil.exists()) {
            boolean moduleUtilDir = moduleUtil.mkdirs();
            if (!moduleUtilDir) {
                logger.error(new StringBuilder("创建目录:").append(moduleUtil.getAbsolutePath()).append(" 失败").toString());
            }
        }

        if(!moduleMethodModel.exists()) {
            boolean moduleMethodModelDir = moduleMethodModel.mkdirs();
            if (!moduleMethodModelDir) {
                logger.error(new StringBuilder("创建目录:").append(moduleMethodModel.getAbsolutePath()).append(" 失败").toString());
            }
        }
        if(!moduleRelation.exists()) {
            boolean moduleRelationDir = moduleRelation.mkdirs();
            if (!moduleRelationDir) {
                logger.error(new StringBuilder("创建目录:").append(moduleRelation.getAbsolutePath()).append(" 失败").toString());
            }
        }

        //            File testModuleJava = new File(moduleDir,"src/test/java");
//            //test基本根路径
//            File testPackagePath = new File(testModuleJava,standardRelativePackagePath);
//            boolean testPackagePathDir = testPackagePath.mkdirs();
//            if(!testPackagePathDir){
//                logger.error(new StringBuilder("创建目录:").append(testPackagePath.getAbsolutePath()).append(" 失败").toString());
//            }

    }
    //构建项目模块下扩展代码路径
    public static void createDirForProjectModuleExtend(String generatorBasePath,Project project,Module module){
        StringBuilder sb = new StringBuilder(generatorBasePath);
        //构建项目的根目录
        File projectDir = new File(sb.append("/").append(project.getName()).toString());
        //相对基本包路径
        String relativePackagePath = project.getPackageName().replaceAll("[.]","/");
        //相对extend包路径
        String extendRelativePackagePath = new StringBuilder(relativePackagePath).append("/extend").toString();


        File moduleDir = new File(projectDir,module.getName());
        //生成代码生成的目录
        File moduleJava = new File(moduleDir,"src/main/java");
        /**扩展代码的目录**/
        File extendModuleJava = new File(moduleDir,"src/main/java");
        File resourcePath = new File(moduleDir,"src/main/resources");
        File extendJavaBasePackage = new File(extendModuleJava,extendRelativePackagePath);

        File extendModuleController = new File(extendJavaBasePackage,"controller");
        File extendModuleService = new File(extendJavaBasePackage,"service");
        File extendModuleServiceImpl = new File(extendJavaBasePackage,"service/impl");
        File extendModuleRepository = new File(extendJavaBasePackage,"repository");
        File extendModuleUtil = new File(extendJavaBasePackage,"util");
        File methodInterceptImpl = new File(extendJavaBasePackage,"methodInterceptImpl");
        if(!resourcePath.exists()){
            boolean resourcePathDir = resourcePath.mkdirs();
            if(!resourcePathDir){
                logger.error(new StringBuilder("创建目录:").append(resourcePath.getAbsolutePath()).append(" 失败").toString());
            }
        }

        if(!methodInterceptImpl.exists()){
            boolean methodInterceptImplDir = methodInterceptImpl.mkdirs();
            if(!methodInterceptImplDir){
                logger.error(new StringBuilder("创建目录:").append(methodInterceptImpl.getAbsolutePath()).append(" 失败").toString());
            }
        }

        if(!extendModuleController.exists()) {
            boolean extendModuleControllerDir = extendModuleController.mkdirs();
            if (!extendModuleControllerDir) {
                logger.error(new StringBuilder("创建目录:").append(extendModuleController.getAbsolutePath()).append(" 失败").toString());
            }
        }
        if(!extendModuleRepository.exists()) {
            boolean extendModuleRepositoryDir = extendModuleRepository.mkdirs();
            if (!extendModuleRepositoryDir) {
                logger.error(new StringBuilder("创建目录:").append(extendModuleRepository.getAbsolutePath()).append(" 失败").toString());
            }
        }

        if(!extendModuleService.exists()) {
            boolean extendModuleServiceDir = extendModuleService.mkdirs();
            if (!extendModuleServiceDir) {
                logger.error(new StringBuilder("创建目录:").append(extendModuleService.getAbsolutePath()).append(" 失败").toString());
            }
        }

        if(!extendModuleServiceImpl.exists()) {
            boolean extendModuleServiceImplDir = extendModuleServiceImpl.mkdirs();
            if (!extendModuleServiceImplDir) {
                logger.error(new StringBuilder("创建目录:").append(extendModuleServiceImpl.getAbsolutePath()).append(" 失败").toString());
            }
        }

        if(!extendModuleUtil.exists()) {
            boolean extendModuleUtilDir = extendModuleUtil.mkdirs();
            if (!extendModuleUtilDir) {
                logger.error(new StringBuilder("创建目录:").append(extendModuleUtil.getAbsolutePath()).append(" 失败").toString());
            }
        }


    }
    //构建项目下代码下standard代码相关文件夹
    public static  void createDirForProjectStandard(String generatorBasePath,Project project){

//        StringBuilder sb = new StringBuilder(generatorBasePath);
//        //构建项目的根目录
//        File projectDir = new File(sb.append("/").append(project.getName()).toString());
//        //相对基本包路径
//        String relativePackagePath = project.getPackageName().replaceAll("[.]","/");
//        //相对standard包路径
//        String standardRelativePackagePath = new StringBuilder(relativePackagePath).append("/standard").toString();

        //构建模块目录结构
        for(Module module:project.getModules()){
            createDirForProjectModuleStandard(generatorBasePath,project,module);
        }
    }

    //构建项目生成代码extend代码相关路径文件夹
    public static  void createDirForProjectExtend(String generatorBasePath,Project project){
//        StringBuilder sb = new StringBuilder(generatorBasePath);
//        //构建项目的根目录
//        File projectDir = new File(sb.append("/").append(project.getName()).toString());
//        //相对基本包路径
//        String relativePackagePath = project.getPackageName().replaceAll("[.]","/");
//        //相对extend包路径
//        String extendRelativePackagePath = new StringBuilder(relativePackagePath).append("/extend").toString();
        //构建模块目录结构
        for(Module module:project.getModules()){
            createDirForProjectModuleExtend(generatorBasePath,project,module);
        }
    }

    //获取standard资源根路径
    public static String getStandardModuleResourcePath(String generatorBasePath,String projectName,String moduleName){
        return new StringBuilder(generatorBasePath).append("/").append(projectName).append("/").append(moduleName).append("/src/main/resources").toString();
    }


}
