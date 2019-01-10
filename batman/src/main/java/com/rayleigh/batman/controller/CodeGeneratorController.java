package com.rayleigh.batman.controller;

import com.rayleigh.batman.model.*;
import com.rayleigh.batman.service.*;
import com.rayleigh.batman.uiModel.ProjectListModel;
import com.rayleigh.batman.util.*;
import com.rayleigh.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Controller
@RequestMapping("/codeGeneratorCtl")
public class CodeGeneratorController extends BaseController{
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private CodeGenerateService codeGenerateService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${batman.aesEncode.rule}")
    private String aesEndoeRule;

    private Map<String, String> tempMap = new HashMap<>();

    @GetMapping("/goProjectCodeGenerator")
    public String goCodeGenerator(HttpServletRequest request, Model model2){
        String userId = (String)request.getSession().getAttribute("userId");
        String sql = "SELECT\n" +
                "  project.id as id,\n" +
                "\tproject. NAME AS NAME,\n" +
                "\tproject.description as description,\n"+
                "\tproject.package_name as packageName,"+
                "\tCOUNT (DISTINCT(\"module\". ID)) AS moduleNum,\n" +
                "\tCOUNT (DISTINCT(entity. ID)) AS entityNum,\n" +
                "\tproject.create_date AS createDate,\n" +
                "\tproject.hierachy_date as hierachyDate,\n" +
                "\tproject.\"version\" as version\n" +
                "FROM\n" +
                "\tbatman_project project\n" +
                "LEFT JOIN batman_module MODULE ON \"module\".project_id = project.\"id\"\n" +
                "LEFT JOIN batman_entity entity ON entity.project_id = project. ID\n" +
                "WHERE\n" +
                "\tproject.sysuser_id = '"+userId+"'\n" +
                "GROUP BY\n" +
                "\tproject. ID";
        logger.info(new StringBuilder("执行本地sql：").append(sql).toString());
        List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
        List<ProjectListModel> resultList = new ArrayList<>();
        list.stream().forEach(it->{
            ProjectListModel model = new ProjectListModel();
            model.setId((String)it.get("id"));
            model.setDescription((String)it.get("description"));
            model.setPackageName((String)it.get("packageName"));
            model.setCreateDate((Date)it.get("createDate"));
            model.setHierachyDate((Date)it.get("hierachyDate"));
            model.setEntityNum((Long)it.get("entityNum"));
            model.setModuleNum((Long)it.get("moduleNum"));
            model.setVersion((Long)it.get("version"));
            model.setName((String)it.get("name"));
            resultList.add(model);
        });
        model2.addAttribute("projects",resultList);

        return "/page/project-list";
    }

