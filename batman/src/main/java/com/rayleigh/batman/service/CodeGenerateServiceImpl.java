package com.rayleigh.batman.service;

import com.rayleigh.batman.model.*;
import com.rayleigh.batman.util.*;
import com.rayleigh.core.async.AsyncServiceUtil;
import com.rayleigh.core.util.AESEncoderUtil;
import com.rayleigh.core.util.StringUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.partitioningBy;

/**
 *
 */
@Service
public class CodeGenerateServiceImpl implements CodeGenerateService{
    private Logger logger = LoggerFactory.getLogger(CodeGenerateServiceImpl.class);

    @Autowired
    private Configuration configuration;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private BuildService buildService;

    @Value("${batman.aesEncode.rule}")
    private String aesEndoeRule;

    @Autowired
    private AsyncServiceUtil asyncServiceUtil;


    //==================以下为生成文件夹组合方法 ==================================================
    //生成模块标准部分代码并打包发布
    //@Async
    @Override
    public void produceModuleStandardJar(String generateBasePath, Module module,Project project) {

        String realPath = generateBasePath;

        //生成的每个项目根据moduleId成对应目录
        String basePath = new StringBuilder("/").append(module.getId()).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        File sourceDir = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).append("/").append(module.getName()).toString());

        File jarFile = new File(sourceDir.getPath()+"/target/"+project.getName()+"-"+module.getName()+"Base-0.0.1-SNAPSHOT.jar");
        if(jarFile.exists()) {
            boolean isGenerate = checkIsNewGenerate(jarFile, project.getUpdateDate().getTime(), module.getUpdateDate().getTime());
            if (isGenerate) {
                logger.info(new StringBuilder("文件【").append(project.getName()).append("-").append(module.getName()).append("Base-0.0.1-SNAPSHOT.jar").append("】已存在，并且需要更新").toString());
                produceProjectModuleStandard(generatorBasePath, project,module);
                buildService.deployJarFile(sourceDir);
            }else{
                logger.info(new StringBuilder("文件【").append(project.getName()).append("-").append(module.getName()).append("Base-0.0.1-SNAPSHOT.jar").append("】已存在，并且不需要更新").toString());
                return;
            }
        }else{
            logger.info(new StringBuilder("文件【").append(project.getName()).append("-").append(module.getName()).append("Base-0.0.1-SNAPSHOT.jar").append("】不存在,立即生成").toString());
            produceProjectModuleStandard(generatorBasePath, project,module);
            buildService.deployJarFile(sourceDir);
        }

    }

    @Override
    public void produceModuleStandard(String generatorBasePath, String moduleId) {
        Module module = moduleService.findOne(moduleId);
        Project project = module.getProject();
        BuildProjectDirUtil.createDirForProjectModuleStandard(generatorBasePath,project,module);
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
        //standard MethodIntercept根路径
        File standardMethodInterceptPath = new File(BuildProjectDirUtil.getStandardMethodInterceptPath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
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
        //生成standMethodIntercept文件
        generateMethodInterceptFile(standardMethodInterceptPath,project,module);
        //生成standardEntityUtil文件
        generateStandardUtilFile(standardEntityUtil,project,module,false);

    }

    //生成项目全部 standard部分代码
    @Override
    public void produceProjectStandard(String generatorBasePath, Project project) {
        BuildProjectDirUtil.createDirForProjectStandard(generatorBasePath,project);
        for(Module module:project.getModules()) {
            produceModuleStandardAllFiles(generatorBasePath,project,module);
        }
    }

    //生成项目module全部 standard部分代码
    public void produceProjectModuleStandard(String generatorBasePath, Project project,Module module) {
        BuildProjectDirUtil.createDirForProjectModuleStandard(generatorBasePath,project,module);
        produceModuleStandardAllFiles(generatorBasePath,project,module);
    }




    //生成项目全部extend部分代码文件
    @Override
    public void produceProjectExtend(String generatorBasePath, Project project) {
        BuildProjectDirUtil.createDirForProjectExtend(generatorBasePath,project);
        for(Module module:project.getModules()) {
            produceModuleExtendAllFiles(generatorBasePath, project, module);
        }
    }


    //生成项目全部文件
    @Override
    public void produceProjectAll(String generatorBasePath, Project project,String port,String root) {
        BuildProjectDirUtil.createDirForProject(generatorBasePath,project);
        //项目根路径
        File projectRoot = new File(BuildProjectDirUtil.getProjectBasePath(generatorBasePath,project.getName()));
        //生成项目pom文件
        generatorProjectPomFile(projectRoot,project);
        //生成项目update.sh文件
        generatorUpdateFile(projectRoot,project,port,root);
        //生成项目git.ignore文件
        generatorIgnoreFile(projectRoot,project);

        for(Module module:project.getModules()) {
            //生成模块下其它文件
            produceModuleOtherFiles(generatorBasePath,project,module,port,root);
            //生成standard路径下所有文件
            produceModuleStandardAllFiles(generatorBasePath,project,module);
            //produceModuleStandardJar(generatorBasePath,module.getId());
            //生成extend路径下所有文件
            produceModuleExtendAllFiles(generatorBasePath,project,module);
        }
    }

    //生成项目全部文件,标准部分采用jar方式
    @Override
    public void produceProjectAllWithStandardJar(String generatorBasePath, Project project,String port,String root) {
        //BuildProjectDirUtil.createDirForProject(generatorBasePath,project);
        BuildProjectDirUtil.createDirForProjectExtend(generatorBasePath,project);
        //项目根路径
        File projectRoot = new File(BuildProjectDirUtil.getProjectBasePath(generatorBasePath,project.getName()));
        //生成项目pom文件
        generatorProjectPomFile(projectRoot,project);
        //生成项目update.sh文件
        generatorUpdateFile(projectRoot,project,port,root);
        //生成项目git.ignore文件
        generatorIgnoreFile(projectRoot,project);

        for(Module module:project.getModules()) {
            //生成模块下其它文件
            produceModuleOtherFiles(generatorBasePath,project,module,port,root);
            //生成standard路径下所有文件
            //produceModuleStandardAllFiles(generatorBasePath,project,module);
            produceModuleStandardJar(generatorBasePath,module,project);
            //生成extend路径下所有文件
            produceModuleExtendAllFiles(generatorBasePath,project,module);
        }
    }

    //生成项目下某一模块的除了standard,extend外的配置文件，包括配置文件
    @Override
    public void produceModuleOtherFiles(String generatorBasePath, Project project, Module module,String port,String root){
        //生成所需要的目录
        //BuildProjectDirUtil.createDirForProjectModuleStandard(generatorBasePath,project,module);
        //生成扩展部分目录
        BuildProjectDirUtil.createDirForProjectModuleExtend(generatorBasePath,project,module);

        //获取模块根路径，用于pom文件
        File moduleRootPath = new File(BuildProjectDirUtil.getModuleBasePath(generatorBasePath,project.getName(),module.getName()));
        //standard资源根路径
        File resourceRootPath = new File(BuildProjectDirUtil.getStandardModuleResourcePath(generatorBasePath, project.getName(), module.getName()));
        //基本包路径 com.tim
        File relativePackagePath = new File(BuildProjectDirUtil.getRelativePackagePath(generatorBasePath,project.getName(),module.getName(),project.getPackageName()));
        //基本包下的filter路径
        File filterPackagePath = new File(BuildProjectDirUtil.getRelativeFilterPath(generatorBasePath,project.getName(),module.getName(),project.getPackageName()));
        filterPackagePath.mkdirs();

        //生成模块根目录下application.property文件
        generateApplicationPropertyFile(resourceRootPath,project,module);
        //生成模块根目录下application-pro.property文件
        generateApplicationProPropertyFile(resourceRootPath,project,module);
        //生成模块根目录下的application-dev.property文件
        generateApplicationDevPropertyFile(resourceRootPath,project,module);
        //生成模块permission文件
        generatePermissionPropertyFile(resourceRootPath,project,module);
        //生成模块filter.java文件
        generateJwtFilterFile(filterPackagePath,project,module);
        //生成日志配置文件
        generateLogbackConfigerationFile(resourceRootPath,project,module);
        //生成模块applicationJava文件,放在基础路径下
        generateApplicationJavaFile(relativePackagePath,project,module);
        //生成模块pom.xml文件
        generateModulePOMFile(moduleRootPath,project,module);
        //生成模块pom-war.xml文件
        generatePOMWarFile(moduleRootPath,project,module);
        //生成模块findbugs-include.xml文件
        generateFindBugsIncludeFile(moduleRootPath,project,module);
        //生成模块下的update.sh文件
        generatorModuleUpdateFile(moduleRootPath,project,module,port,root);

    }


    //生成某一模块标准部分代码standard
    @Override
    public void produceModuleStandardAllFiles(String generatorBasePath, Project project, Module module){

        BuildProjectDirUtil.createDirForProjectModuleStandard(generatorBasePath,project,module);
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
        //standard MethodIntercept根路径
        File standardMethodInterceptPath = new File(BuildProjectDirUtil.getStandardMethodInterceptPath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
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
        generateModelFile(standardModelPath, project, module);
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
        //生成standMethodIntercept文件
        generateMethodInterceptFile(standardMethodInterceptPath,project,module);
        //生成standardEntityUtil文件
        generateStandardUtilFile(standardEntityUtil,project,module,false);
        //生成pom文件
        generateModuleStandardPOMFile(moduleRootPath,project,module);
    }
    //生成某一模块扩展代码extend
    @Override
    public void produceModuleExtendAllFiles(String generatorBasePath, Project project, Module module){
        BuildProjectDirUtil.createDirForProjectModuleExtend(generatorBasePath,project,module);
        //extend Java根路径
        File extendJavaRootPath = new File(BuildProjectDirUtil.getExtendJavaBasePackagePath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
        //extend repository根路径
        File extendRepositoryPath = new File(BuildProjectDirUtil.getExtendRepositoryPath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
        //extend service根路径
        File extendServicePath = new File(BuildProjectDirUtil.getExtendServicePath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
        //extend controller根路径
        File extendControllerPath = new File(BuildProjectDirUtil.getExtendControllerPath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
        //extend methodInterceptImpl根路径
        File extendMethodInterceptImplPath = new File(BuildProjectDirUtil.getExtendMethodInterceptImplPath(generatorBasePath, project.getName(), module.getName(), project.getPackageName()));
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
        //生成extendMethodInterceptImpl文件
        generateMethodInterceptImplFile(extendMethodInterceptImplPath,project,module);
    }
//======================================================================以下为生成文件========================================================================================

    //生成application.java文件
    private void generateApplicationJavaFile(File extendJavaRootPath, Project project, Module module) {
        try {
            Map<String, BatmanBaseModel> map = new HashMap<>();
            map.put("project",project);
            map.put("module",module);
            File pomFile = new File(extendJavaRootPath, GeneratorStringUtil.upperFirstLetter(module.getName())+"Application.java");
            generateFileByTemplate(pomFile,"applicationJava.ftl",map);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取｛applicationJava.ftl｝模板失败");
        }
    }



    //生成工程pom文件
    private void generatorProjectPomFile(File projectRootDir, Project project) {
        try {
            Map<String, Project> map = new HashMap<>();
            map.put("project",project);
            File pomFile = new File(projectRootDir,"pom.xml");
            generateFileByTemplate(pomFile,"projectPom.ftl",map);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取｛projectPom.ftl｝模板失败");
        }
    }

    //生成项目 update.sh文件，更新代码脚本文件
    private void generatorUpdateFile(File projectRootDir, Project project,String port,String root) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("port",port);
            map.put("root",root);
            map.put("project",project);
            map.put("ip", NetworkUtil.getLocalHostLANAddress().getHostAddress());
            File pomFile = new File(projectRootDir,"update.sh");
            generateFileByTemplate(pomFile,"updateScript.ftl",map);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取｛updateScript.ftl｝模板失败");
        }
    }


    //生成项目 模块 update.sh文件，更新代码脚本文件
    private void generatorModuleUpdateFile(File moduleRootPath, Project project,Module module,String port,String root) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("port",port);
            map.put("root",root);
            map.put("project",project);
            map.put("module",module);
            map.put("ip", NetworkUtil.getLocalHostLANAddress().getHostAddress());
            File pomFile = new File(moduleRootPath,"update.sh");
            generateFileByTemplate(pomFile,"moduleUpdateScript.ftl",map);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取｛moduleUpdateScript.ftl｝模板失败");
        }
    }

    private void generatorIgnoreFile(File projectRootDir, Project project) {
        try {
            Map<String, Project> map = new HashMap<>();
            map.put("project",project);
            File pomFile = new File(projectRootDir,".gitignore");
            generateFileByTemplate(pomFile,"gitIgnore.ftl",map);

        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取{gitIgnore.ftl}模板失败");
        }
    }
    //生成模块findbugs-include文件
    private void generateFindBugsIncludeFile(File dir,Project project,Module module){
        try {
            Map<String, String> map = new HashMap<>();
            String packageName = project.getPackageName();
            StringBuilder stringBuilder = new StringBuilder("~");
            stringBuilder.append(packageName.replaceAll("[/.]", "\\\\."));
            stringBuilder.append("\\.extend\\..*");
            map.put("path",stringBuilder.toString());
            File pomFile = new File(dir,"findbugs-include.xml");
            generateFileByTemplate(pomFile,"findbugs-include.ftl",map);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取{findbugs-include.ftl}模板失败");
        }
    }
    //生成模块POM文件
    private void generateModulePOMFile(File dir, Project project, Module module){
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("projectName",project.getName());
            map.put("moduleName",module.getName());
            map.put("basePackage",project.getPackageName());
            if(project.getProjectDataSources().size()>0) {
                map.put("isMemDatasource", false);
            }else{
                map.put("isMemDatasource", true);
            }
            File pomFile = new File(dir,"pom.xml");
            generateFileByTemplate(pomFile,"modulePom.ftl",map);

        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取{modulePom.ftl}模板失败");
        }
    }

    //生成模块POM文件
    private void generateModuleStandardPOMFile(File dir, Project project, Module module){
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("projectBasePackage",project.getPackageName());
            map.put("projectName",project.getName());
            map.put("moduleName",module.getName());
            map.put("basePackage",project.getPackageName());
            if(project.getProjectDataSources().size()>0) {
                map.put("isMemDatasource", false);
            }else{
                map.put("isMemDatasource", true);
            }
            File pomFile = new File(dir,"standard-pom.xml");
            generateFileByTemplate(pomFile,"moduleStandardPom.ftl",map);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取{moduleStandardPom.ftl}模板失败");
        }
    }
    //生成模块pom-war文件
    private void generatePOMWarFile(File dir,Project project,Module module){
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("projectName",project.getName());
            map.put("moduleName",module.getName());
            map.put("basePackage",project.getPackageName());
            if(project.getProjectDataSources().size()>0) {
                map.put("isMemDatasource", false);
            }else{
                map.put("isMemDatasource", true);
            }
            File pomFile = new File(dir,"pom-war.xml");
            generateFileByTemplate(pomFile,"modulePom-War.ftl",map);

        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取{modulePom-War.ftl}模板失败");
        }
    }
    //生成application文件
    private void generateApplicationPropertyFile(File dir, Project project,Module module){
        try {
            File applicationFile = new File(dir,"application.properties");
            generateFileByTemplate(applicationFile,"application.ftl",null);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取{application.ftl}模板失败");
        }
    }

    //生成application-pro.properties文件
    private void generateApplicationProPropertyFile(File dir, Project project,Module module){
        try {
            Map<String, Object> map = new HashMap<>();
            if(StringUtil.isEmpty(project.getPort())) {
                String port = new Random().nextInt(9000) + 8080 + "";
                //设置随机端口
                project.setPort(port);
            }
            map.put("module",module);
            map.put("project",project);
            map.put("GeneratorStringUtil",new GeneratorStringUtil());
            List<ProjectDataSource> projectDataSources = project.getProjectDataSources();
            ProjectDataSource mainDataSource = null;
            List<ProjectDataSource> otherDataSourceList = null;
            if(null!=projectDataSources && projectDataSources.size()>0){
                Map<Boolean,List<ProjectDataSource>> map2 = projectDataSources.parallelStream().collect(partitioningBy(it -> it.getIsMainDataSource()));
                List<ProjectDataSource> mainList = map2.get(true);
                otherDataSourceList = map2.get(false).parallelStream().map(it->{return ProjectDataSourceUtil.getCopy(it);}).collect(Collectors.toList());
                if(null!=mainList && mainList.size()==1){
                    mainDataSource = ProjectDataSourceUtil.getCopy(mainList.get(0));
                    if(project.getIsEncodeDataSource()){
                        String newUserName = AESEncoderUtil.AESEncode(aesEndoeRule,mainDataSource.getUsername());
                        String newPassword = AESEncoderUtil.AESEncode(aesEndoeRule,mainDataSource.getPassword());
                        if(null==newUserName||null==newPassword){
                            logger.info("加密数据源出错!");
                        }else {
                            mainDataSource.setUsername(newUserName);
                            mainDataSource.setPassword(newPassword);
                        }
                    }
                }
                if(null!=otherDataSourceList&&otherDataSourceList.size()>0){
                    String otherDataSourceNickNames = otherDataSourceList.parallelStream().map(it->it.getDataSourceNickName()).collect(Collectors.joining(","));
                    map.put("otherDataSourceNames",otherDataSourceNickNames);
                    if(project.getIsEncodeDataSource()) {
                        otherDataSourceList.parallelStream().forEach(datasource -> {
                            String newUserName = AESEncoderUtil.AESEncode(aesEndoeRule, datasource.getUsername());
                            String newPassword = AESEncoderUtil.AESEncode(aesEndoeRule, datasource.getPassword());
                            if (null == newUserName || null == newPassword) {
                                logger.info("加密数据源出错!");
                            } else {
                                datasource.setUsername(newUserName);
                                datasource.setPassword(newPassword);
                            }
                        });
                    }
                    map.put("otherDataSources",otherDataSourceList);
                }

            }
            map.put("mainDataSource",mainDataSource);
            File applicationFile = new File(dir,"application-pro.properties");
            generateFileByTemplate(applicationFile,"application-pro.ftl",map);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取{application-pro.ftl}模板失败");
        }
    }

    //生成application-dev文件
    private void generateApplicationDevPropertyFile(File dir, Project project,Module module){
        try {
            Map<String, Object> map = new HashMap<>();
            if(StringUtil.isEmpty(project.getPort())) {
                String port = new Random().nextInt(9000) + 8080 + "";
                //设置随机端口
                project.setPort(port);
            }
            map.put("module",module);
            map.put("project",project);
            map.put("GeneratorStringUtil",new GeneratorStringUtil());
            List<ProjectDataSource> projectDataSources = project.getProjectDataSources();
            ProjectDataSource mainDataSource = null;
            List<ProjectDataSource> otherDataSourceList = null;
            if(null!=projectDataSources && projectDataSources.size()>0){
                Map<Boolean,List<ProjectDataSource>> map2 = projectDataSources.parallelStream().collect(partitioningBy(it -> it.getIsMainDataSource()));
                List<ProjectDataSource> mainList = map2.get(true);
                otherDataSourceList = map2.get(false).parallelStream().map(it->{return ProjectDataSourceUtil.getCopy(it);}).collect(Collectors.toList());
                if(null!=mainList && mainList.size()==1){
                    mainDataSource = ProjectDataSourceUtil.getCopy(mainList.get(0));
                    if(project.getIsEncodeDataSource()){
                        String newUserName = AESEncoderUtil.AESEncode(aesEndoeRule,mainDataSource.getUsername());
                        String newPassword = AESEncoderUtil.AESEncode(aesEndoeRule,mainDataSource.getPassword());
                        if(null==newUserName||null==newPassword){
                            logger.info("加密数据源出错!");
                        }else {
                            mainDataSource.setUsername(newUserName);
                            mainDataSource.setPassword(newPassword);
                        }
                    }
                }
                if(null!=otherDataSourceList&&otherDataSourceList.size()>0){
                    String otherDataSourceNickNames = otherDataSourceList.parallelStream().map(it->it.getDataSourceNickName()).collect(Collectors.joining(","));
                    map.put("otherDataSourceNames",otherDataSourceNickNames);
                    if(project.getIsEncodeDataSource()) {
                        otherDataSourceList.parallelStream().forEach(datasource -> {
                            String newUserName = AESEncoderUtil.AESEncode(aesEndoeRule, datasource.getUsername());
                            String newPassword = AESEncoderUtil.AESEncode(aesEndoeRule, datasource.getPassword());
                            if (null == newUserName || null == newPassword) {
                                logger.info("加密数据源出错!");
                            } else {
                                datasource.setUsername(newUserName);
                                datasource.setPassword(newPassword);
                            }
                        });
                    }
                    map.put("otherDataSources",otherDataSourceList);
                }

            }
            map.put("mainDataSource",mainDataSource);
            File applicationFile = new File(dir,"application-dev.properties");
            generateFileByTemplate(applicationFile,"application-dev.ftl",map);

        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取{application-dev.ftl}模板失败");
        }
    }

    //生成permissionProperty.property文件
    private void generatePermissionPropertyFile(File dir,Project project,Module module){
        try {
            Map<String, Object> map = new HashMap<>();
            File applicationFile = new File(dir,"permission.properties");
            generateFileByTemplate(applicationFile,"jwt/permissionProperty.ftl",map);

        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取{jwt/permissionProperty.ftl}模板失败");
        }
    }
    //生成jwtFilter文件
    private void generateJwtFilterFile(File dir,Project project,Module module){
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("basePackage",project.getPackageName());
            File applicationFile = new File(dir,"JwtFilter.java");
            generateFileByTemplate(applicationFile,"jwt/JwtFilter.ftl",map);

        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取{jwt/JwtFilter.ftl}模板失败");
        }
    }


    private void generateLogbackConfigerationFile(File dir,Project project,Module module){
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("module",module);
            File applicationFile = new File(dir,"logback-spring.xml");
            generateFileByTemplate(applicationFile,"logbackXML.ftl",map);

        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取{logbackXML.ftl}模板失败");
        }


    }
    //生成实体类文件
    private void generateModelFile(File dir,Project project,Module module){
        try {
            for(Entities entity:module.getEntities()){
                Map<String,Object> map = new HashMap();
                map.put("project",project);
                map.put("entity",entity);
                map.put("module",module);
                map.put("GeneratorStringUtil",new GeneratorStringUtil());
                List<String> entityFieldNames = entity.getFields().parallelStream().map(field -> field.getName()).collect(Collectors.toList());
                if(entityFieldNames.contains("createDate")){
                    map.put("isCreateDate",true);
                }else{
                    map.put("isCreateDate",false);
                }
                if(entityFieldNames.contains("updateDate")){
                    map.put("isUpdateDate",true);
                }else{
                    map.put("isUpdateDate",false);
                }
                File entityFile = new File(dir,entity.getName()+".java");
                generateFileByTemplate(entityFile,"entity.ftl",map);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取｛entity.ftl｝模板失败");
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
        processTemplate(dir, project, module, templateName, fileSuffix);
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
        processTemplate(dir, project, module, templateName, fileSuffix);
    }
    //生成方法的入参和出参包装类
    private void generateMethodWrapperFile(File dir,Project project,Module module){
        String paramTemplateFile = "methodParamWrapper.ftl";
        String resultTemplateFile = "methodResultWrapper.ftl";
        try {
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
                    generateFileByTemplate(paramFile,paramTemplateFile,map);

                    //如果方法返回类型选择了主对象类型，则不需要包装结果返回类型,默认返回包装类型
                    if(null == method.getIsReturnObject()||(null != method.getIsReturnObject() && method.getIsReturnObject()== false)) {
                        generateFileByTemplate(resultFile,resultTemplateFile,map);
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
        try {
            for (Entities entities : module.getEntities()) {
                Map<String,Object> map = new HashMap();
                map.put("project", project);
                map.put("entity", entities);
                File modelRelationFile = new File(dir,entities.getName()+"$Relation.java");
                generateFileByTemplate(modelRelationFile,"modelRelationWrapper.ftl",map);
            }
        }catch (RuntimeException e){
            e.printStackTrace();
            logger.error("处理方法包装类失败");
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }
    //生成methodIntercept文件
    private void generateMethodInterceptFile(File dir,Project project,Module module){
        try {
            if(!dir.isDirectory()){
                dir.mkdirs();
            }
            for(Entities entity:module.getEntities()) {
                Map<String,Object> map = new HashMap();
                map.put("project", project);
                map.put("entity", entity);

                File methodInterceptFile = new File(dir,entity.getName()+"MethodIntercept.java");
                generateFileByTemplate(methodInterceptFile,"methodIntercept.ftl",map);

            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取｛ methodIntercept.ftl ｝模板失败");
        }
    }

    //生成methodIntercept文件
    private void generateMethodInterceptImplFile(File dir,Project project,Module module){
        try {
            for(Entities entity:module.getEntities()) {
                Map<String,Object> map = new HashMap();
                map.put("project", project);
                map.put("entity", entity);
                //List<String> entityFieldNames = entity.getFields().parallelStream().map(field -> field.getName()).collect(Collectors.toList());
                File methodInterceptFile = new File(dir,entity.getName()+"MethodInterceptImpl.java");
                generateFileByTemplate(methodInterceptFile,"methodInterceptImpl.ftl",map);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取｛methodInterceptImpl.ftl｝模板失败");
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
            for(Entities entity:module.getEntities()) {
                Map<String,Object> map = new HashMap();
                map.put("project", project);
                map.put("entity", entity);
                map.put("module",module);
                map.put("searchDBUtil",new SearchDBUtil());
                map.put("constructSearchMethodUtil",new ConstructSearchMethodUtil());
                map.put("generatorStringUtil",new GeneratorStringUtil());
                List<String> entityFieldNames = entity.getFields().parallelStream().map(field -> field.getName()).collect(Collectors.toList());
                if(entityFieldNames.contains("version")){
                    map.put("isVersion",true);
                }else{
                    map.put("isVersion",false);
                }
                File serviceFile = new File(dir,entity.getName()+fileSuffix);
                generateFileByTemplate(serviceFile,templateName,map);

            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取｛"+templateName+"｝模板失败");
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
        processTemplate(dir, project, module, templateName, fileSuffix);
    }
    //生成某一模块下standardEntityUtil文件
    private void generateStandardUtilFile(File dir,Project project,Module module,boolean isExtend){
        String templateName;
        String fileSuffix;
        templateName = "standardEntityUtil.ftl";
        fileSuffix = "Util.java";
        processTemplate(dir, project, module, templateName, fileSuffix);
    }

    //处理模板文件,生成service,respository,standardEntityUtil,controller
    private void processTemplate(File dir, Project project, Module module, String templateName, String fileSuffix) {
        try {
            for(Entities entity:module.getEntities()) {
                Map<String,Object> map = new HashMap();
                map.put("project", project);
                map.put("entity", entity);
                map.put("module", module);
                map.put("GeneratorStringUtil",new GeneratorStringUtil());
                map.put("searchDBUtil", new SearchDBUtil());
                List<String> entityFieldNames = entity.getFields().parallelStream().map(field -> field.getName()).collect(Collectors.toList());
                if(entityFieldNames.contains("createDate")){
                    map.put("isCreateDate",true);
                }else{
                    map.put("isCreateDate",false);
                }
                if(entityFieldNames.contains("updateDate")){
                    map.put("isUpdateDate",true);
                }else{
                    map.put("isUpdateDate",false);
                }
                if(entityFieldNames.contains("version")){
                    map.put("isVersion",true);
                }else{
                    map.put("isVersion",false);
                }
                dir.mkdirs();

                File serviceFile = new File(dir,entity.getName()+fileSuffix);

                generateFileByTemplate(serviceFile,templateName,map);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取｛"+templateName+"｝模板失败");
        }
    }

    //===============================================================================================================以下为生成文件配置====================

    //判断是否需要重新生成文件
    @Override
    public boolean checkIsNewGenerate(File file, Long projectHierachyTime, Long moduleHierachyTime){
        boolean isNewGenerate = true;
        if (!file.exists()) {
            isNewGenerate = true;
        } else {
            long fileUpdateTime = file.lastModified()-60000;
            if (projectHierachyTime >= fileUpdateTime) {
                isNewGenerate = true;
            } else {
                if (null!=moduleHierachyTime && moduleHierachyTime >= fileUpdateTime) {
                    isNewGenerate = true;
                } else {
                    isNewGenerate = false;
                }
            }
        }
        return isNewGenerate;
    }

    //根据是否需要生成最新的文件去生成文件
    public void generateLastFileByTemplate(File file, String templateFileName, Map map, boolean isNewGenerate)throws Exception{
        if(file.exists()) {
            if (isNewGenerate) {
                logger.info(new StringBuilder("【").append(file.getName()).append("】").append(" 文件更新,重新生成!").toString());
                generateFileByTemplate(file, templateFileName, map);
            }else{
                logger.info(new StringBuilder("【").append(file.getName()).append("】").append(" 已经生成,并且不需要更新!").toString());
            }
        }else{
            logger.info(new StringBuilder("【").append(file.getName()).append("】").append(" 文件不存在,生成!").toString());
            generateFileByTemplate(file, templateFileName, map);
        }
    }

    //根据提供的模版生成对应的文件
    private void generateFileByTemplate(File file,String templateFileName,Map map) {
        try(Writer writer = new OutputStreamWriter(new FileOutputStream(file),"utf-8");) {
            Template template = configuration.getTemplate(templateFileName);
            template.process(map, writer);
            writer.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
