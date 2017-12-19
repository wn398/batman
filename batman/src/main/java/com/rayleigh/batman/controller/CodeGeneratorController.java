package com.rayleigh.batman.controller;

import com.rayleigh.batman.model.*;
import com.rayleigh.batman.service.EntityService;
import com.rayleigh.batman.service.ProjectService;
import com.rayleigh.batman.service.SysUserService;
import com.rayleigh.batman.util.*;
import com.rayleigh.core.controller.BaseController;
import com.rayleigh.core.model.BaseModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.partitioningBy;

@Controller
@RequestMapping("/codeGeneratorCtl")
public class CodeGeneratorController extends BaseController{
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private EntityService entityService;

    private Map<String, String> tempMap = new HashMap<>();
//    @Value("${server.generator.path}")
//    private String basePath;
    @Value("${springBoot.version}")
    private String springBootVersion;
    @Autowired
    private Configuration configuration;//freemarker模板配置

    @GetMapping("/goProjectCodeGenerator")
    public String goCodeGenerator(HttpServletRequest request){
        String userId = (String)request.getSession().getAttribute("userId");
        SysUser sysUser = sysUserService.findOne(userId);
        request.setAttribute("projects",sysUser.getProjects());
        return "/page/project-list-for-code-generator";
    }

    //生成standard部分代码
    @RequestMapping("/generateProjectStandard/{projectId}")
    public void generatorByProjectStandard(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId) throws IOException {
        String realPath = request.getServletContext().getRealPath("/");
        //生成的每个项目根据时间生成对应目录
        String basePath = new StringBuilder("/").append(System.currentTimeMillis()).append("_").append(projectId).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        Project project = projectService.findOne(projectId);

        generatorProjectStandard(generatorBasePath, project);

        File sourceDir = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).toString());
        File targetFile = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).append("standard").append(".zip").toString());
        FileCompressUtil.compress(sourceDir,targetFile);

        String fileName=new String(new StringBuilder(project.getName()).append("standard").append(".zip").toString().getBytes("UTF-8"),"iso-8859-1");//为了解决中文名称乱码问题
        response.reset();
        response.addHeader("Content-Length", "" + targetFile.length());
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        try(OutputStream outputStream =response.getOutputStream();InputStream in = new FileInputStream(targetFile);){
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf, 0, 1024)) != -1) {
                outputStream.write(buf, 0, len);
            }
        }
    }

    @RequestMapping("/generateProjectExtend/{projectId}")
    public void generatorByProjectExtend(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId) throws IOException {
        String realPath = request.getServletContext().getRealPath("/");
        //生成的每个项目根据时间生成对应目录
        String basePath = new StringBuilder("/").append(System.currentTimeMillis()).append("_").append(projectId).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        Project project = projectService.findOne(projectId);

        generatorProjectExtend(generatorBasePath, project);

        File sourceDir = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).toString());
        File targetFile = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).append("extend").append(".zip").toString());
        FileCompressUtil.compress(sourceDir,targetFile);

        String fileName=new String(new StringBuilder(project.getName()).append("extend").append(".zip").toString().getBytes("UTF-8"),"iso-8859-1");//为了解决中文名称乱码问题
        response.reset();
        response.addHeader("Content-Length", "" + targetFile.length());
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        try(OutputStream outputStream =response.getOutputStream();InputStream in = new FileInputStream(targetFile);){
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf, 0, 1024)) != -1) {
                outputStream.write(buf, 0, len);
            }
        }
    }
    //生成项目全部代码
    @RequestMapping("/generateProject/{projectId}")
    public void generatorByProject(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId) throws IOException {
        String realPath = request.getServletContext().getRealPath("/");
        tempMap.put("port",request.getServerPort()+"");
        tempMap.put("root",request.getRequestURI().split("/")[1]);
        //生成的每个项目根据时间生成对应目录
        String basePath = new StringBuilder("/").append(System.currentTimeMillis()).append("_").append(projectId).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        Project project = projectService.findOne(projectId);

        generatorProject(generatorBasePath, project);

        File sourceDir = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).toString());
        File targetFile = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).append(".zip").toString());
        FileCompressUtil.compress(sourceDir,targetFile);

        String fileName=new String(new StringBuilder(project.getName()).append(".zip").toString().getBytes("UTF-8"),"iso-8859-1");//为了解决中文名称乱码问题
        response.reset();
        response.addHeader("Content-Length", "" + targetFile.length());
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        try(OutputStream outputStream =response.getOutputStream();InputStream in = new FileInputStream(targetFile);){
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf, 0, 1024)) != -1) {
                outputStream.write(buf, 0, len);
            }
        }
    }
    //生成standard部分代码
    private void generatorProjectStandard(String generatorBasePath, Project project) {
        BuildProjectDirUtil.createDirForProjectStandard(generatorBasePath,project);
        for(Module module:project.getModules()) {
            //获取模块根路径，用于pom文件
            File moduleRootPath = new File(BuildProjectDirUtil.getModuleBasePath(generatorBasePath,project.getName(),module.getName()));
            //standard Model根路径
            File standardModelPath = new File(BuildProjectDirUtil.getStandardModelPath(generatorBasePath, project.getName(),module.getName(),project.getPackageName()));
            //standard Repository根路径
            File standardRepositoryPath = new File(BuildProjectDirUtil.getStandardRepositoryPath(generatorBasePath,project.getName(),module.getName(),project.getPackageName()));
            //standard Java根路径
            File standardJavaRootPath = new File(BuildProjectDirUtil.getStandardJavaBasePackagePath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
            // standard Service根路径
            File standardServicePath = new File(BuildProjectDirUtil.getStandardServicePath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
            //standard Controller根路径
            File standardControllerPath = new File(BuildProjectDirUtil.getStandardControllerPath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
            //standard资源根路径
            File resourceRootPath = new File(BuildProjectDirUtil.getStandardModuleResourcePath(generatorBasePath, project.getName(), module.getName()));
            //standard util路径
            File standardEntityUtil = new File(BuildProjectDirUtil.getStandardUtilPath(generatorBasePath, project.getName(), module.getName(),project.getPackageName()));
            //standard methodModel 方法的入参和结果模型目录
            File standardMethodModelPath = new File(BuildProjectDirUtil.getStandardMethodModel(generatorBasePath, project.getName(), module.getName(),project.getPackageName()));
            //standard modelRelation 路径
            File standardModelRelation = new File(BuildProjectDirUtil.getModuleRelationDirPath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
            //standard serviceImpl路径
            File standardServiceImplPath = new File(BuildProjectDirUtil.getStandardServiceImplPath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));

             //生成entity文件
            generateModelFile(standardModelPath,project,module);
            //生成standRepository文件
            generateRepositoryFile(standardRepositoryPath,project,module,false);
            //生成standService文件
            generateServiceFile(standardServicePath,project,module,false);
            //生成方法的入参和结果类型 ---【2017-8-4】
            generateMethodWrapperFile(standardMethodModelPath,project,module);
            //生成modelRelation文件
            generateModuleRelationFile(standardModelRelation,project,module);
            //生成standServiceImpl文件
            generateServiceImplFile(standardServiceImplPath,project,module,false);
            //生成standController文件
            generateControllerFile(standardControllerPath,project,module,false);
            //生成standardEntityUtil文件
            generateStandardUtilFile(standardEntityUtil,project,module,false);
        }
    }
    //生成项目extend部分代码文件
    private void generatorProjectExtend(String generatorBasePath, Project project) {
        BuildProjectDirUtil.createDirForProjectExtend(generatorBasePath,project);
        for(Module module:project.getModules()) {
            //extend Java根路径
            File extendJavaRootPath = new File(BuildProjectDirUtil.getExtendJavaBasePackagePath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
            //extend repository根路径
            File extendRepositoryPath = new File(BuildProjectDirUtil.getExtendRepositoryPath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
            //extend service根路径
            File extendServicePath = new File(BuildProjectDirUtil.getExtendServicePath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
            //extend controller根路径
            File extendControllerPath = new File(BuildProjectDirUtil.getExtendControllerPath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
            //extend serviceImpl路径
            File extendServiceImplPath = new File(BuildProjectDirUtil.getExtendServiceImplPath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));

            //生成extendRepository文件
            generateRepositoryFile(extendRepositoryPath,project,module,true);
            //生成extendService文件
            generateServiceFile(extendServicePath,project,module,true);
            //生成extendServiceImpl文件
            generateServiceImplFile(extendServiceImplPath,project,module,true);
            //生成extendController文件
            generateControllerFile(extendControllerPath,project,module,true);
        }
    }
    //生成项目全部
    private void generatorProject(String generatorBasePath, Project project) {
        BuildProjectDirUtil.createDirForProject(generatorBasePath,project);
        //项目根路径
        File projectRoot = new File(BuildProjectDirUtil.getProjectBasePath(generatorBasePath,project.getName()));
        //生成项目pom文件
        generatorProjectPomFile(projectRoot,project);
        //生成update.sh文件
        generatorUpdateFile(projectRoot,project);
        //生成git .ignore文件
        generatorIgnoreFile(projectRoot,project);

        for(Module module:project.getModules()) {
            //获取模块根路径，用于pom文件
            File moduleRootPath = new File(BuildProjectDirUtil.getModuleBasePath(generatorBasePath,project.getName(),module.getName()));
            //standard Model根路径
            File standardModelPath = new File(BuildProjectDirUtil.getStandardModelPath(generatorBasePath, project.getName(),module.getName(),project.getPackageName()));
            //standard Repository根路径
            File standardRepositoryPath = new File(BuildProjectDirUtil.getStandardRepositoryPath(generatorBasePath,project.getName(),module.getName(),project.getPackageName()));
            //standard Java根路径
            File standardJavaRootPath = new File(BuildProjectDirUtil.getStandardJavaBasePackagePath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
            // standard Service根路径
            File standardServicePath = new File(BuildProjectDirUtil.getStandardServicePath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
            //standard Controller根路径
            File standardControllerPath = new File(BuildProjectDirUtil.getStandardControllerPath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
            //standard资源根路径
            File resourceRootPath = new File(BuildProjectDirUtil.getStandardModuleResourcePath(generatorBasePath, project.getName(), module.getName()));
            //standard util路径
            File standardEntityUtil = new File(BuildProjectDirUtil.getStandardUtilPath(generatorBasePath, project.getName(), module.getName(),project.getPackageName()));
            //standard methodModel 方法的入参和结果模型目录
            File standardMethodModelPath = new File(BuildProjectDirUtil.getStandardMethodModel(generatorBasePath, project.getName(), module.getName(),project.getPackageName()));
            //standard modelRelation 路径
            File standardModelRelation = new File(BuildProjectDirUtil.getModuleRelationDirPath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
            //extend Java根路径
            File extendJavaRootPath = new File(BuildProjectDirUtil.getExtendJavaBasePackagePath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
            //extend repository根路径
            File extendRepositoryPath = new File(BuildProjectDirUtil.getExtendRepositoryPath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
            //extend service根路径
            File extendServicePath = new File(BuildProjectDirUtil.getExtendServicePath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
            //extend controller根路径
            File extendControllerPath = new File(BuildProjectDirUtil.getExtendControllerPath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
            //基本包路径 com.tim
            File relativePackagePath = new File(BuildProjectDirUtil.getRelativePackagePath(generatorBasePath,project.getName(),module.getName(),project.getPackageName()));
            //standard serviceImpl路径
            File standardServiceImplPath = new File(BuildProjectDirUtil.getStandardServiceImplPath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
            //extend serviceImpl路径
            File extendServiceImplPath = new File(BuildProjectDirUtil.getExtendServiceImplPath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));

            //生成根目录下application.property文件
            generateApplicationPropertyFile(resourceRootPath,project,module);
            //生成根目录下applicationProProperty.property文件
            generateApplicationProPropertyFile(resourceRootPath,project,module);
            //生成根目录下的application-dev.property文件
            generateApplicationDevPropertyFile(resourceRootPath,project,module);
            //生成日志配置文件
            generateLogbackConfigerationFile(resourceRootPath,project,module);
            //生成模块applicationJava文件,放在基础路径下
            generateApplicationJavaFile(relativePackagePath,project,module);
            //生成模块pom.xml文件
            generatePOMFile(moduleRootPath,project,module);
            //生成模块pom-war.xml文件
            generatePOMWarFile(moduleRootPath,project,module);
            //生成entity文件
            generateModelFile(standardModelPath,project,module);
            //生成standRepository文件
            generateRepositoryFile(standardRepositoryPath,project,module,false);
            //生成extendRepository文件
            generateRepositoryFile(extendRepositoryPath,project,module,true);
            //生成standService文件
            generateServiceFile(standardServicePath,project,module,false);
            //生成方法的入参和结果类型 ---【2017-8-4】
            generateMethodWrapperFile(standardMethodModelPath,project,module);
            //生成modelRelation文件
            generateModuleRelationFile(standardModelRelation,project,module);
            //生成extendService文件
            generateServiceFile(extendServicePath,project,module,true);
            //生成standServiceImpl文件
            generateServiceImplFile(standardServiceImplPath,project,module,false);
            //生成extendServiceImpl文件
            generateServiceImplFile(extendServiceImplPath,project,module,true);
            //生成standController文件
            generateControllerFile(standardControllerPath,project,module,false);
            //生成extendController文件
            generateControllerFile(extendControllerPath,project,module,true);
            //生成standardEntityUtil文件
            generateStandardUtilFile(standardEntityUtil,project,module,false);
        }
    }

    //生成application.java文件
    private void generateApplicationJavaFile(File extendJavaRootPath, Project project, Module module) {
        try {
            Template template = configuration.getTemplate("applicationJava.ftl");
            Map<String, BaseModel> map = new HashMap<>();
            map.put("project",project);
            map.put("module",module);
            File pomFile = new File(extendJavaRootPath, GeneratorStringUtil.upperFirstLetter(module.getName())+"Application.java");

            try(Writer writer = new OutputStreamWriter(new FileOutputStream(pomFile),"utf-8");) {
                template.process(map, writer);
                writer.flush();
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取projectPom.ftl模板失败");
        }
    }

    //生成工程pom文件
    private void generatorProjectPomFile(File projectRootDir, Project project) {
        try {
            Template template = configuration.getTemplate("projectPom.ftl");
            Map<String, Project> map = new HashMap<>();
            map.put("project",project);
            File pomFile = new File(projectRootDir,"pom.xml");

            try(Writer writer = new OutputStreamWriter(new FileOutputStream(pomFile),"utf-8");) {
                template.process(map, writer);
                writer.flush();
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取projectPom.ftl模板失败");
        }
    }

    //生成update.sh文件，更新代码脚本文件
    private void generatorUpdateFile(File projectRootDir, Project project) {
        try {
            Template template = configuration.getTemplate("updateScript.ftl");
            Map<String, Object> map = new HashMap<>();
            map.put("port",tempMap.get("port"));
            map.put("root",tempMap.get("root"));
            map.put("project",project);
            map.put("ip", NetworkUtil.getLocalHostLANAddress().getHostAddress());
            File pomFile = new File(projectRootDir,"update.sh");

            try(Writer writer = new OutputStreamWriter(new FileOutputStream(pomFile),"utf-8");) {
                template.process(map, writer);
                writer.flush();
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取updateScript.ftl模板失败");
        }
    }

    private void generatorIgnoreFile(File projectRootDir, Project project) {
        try {
            Template template = configuration.getTemplate("gitIgnore.ftl");
            Map<String, Project> map = new HashMap<>();
            map.put("project",project);
            File pomFile = new File(projectRootDir,".gitignore");

            try(Writer writer = new OutputStreamWriter(new FileOutputStream(pomFile),"utf-8");) {
                template.process(map, writer);
                writer.flush();
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取gitIgnore.ftl模板失败");
        }
    }
    //生成模块POM文件
    private void generatePOMFile(File dir,Project project,Module module){
            try {
                Template template = configuration.getTemplate("modulePom.ftl");
                Map<String, String> map = new HashMap<>();
                map.put("projectName",project.getName());
                map.put("moduleName",module.getName());
                map.put("basePackage",project.getPackageName());
                map.put("springBootVersion",springBootVersion);
                File pomFile = new File(dir,"pom.xml");

                try(Writer writer = new OutputStreamWriter(new FileOutputStream(pomFile),"utf-8");) {
                    template.process(map, writer);
                    writer.flush();
                }
            }catch (Exception e){
                e.printStackTrace();
                logger.error("获取modulePom.ftl模板失败");
            }
    }
    //生成模块pom-war文件
    private void generatePOMWarFile(File dir,Project project,Module module){
        try {
            Template template = configuration.getTemplate("modulePom-War.ftl");
            Map<String, String> map = new HashMap<>();
            map.put("projectName",project.getName());
            map.put("moduleName",module.getName());
            map.put("basePackage",project.getPackageName());
            map.put("springBootVersion",springBootVersion);
            File pomFile = new File(dir,"pom-war.xml");

            try(Writer writer = new OutputStreamWriter(new FileOutputStream(pomFile),"utf-8");) {
                template.process(map, writer);
                writer.flush();
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取modulePom-War.ftl模板失败");
        }
    }
    //生成application文件
    private void generateApplicationPropertyFile(File dir, Project project,Module module){
        try {
            Template template = configuration.getTemplate("application.ftl");
//            Map<String, Object> map = new HashMap<>();
//            String port = new Random().nextInt(9000)+8080+"";
//            //设置随机端口
//            project.setPort(port);
//            map.put("module",module);
//            map.put("project",project);
            File applicationFile = new File(dir,"application.properties");
            try(Writer writer = new OutputStreamWriter(new FileOutputStream(applicationFile),"utf-8");) {
                template.process(null, writer);
                writer.flush();
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取application模板失败");
        }
    }

    //生成application-pro.properties文件
    private void generateApplicationProPropertyFile(File dir, Project project,Module module){
        try {
            Template template = configuration.getTemplate("application-pro.ftl");
            Map<String, Object> map = new HashMap<>();
            String port = new Random().nextInt(9000)+8080+"";
            //设置随机端口
            project.setPort(port);
            map.put("module",module);
            map.put("project",project);
            List<ProjectDataSource> projectDataSources = project.getProjectDataSources();
            ProjectDataSource mainDataSource = null;
            List<ProjectDataSource> otherDataSourceList = null;
            if(null!=projectDataSources && projectDataSources.size()>0){
                Map<Boolean,List<ProjectDataSource>> map2 = projectDataSources.parallelStream().collect(partitioningBy(it -> it.getIsMainDataSource()));
                List<ProjectDataSource> mainList = map2.get(true);
                otherDataSourceList = map2.get(false);
                if(null!=mainList && mainList.size()==1){
                    mainDataSource = mainList.get(0);
                }
                if(null!=otherDataSourceList&&otherDataSourceList.size()>0){
                    String otherDataSourceNickNames = otherDataSourceList.parallelStream().map(it->it.getDataSourceNickName()).collect(Collectors.joining(","));
                    map.put("otherDataSourceNames",otherDataSourceNickNames);
                }

            }
            map.put("mainDataSource",mainDataSource);
            map.put("otherDataSources",otherDataSourceList);
            File applicationFile = new File(dir,"application-pro.properties");
            try(Writer writer = new OutputStreamWriter(new FileOutputStream(applicationFile),"utf-8");) {
                template.process(map, writer);
                writer.flush();
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取application模板失败");
        }
    }

    //生成application-dev文件
    private void generateApplicationDevPropertyFile(File dir, Project project,Module module){
        try {
            Template template = configuration.getTemplate("application-dev.ftl");
            Map<String, Object> map = new HashMap<>();
            String port = new Random().nextInt(9000)+8080+"";
            //设置随机端口
            project.setPort(port);
            map.put("module",module);
            map.put("project",project);
            map.put("GeneratorStringUtil",new GeneratorStringUtil());
            List<ProjectDataSource> projectDataSources = project.getProjectDataSources();
            ProjectDataSource mainDataSource = null;
            List<ProjectDataSource> otherDataSourceList = null;
            if(null!=projectDataSources && projectDataSources.size()>0){
                Map<Boolean,List<ProjectDataSource>> map2 = projectDataSources.parallelStream().collect(partitioningBy(it -> it.getIsMainDataSource()));
                List<ProjectDataSource> mainList = map2.get(true);
                otherDataSourceList = map2.get(false);
                if(null!=mainList && mainList.size()==1){
                    mainDataSource = mainList.get(0);
                }
                if(null!=otherDataSourceList&&otherDataSourceList.size()>0){
                    String otherDataSourceNickNames = otherDataSourceList.parallelStream().map(it->it.getDataSourceNickName()).collect(Collectors.joining(","));
                    map.put("otherDataSourceNames",otherDataSourceNickNames);
                }

            }
            map.put("mainDataSource",mainDataSource);
            map.put("otherDataSources",otherDataSourceList);
            File applicationFile = new File(dir,"application-dev.properties");
            try(Writer writer = new OutputStreamWriter(new FileOutputStream(applicationFile),"utf-8");) {
                template.process(map, writer);
                writer.flush();
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取application模板失败");
        }
    }

    private void generateLogbackConfigerationFile(File dir,Project project,Module module){
        try {
            Template template = configuration.getTemplate("logbackXML.ftl");
            Map<String, Object> map = new HashMap<>();
            map.put("module",module);
            File applicationFile = new File(dir,"logback-spring.xml");
            try(Writer writer = new OutputStreamWriter(new FileOutputStream(applicationFile),"utf-8");) {
                template.process(map, writer);
                writer.flush();
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取logbackXML模板失败");
        }


    }
    //生成实体类文件
    private void generateModelFile(File dir,Project project,Module module){
        try {
            Template template = configuration.getTemplate("entity.ftl");
            for(Entities entity:module.getEntities()){
                Map<String,Object> map = new HashMap();
                map.put("project",project);
                map.put("entity",entity);
                map.put("GeneratorStringUtil",new GeneratorStringUtil());
                File entityFile = new File(dir,entity.getName()+".java");
                try(Writer writer = new OutputStreamWriter(new FileOutputStream(entityFile),"utf-8");) {
                    template.process(map, writer);
                    writer.flush();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取entity模板失败");
        }
    }

    //生成Repository文件
    private void generateRepositoryFile(File dir,Project project,Module module,boolean isExtend){
        String templateName;
        String fileSuffix;
        if(isExtend){
            templateName = "extendRepository.ftl";
            fileSuffix = "ExtendRepository.java";
        }else{
            templateName = "standardRepository.ftl";
            fileSuffix = "Repository.java";
        }
        ProcessTemplate(dir, project, module, templateName, fileSuffix);
    }

    //生成service文件
    private void generateServiceFile(File dir,Project project,Module module,boolean isExtend){
        String templateName;
        String fileSuffix;
        if(isExtend){
            templateName = "extendService.ftl";
            fileSuffix = "ExtendService.java";
        }else{
            templateName = "standardService.ftl";
            fileSuffix = "Service.java";
        }
        ProcessTemplate(dir, project, module, templateName, fileSuffix);
    }
    //生成方法的入参和出参包装类
    private void generateMethodWrapperFile(File dir,Project project,Module module){
        String paramTemplateFile = "methodParamWrapper.ftl";
        String resultTemplateFile = "methodResultWrapper.ftl";
        try {
            Template paramTemplate = configuration.getTemplate(paramTemplateFile);
            Template resultTemplate = configuration.getTemplate(resultTemplateFile);
            for (Entities entities : module.getEntities()) {
                for (SearchMethod method : entities.getMethods()) {
                    Map<String,Object> map = new HashMap();
                    map.put("project", project);
                    map.put("entity", entities);
                    map.put("method",method);
                    map.put("searchDBUtil",new SearchDBUtil());
                    map.put("constructSearchMethodUtil",new ConstructSearchMethodUtil());
                    File paramFile = new File(dir,entities.getName()+"$"+method.getMethodName().substring(0,1).toUpperCase()+method.getMethodName().substring(1)+"ParamWrapper.java");
                    File resultFile = new File(dir,entities.getName()+"$"+method.getMethodName().substring(0,1).toUpperCase()+method.getMethodName().substring(1)+"ResultWrapper.java");
                    try(Writer writer = new OutputStreamWriter(new FileOutputStream(paramFile),"utf-8");) {
                        paramTemplate.process(map, writer);
                        writer.flush();
                    }
                    //如果方法返回类型选择了主对象类型，则不需要包装结果返回类型,默认返回包装类型
                    if(null == method.getIsReturnObject()||(null != method.getIsReturnObject() && method.getIsReturnObject()== false)) {
                        try (Writer writer = new OutputStreamWriter(new FileOutputStream(resultFile), "utf-8");) {
                            resultTemplate.process(map, writer);
                            writer.flush();
                        }
                    }
                }
            }
        }catch (RuntimeException e){
            e.printStackTrace();
            logger.error("处理方法包装类失败");
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }

    }
    //生成每个类关联关系包装文件，为获取实体时，findOneWithRelationObj
    private void generateModuleRelationFile(File dir,Project project,Module module){
        String templateFile = "modelRelationWrapper.ftl";
        try {
            Template modelRelationTemplate = configuration.getTemplate(templateFile);
            for (Entities entities : module.getEntities()) {
                    Map<String,Object> map = new HashMap();
                    map.put("project", project);
                    map.put("entity", entities);
                    File modelRelationFile = new File(dir,entities.getName()+"$Relation.java");
                    try(Writer writer = new OutputStreamWriter(new FileOutputStream(modelRelationFile),"utf-8");) {
                        modelRelationTemplate.process(map, writer);
                        writer.flush();
                    }
            }
        }catch (RuntimeException e){
            e.printStackTrace();
            logger.error("处理方法包装类失败");
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    //生成serviceImpl文件
    private void generateServiceImplFile(File dir,Project project,Module module,boolean isExtend){
        String templateName;
        String fileSuffix;
        if(isExtend){
            templateName = "extendServiceImpl.ftl";
            fileSuffix = "ExtendServiceImpl.java";
        }else{
            templateName = "standardServiceImpl.ftl";
            fileSuffix = "ServiceImpl.java";
        }
        try {
            Template template = configuration.getTemplate(templateName);
            for(Entities entity:module.getEntities()) {
                Map<String,Object> map = new HashMap();
                map.put("project", project);
                map.put("entity", entity);
                map.put("searchDBUtil",new SearchDBUtil());
                map.put("constructSearchMethodUtil",new ConstructSearchMethodUtil());
                map.put("generatorStringUtil",new GeneratorStringUtil());
                File serviceFile = new File(dir,entity.getName()+fileSuffix);

                try(Writer writer = new OutputStreamWriter(new FileOutputStream(serviceFile),"utf-8");) {
                    template.process(map, writer);
                    writer.flush();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取"+templateName+"模板失败");
        }
    }

    //生成controller文件
    private void generateControllerFile(File dir,Project project,Module module,boolean isExtend){
        String templateName;
        String fileSuffix;
        if(isExtend){
            templateName = "extendController.ftl";
            fileSuffix = "ExtendController.java";
        }else{
            templateName = "standardController.ftl";
            fileSuffix = "Controller.java";
        }
        ProcessTemplate(dir, project, module, templateName, fileSuffix);
    }
    //生成util文件
    private void generateStandardUtilFile(File dir,Project project,Module module,boolean isExtend){
        String templateName;
        String fileSuffix;
        templateName = "standardEntityUtil.ftl";
        fileSuffix = "Util.java";
        ProcessTemplate(dir, project, module, templateName, fileSuffix);
    }

    //处理模板文件
    private void ProcessTemplate(File dir, Project project, Module module, String templateName, String fileSuffix) {
        try {
            Template template = configuration.getTemplate(templateName);
            for(Entities entity:module.getEntities()) {
                Map<String,BaseModel> map = new HashMap();
                map.put("project", project);
                map.put("entity", entity);
                File serviceFile = new File(dir,entity.getName()+fileSuffix);

                try(Writer writer = new OutputStreamWriter(new FileOutputStream(serviceFile),"utf-8");) {
                    template.process(map, writer);
                    writer.flush();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取"+templateName+"模板失败");
        }
    }


}