    /**
     * 生成整个项目base部分代码
     */
    @RequestMapping("/generateProjectStandard/{projectId}")
    public void generatorByProjectStandard(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId) throws IOException {
        String realPath = request.getServletContext().getRealPath("/");
        if(tempMap.isEmpty()) {
            tempMap.put("port", request.getServerPort() + "");
            tempMap.put("root", request.getRequestURI().split("/")[1]);
        }
        //生成的每个项目对应目录
        String basePath = new StringBuilder("/").append(projectId).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();
        Project project = projectService.findOne(projectId);
        File targetFile = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).append("base").append(".zip").toString());
        String fileName=new StringBuilder(project.getName()).append("base").append(".zip").toString();
        if(targetFile.exists()){
            boolean isGenerate = codeGenerateService.checkIsNewGenerate(targetFile,project.getUpdateDate().getTime(),null);
            if(!isGenerate){
                responseOutputFile(response,fileName,targetFile);
                return;
            }
        }

        FileSystemUtils.deleteRecursively(new File(generatorBasePath));
        codeGenerateService.produceProjectStandard(generatorBasePath, project);
        File sourceDir = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).toString());
        FileCompressUtil.compress(sourceDir,targetFile);
        responseOutputFile(response,fileName,targetFile);
    }


    /**
     * 生成项目module base部分代码并编译生成jar包
     */
    @RequestMapping("/generateProjectStandardJar/{projectId}")
    public void generatorByProjectStandardJar(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId)  {


        String realPath = request.getServletContext().getRealPath("/");
        if(tempMap.isEmpty()) {
            tempMap.put("port", request.getServerPort() + "");
            tempMap.put("root", request.getRequestURI().split("/")[1]);
        }
        //生成的每个项目根据时间生成对应目录
        String basePath = new StringBuilder("/").append(projectId).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        Project project = projectService.findOne(projectId);
        for(Module module :project.getModules()){
            codeGenerateService.produceModuleStandardJar(generatorBasePath,module,project);
        }
    }

    /**
     * 生成某个module base部分代码并编译生成jar包
     */
    @RequestMapping("/generateModuleStandardJar/{moduleId}")
    public void generatorModuleStandardJar(HttpServletRequest request, HttpServletResponse response, @PathVariable("moduleId") String moduleId)  {


        String realPath = request.getServletContext().getRealPath("/");
        if(tempMap.isEmpty()) {
            tempMap.put("port", request.getServerPort() + "");
            tempMap.put("root", request.getRequestURI().split("/")[1]);
        }
        Module module = moduleService.findOne(moduleId);
        Project project = module.getProject();
        //生成的每个项目生成对应目录
        String basePath = new StringBuilder("/").append(project.getId()).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        codeGenerateService.produceModuleStandardJar(generatorBasePath,module,project);

    }

    /**
     * 生成某个module standard部分代码
     */
    @RequestMapping("/generateModuleStandard/{moduleId}")
    public void generatorModuleStandard(HttpServletRequest request, HttpServletResponse response, @PathVariable("moduleId") String moduleId) throws IOException {


        String realPath = request.getServletContext().getRealPath("/");
        if(tempMap.isEmpty()) {
            tempMap.put("port", request.getServerPort() + "");
            tempMap.put("root", request.getRequestURI().split("/")[1]);
        }
        Module module = moduleService.findOne(moduleId);
        //生成的模块对应根目录
        String basePath = new StringBuilder("/").append(module.getId()).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        File sourceDir = new File(new StringBuilder(generatorBasePath).append("/").append(module.getProject().getName()).append("/").append(module.getName()).toString());
        File targetFile = new File(new StringBuilder(generatorBasePath).append("/").append(module.getProject().getName()).append("/").append(module.getName()).append("Standard").append(".zip").toString());
        String fileName = new StringBuilder(module.getName()).append("Base").append(".zip").toString();
        if(targetFile.exists()) {
            boolean isGenerate = codeGenerateService.checkIsNewGenerate(targetFile, module.getProject().getUpdateDate().getTime(), module.getUpdateDate().getTime());
            if (!isGenerate) {
                logger.info(new StringBuilder("文件【").append(module.getName()).append("Base.zip】存在，不需要更新生成！").toString());
                responseOutputFile(response, fileName, targetFile);
                return;
            }
        }
        logger.info(new StringBuilder("文件【").append(module.getName()).append("Base.zip】不存在，生成！").toString());
        FileSystemUtils.deleteRecursively(new File(generatorBasePath));
        codeGenerateService.produceModuleStandard(generatorBasePath,moduleId);
        FileCompressUtil.compress(sourceDir, targetFile);
        responseOutputFile(response, fileName, targetFile);
    }


    /**
     * 生成某个module extend部分代码
     */
    @RequestMapping("/generateModuleExtend/{moduleId}")
    public void generatorModuleExtend(HttpServletRequest request, HttpServletResponse response, @PathVariable("moduleId") String moduleId) throws IOException {

        String realPath = request.getServletContext().getRealPath("/");
        if(tempMap.isEmpty()) {
            tempMap.put("port", request.getServerPort() + "");
            tempMap.put("root", request.getRequestURI().split("/")[1]);
        }
        Module module = moduleService.findOne(moduleId);
        Project project = module.getProject();
        //生成的每个项目生成对应目录
        String basePath = new StringBuilder("/").append(module.getProject().getId()).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        File sourceDir = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).append("/").append(module.getName()).toString());
        File targetFile = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).append("/").append(module.getName()).append("Extend.zip").toString());
        String fileName = new StringBuilder(module.getName()).append("Extend.zip").toString();
        if(targetFile.exists()){
            boolean isGenerate = codeGenerateService.checkIsNewGenerate(targetFile,project.getUpdateDate().getTime(),module.getUpdateDate().getTime());
            if(!isGenerate){
                logger.info(new StringBuilder("文件【").append(module.getName()).append("Extend.zip】存在，不需要更新生成！").toString());
                responseOutputFile(response,fileName,targetFile);
                return;
            }
        }
        logger.info(new StringBuilder("文件【").append(module.getName()).append("Extend.zip】不存在，生成！").toString());
        FileSystemUtils.deleteRecursively(new File(generatorBasePath));
        codeGenerateService.produceModuleExtendAllFiles(generatorBasePath,module.getProject(),module);
        FileCompressUtil.compress(sourceDir, targetFile);
        responseOutputFile(response,fileName,targetFile);





    }

    /**
     * 生成项目扩展部分代码
     */
    @RequestMapping("/generateProjectExtend/{projectId}")
    public void generatorByProjectExtend(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId) throws IOException {
        String realPath = request.getServletContext().getRealPath("/");
        if(tempMap.isEmpty()) {
            tempMap.put("port", request.getServerPort() + "");
            tempMap.put("root", request.getRequestURI().split("/")[1]);
        }
        //生成的每个项目生成对应目录
        String basePath = new StringBuilder("/").append(projectId).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        Project project = projectService.findOne(projectId);

        File sourceDir = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).toString());
        File targetFile = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).append("extend").append(".zip").toString());
        String fileName=new StringBuilder(project.getName()).append("extend").append(".zip").toString();
        //如果已经存在并且不需要更新，则直接返回
        if(targetFile.exists()){
            boolean isGenerate = codeGenerateService.checkIsNewGenerate(targetFile,project.getUpdateDate().getTime(),null);
            if(!isGenerate){
                logger.info(new StringBuilder("文件 【").append(fileName).append("】 已经存在，并且不需要重新生成!").toString());
                responseOutputFile(response,fileName,targetFile);
                return;
            }
        }
        logger.info(new StringBuilder("文件【").append(project.getName()).append("Extend.zip】不存在，生成！").toString());
        FileSystemUtils.deleteRecursively(new File(generatorBasePath));
        codeGenerateService.produceProjectExtend(generatorBasePath, project);
        FileCompressUtil.compress(sourceDir,targetFile);
        responseOutputFile(response,fileName,targetFile);
    }

    /**
     * 生成项目全部代码,标准部分代码也是生成
     */
    @RequestMapping("/generateProject/{projectId}")
    public void generatorByProject(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId) throws IOException {
        String realPath = request.getServletContext().getRealPath("/");
        tempMap.put("port",request.getServerPort()+"");
        tempMap.put("root",request.getRequestURI().split("/")[1]);
        //生成的每个项目根据id生成对应目录
        String basePath = new StringBuilder("/").append(projectId).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        Project project = projectService.findOne(projectId);

        File sourceDir = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).toString());
        File targetFile = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).append(".zip").toString());

        //如果已经存在并且不需要更新，则直接返回
        if(targetFile.exists()){
            boolean isGenerate = codeGenerateService.checkIsNewGenerate(targetFile,project.getUpdateDate().getTime(),null);
            if(!isGenerate){
                String fileName=new StringBuilder(project.getName()).append("base").append(".zip").toString();
                logger.info(new StringBuilder("文件 【").append(fileName).append("】 已经存在，并且不需要重新生成!").toString());
                responseOutputFile(response,fileName,targetFile);
                return;
            }
        }
        //initProject(project);
        codeGenerateService.produceProjectAll(generatorBasePath, project,tempMap.get("port"),tempMap.get("root"));
        FileCompressUtil.compress(sourceDir,targetFile);
        String fileName = new StringBuilder(project.getName()).append(".zip").toString();
        responseOutputFile(response,fileName,targetFile);
    }


    /**
     *  生成项目全部代码,标准部分代码打包到仓库
     */
    @RequestMapping("/generateProjectWithStandJar/{projectId}")
    public void generatorByProjectWithStandardJar(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId) throws IOException {
        String realPath = request.getServletContext().getRealPath("/");
        tempMap.put("port",request.getServerPort()+"");
        tempMap.put("root",request.getRequestURI().split("/")[1]);
        //生成的每个项目根据id生成对应目录
        String basePath = new StringBuilder("/").append(projectId).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        Project project = projectService.findOne(projectId);

       // initProject(project);

        File sourceDir = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).toString());
        File targetFile = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).append(".zip").toString());
        //如果已经存在并且不需要更新，则直接返回
        if(targetFile.exists()){
            boolean isGenerate = codeGenerateService.checkIsNewGenerate(targetFile,project.getUpdateDate().getTime(),null);
            if(!isGenerate){
                String fileName=new StringBuilder(project.getName()).append("base").append(".zip").toString();
                logger.info(new StringBuilder("文件 【").append(fileName).append("】 已经存在，并且不需要重新生成!").toString());
                responseOutputFile(response,fileName,targetFile);
                return;
            }
        }

        codeGenerateService.produceProjectAllWithStandardJar(generatorBasePath, project,tempMap.get("port"),tempMap.get("root"));
        FileCompressUtil.compress(sourceDir,targetFile);
        String fileName = new StringBuilder(project.getName()).append(".zip").toString();
        responseOutputFile(response,fileName,targetFile);
    }

    private void responseOutputFile(HttpServletResponse response, String fileName, File targetFile) throws IOException {
        //为了解决中文名称乱码问题
        fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
        response.reset();
        response.addHeader("Content-Length", "" + targetFile.length());
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        try (OutputStream outputStream = response.getOutputStream(); InputStream in = new FileInputStream(targetFile);) {

            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf, 0, 1024)) != -1) {
                outputStream.write(buf, 0, len);
            }
        }
    }







}
