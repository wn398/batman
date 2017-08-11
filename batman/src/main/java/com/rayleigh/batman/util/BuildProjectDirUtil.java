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

            boolean moduleResouceDir = moduleResources.mkdirs();
            if(!moduleResouceDir){
                logger.error(new StringBuilder("创建目录:").append(moduleResources.getAbsolutePath()).append(" 失败").toString());
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

            boolean moduleControllerDir = moduleController.mkdirs();
            if(!moduleControllerDir){
                logger.error(new StringBuilder("创建目录:").append(moduleController.getAbsolutePath()).append(" 失败").toString());
            }
            boolean moduleModelDir = moduleModel.mkdirs();
            if(!moduleModelDir){
                logger.error(new StringBuilder("创建目录:").append(moduleModel.getAbsolutePath()).append(" 失败").toString());
            }
            boolean moduleRepositoryDir = moduleRepository.mkdirs();
            if(!moduleRepositoryDir){
                logger.error(new StringBuilder("创建目录:").append(moduleRepository.getAbsolutePath()).append(" 失败").toString());
            }
            boolean moduleServiceDir = moduleService.mkdirs();
            if(!moduleServiceDir){
                logger.error(new StringBuilder("创建目录:").append(moduleService.getAbsolutePath()).append(" 失败").toString());
            }
            boolean moduleServiceImplDir = moduleServiceImpl.mkdirs();
            if(!moduleServiceImplDir){
                logger.error(new StringBuilder("创建目录:").append(moduleServiceImpl.getAbsolutePath()).append(" 失败").toString());
            }
            boolean moduleUtilDir = moduleUtil.mkdirs();
            if(!moduleUtilDir){
                logger.error(new StringBuilder("创建目录:").append(moduleUtil.getAbsolutePath()).append(" 失败").toString());
            }
            boolean moduleMethodModelDir = moduleMethodModel.mkdirs();
            if(!moduleMethodModelDir){
                logger.error(new StringBuilder("创建目录:").append(moduleMethodModel.getAbsolutePath()).append(" 失败").toString());
            }

            File testModuleJava = new File(moduleDir,"src/test/java");
            //test基本根路径
            File testPackagePath = new File(testModuleJava,standardRelativePackagePath);
            boolean testPackagePathDir = testPackagePath.mkdirs();
            if(!testPackagePathDir){
                logger.error(new StringBuilder("创建目录:").append(testPackagePath.getAbsolutePath()).append(" 失败").toString());
            }

            /**扩展代码的目录**/
            File extendModuleJava = new File(moduleDir,"src/main/java");
            File extendJavaBasePackage = new File(extendModuleJava,extendRelativePackagePath);

            File extendModuleController = new File(extendJavaBasePackage,"controller");
            File extendModuleService = new File(extendJavaBasePackage,"service");
            File extendModuleServiceImpl = new File(extendJavaBasePackage,"service/impl");
            File extendModuleRepository = new File(extendJavaBasePackage,"repository");
            File extendModuleUtil = new File(extendJavaBasePackage,"util");

            boolean extendModuleControllerDir = extendModuleController.mkdirs();
            if(!extendModuleControllerDir){
                logger.error(new StringBuilder("创建目录:").append(extendModuleController.getAbsolutePath()).append(" 失败").toString());
            }
            boolean extendModuleRepositoryDir = extendModuleRepository.mkdirs();
            if(!extendModuleRepositoryDir){
                logger.error(new StringBuilder("创建目录:").append(extendModuleRepository.getAbsolutePath()).append(" 失败").toString());
            }
            boolean extendModuleServiceDir = extendModuleService.mkdirs();
            if(!extendModuleServiceDir){
                logger.error(new StringBuilder("创建目录:").append(extendModuleService.getAbsolutePath()).append(" 失败").toString());
            }
            boolean extendModuleServiceImplDir = extendModuleServiceImpl.mkdirs();
            if(!extendModuleServiceImplDir){
                logger.error(new StringBuilder("创建目录:").append(extendModuleServiceImpl.getAbsolutePath()).append(" 失败").toString());
            }
            boolean extendModuleUtilDir = extendModuleUtil.mkdirs();
            if(!extendModuleUtilDir){
                logger.error(new StringBuilder("创建目录:").append(extendModuleUtil.getAbsolutePath()).append(" 失败").toString());
            }

        }
        //如果没有模块，则把目录建在项目下
        if(project.getModules().size()==0){
            //standard
            File mainJava = new File(projectDir,"src/main/java");
            File testJava = new File(projectDir,"src/test/java");
            File dirResource = new File(projectDir,"src/main/resources");


            File mainJavaBasePackage = new File(mainJava,standardRelativePackagePath);
            File testJavaBasePackage = new File(testJava,standardRelativePackagePath);

            File mainController = new File(mainJavaBasePackage,"controller");
            File mainRepository = new File(mainJavaBasePackage,"repository");
            File mainService = new File(mainJavaBasePackage,"service");
            File mainServiceImpl = new File(mainJavaBasePackage,"service/impl");
            File mainModel = new File(mainJavaBasePackage,"model");
            File mainUtil = new File(mainJavaBasePackage,"util");

            boolean testJavaBasePackageDir = testJavaBasePackage.mkdirs();
            if(!testJavaBasePackageDir){
                logger.error(new StringBuilder("创建目录:").append(testJavaBasePackage.getAbsolutePath()).append(" 失败").toString());
            }
            boolean mainControllerDir = mainController.mkdirs();
            if(!mainControllerDir){
                logger.error(new StringBuilder("创建目录:").append(mainController.getAbsolutePath()).append(" 失败").toString());
            }
            boolean mainRepositoryDir = mainRepository.mkdirs();
            if(!mainRepositoryDir){
                logger.error(new StringBuilder("创建目录:").append(mainRepository.getAbsolutePath()).append(" 失败").toString());
            }
            boolean mainServiceDir = mainService.mkdirs();
            if(!mainServiceDir){
                logger.error(new StringBuilder("创建目录:").append(mainService.getAbsolutePath()).append(" 失败").toString());
            }
            boolean mainServiceImplDir = mainServiceImpl.mkdirs();
            if(!mainServiceImplDir){
                logger.error(new StringBuilder("创建目录:").append(mainServiceImpl.getAbsolutePath()).append(" 失败").toString());
            }
            boolean mainModelDir = mainModel.mkdirs();
            if(!mainModelDir){
                logger.error(new StringBuilder("创建目录:").append(mainModel.getAbsolutePath()).append(" 失败").toString());
            }
            boolean mainUtilDir = mainUtil.mkdirs();
            if(!mainUtilDir){
                logger.error(new StringBuilder("创建目录:").append(mainUtil.getAbsolutePath()).append(" 失败").toString());
            }

            //extend
            File extendMainJava = new File(projectDir,"src/main/java");
            File extendTestJava = new File(projectDir,"src/test/java");

            File extendMainJavaBasePackage = new File(extendMainJava,extendRelativePackagePath);
            File extendTestJavaBasePackage = new File(extendTestJava,extendRelativePackagePath);

            File extendController = new File(extendMainJavaBasePackage,"controller");
            File extendRepository = new File(extendMainJavaBasePackage,"repository");
            File extendService = new File(extendMainJavaBasePackage,"service");
            File extendServiceImpl = new File(extendMainJavaBasePackage,"service/impl");
            File extendUtil = new File(extendMainJavaBasePackage,"util");

            boolean extendTestJavaBasePackageDir = extendTestJavaBasePackage.mkdirs();
            if(!extendTestJavaBasePackageDir){
                logger.error(new StringBuilder("创建目录:").append(extendTestJavaBasePackage.getAbsolutePath()).append(" 失败").toString());
            }
            boolean extendControllerDir = extendController.mkdirs();
            if(!extendControllerDir){
                logger.error(new StringBuilder("创建目录:").append(extendController.getAbsolutePath()).append(" 失败").toString());
            }
            boolean extendRepositoryDir = extendRepository.mkdirs();
            if(!extendControllerDir){
                logger.error(new StringBuilder("创建目录:").append(extendRepository.getAbsolutePath()).append(" 失败").toString());
            }
            boolean extendServiceDir = extendService.mkdirs();
            if(!extendServiceDir){
                logger.error(new StringBuilder("创建目录:").append(extendService.getAbsolutePath()).append(" 失败").toString());
            }
            boolean extendServiceImplDir = extendServiceImpl.mkdirs();
            if(!extendServiceImplDir){
                logger.error(new StringBuilder("创建目录:").append(extendServiceImpl.getAbsolutePath()).append(" 失败").toString());
            }

            boolean extendUtilDir = extendUtil.mkdirs();
            if(!extendUtilDir){
                logger.error(new StringBuilder("创建目录:").append(extendUtil.getAbsolutePath()).append(" 失败").toString());
            }

        }
    }

    //获取standard资源根路径
    public static String getStandardModuleResourcePath(String generatorBasePath,String projectName,String moduleName){
        return new StringBuilder(generatorBasePath).append("/").append(projectName).append("/").append(moduleName).append("/src/main/resources").toString();
    }


}
