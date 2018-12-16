package com.rayleigh.batman.controller;

import com.rayleigh.batman.model.*;
import com.rayleigh.batman.service.*;
import com.rayleigh.batman.uiModel.ProjectListModel;
import com.rayleigh.batman.util.*;
import com.rayleigh.core.controller.BaseController;
import org.apache.http.client.utils.DateUtils;
import org.hibernate.cache.ehcache.internal.HibernateEhcacheUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/codeGeneratorCtl")
public class CodeGeneratorController extends BaseController{
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private EntityService entityService;
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
     * 生成整个项目standard部分代码
     */
    @RequestMapping("/generateProjectStandard/{projectId}")
    public void generatorByProjectStandard(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId) throws IOException {
        String realPath = request.getServletContext().getRealPath("/");
        tempMap.put("port",request.getServerPort()+"");
        tempMap.put("root",request.getRequestURI().split("/")[1]);
        //生成的每个项目根据时间生成对应目录
        String basePath = new StringBuilder("/").append(projectId).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        Project project = projectService.findOne(projectId);

        project = initProject(project);

        codeGenerateService.produceProjectStandard(generatorBasePath, project);

        File sourceDir = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).toString());
        File targetFile = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).append("standard").append(".zip").toString());
        FileCompressUtil.compress(sourceDir,targetFile);

        String fileName=new StringBuilder(project.getName()).append("standard").append(".zip").toString();
        responseOutputFile(response,fileName,targetFile);
    }


    /**
     * 生成项目module standard部分代码并编译生成jar包
     */
    @RequestMapping("/generateProjectStandardJar/{projectId}")
    public void generatorByProjectStandardJar(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId)  {


        String realPath = request.getServletContext().getRealPath("/");
        tempMap.put("port",request.getServerPort()+"");
        tempMap.put("root",request.getRequestURI().split("/")[1]);
        //生成的每个项目根据时间生成对应目录
        String basePath = new StringBuilder("/").append(projectId).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        Project project = projectService.findOne(projectId);

        initProject(project);

        for(Module module :project.getModules()){
            codeGenerateService.produceModuleStandardJar(generatorBasePath,module,project);
        }
    }

    /**
     * 生成某个module standard部分代码并编译生成jar包
     */
    @RequestMapping("/generateModuleStandardJar/{moduleId}")
    public void generatorModuleStandardJar(HttpServletRequest request, HttpServletResponse response, @PathVariable("moduleId") String moduleId)  {


        String realPath = request.getServletContext().getRealPath("/");
        tempMap.put("port",request.getServerPort()+"");
        tempMap.put("root",request.getRequestURI().split("/")[1]);
        Module module = moduleService.findOne(moduleId);
        Project project = module.getProject();

        initModule(module);
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
        tempMap.put("port",request.getServerPort()+"");
        tempMap.put("root",request.getRequestURI().split("/")[1]);
        Module module = moduleService.findOne(moduleId);

        initModule(module);
        //生成的模块对应根目录
        String basePath = new StringBuilder("/").append(module.getId()).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        File sourceDir = new File(new StringBuilder(generatorBasePath).append("/").append(module.getProject().getName()).append("/").append(module.getName()).toString());
        File targetFile = new File(new StringBuilder(generatorBasePath).append("/").append(module.getProject().getName()).append("/").append(module.getName()).append("Standard").append(".zip").toString());

        Date maxModuleDate = moduleService.getMaxModuleHierachyDate(moduleId);
        if(targetFile.exists()){
            boolean isGenerate = codeGenerateService.checkIsNewGenerate(targetFile,module.getProject().getHierachyDate().getTime(),module.getUpdateDate().getTime(),maxModuleDate.getTime());
            if(isGenerate){
                logger.info(new StringBuilder("文件【").append(module.getName()).append("Standard.zip】存在，需要更新生成！").toString());
                codeGenerateService.produceModuleStandard(generatorBasePath,moduleId);
                FileCompressUtil.compress(sourceDir, targetFile);
            }else{
                logger.info(new StringBuilder("文件【").append(module.getName()).append("Standard.zip】存在，不需要更新生成！").toString());
            }
        }else{
            logger.info(new StringBuilder("文件【").append(module.getName()).append("Standard.zip】不存在，生成！").toString());
            codeGenerateService.produceModuleStandard(generatorBasePath,moduleId);
            FileCompressUtil.compress(sourceDir, targetFile);
        }

            String fileName = new StringBuilder(module.getName()).append("Standard").append(".zip").toString();
            responseOutputFile(response,fileName,targetFile);


    }


    /**
     * 生成某个module extend部分代码
     */
    @RequestMapping("/generateModuleExtend/{moduleId}")
    public void generatorModuleExtend(HttpServletRequest request, HttpServletResponse response, @PathVariable("moduleId") String moduleId) throws IOException {

        String realPath = request.getServletContext().getRealPath("/");
        tempMap.put("port",request.getServerPort()+"");
        tempMap.put("root",request.getRequestURI().split("/")[1]);
        Module module = moduleService.findOne(moduleId);
        Project project = module.getProject();

        initModule(module);

        //生成的每个项目生成对应目录
        String basePath = new StringBuilder("/").append(module.getProject().getId()).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        File sourceDir = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).append("/").append(module.getName()).toString());
        File targetFile = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).append("/").append(module.getName()).append("Extend.zip").toString());
        Date maxModuleDate = moduleService.getMaxModuleHierachyDate(moduleId);

        if(targetFile.exists()){
            boolean isGenerate = codeGenerateService.checkIsNewGenerate(targetFile,project.getHierachyDate().getTime(),module.getUpdateDate().getTime(),maxModuleDate.getTime());
            if(isGenerate){
                logger.info(new StringBuilder("文件【").append(module.getName()).append("Extend.zip】存在，需要更新生成！").toString());
                codeGenerateService.produceModuleExtendAllFiles(generatorBasePath,module.getProject(),module);
                FileCompressUtil.compress(sourceDir, targetFile);
            }else{
                logger.info(new StringBuilder("文件【").append(module.getName()).append("Extend.zip】存在，不需要更新生成！").toString());
            }
        }else{
            logger.info(new StringBuilder("文件【").append(module.getName()).append("Extend.zip】不存在，生成！").toString());
            codeGenerateService.produceModuleExtendAllFiles(generatorBasePath,module.getProject(),module);
            FileCompressUtil.compress(sourceDir, targetFile);
        }


        String fileName = new StringBuilder(module.getName()).append("Extend.zip").toString();
        responseOutputFile(response,fileName,targetFile);


    }

    /**
     * 生成项目扩展部分代码
     */
    @RequestMapping("/generateProjectExtend/{projectId}")
    public void generatorByProjectExtend(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId) throws IOException {
        String realPath = request.getServletContext().getRealPath("/");
        tempMap.put("port",request.getServerPort()+"");
        tempMap.put("root",request.getRequestURI().split("/")[1]);
        //生成的每个项目生成对应目录
        String basePath = new StringBuilder("/").append(projectId).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        Project project = projectService.findOne(projectId);

        initProject(project);

        codeGenerateService.produceProjectExtend(generatorBasePath, project);

        File sourceDir = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).toString());
        File targetFile = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).append("extend").append(".zip").toString());
        FileCompressUtil.compress(sourceDir,targetFile);

        String fileName=new StringBuilder(project.getName()).append("extend").append(".zip").toString();
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

        initProject(project);
        codeGenerateService.produceProjectAll(generatorBasePath, project,tempMap.get("port"),tempMap.get("root"));

        File sourceDir = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).toString());
        File targetFile = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).append(".zip").toString());
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

        initProject(project);

        codeGenerateService.produceProjectAllWithStandardJar(generatorBasePath, project,tempMap.get("port"),tempMap.get("root"));

        File sourceDir = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).toString());
        File targetFile = new File(new StringBuilder(generatorBasePath).append("/").append(project.getName()).append(".zip").toString());
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

    /**
     * @Description
     * @param project
     * @return
     */
    private Project initProject(Project project) {
            project.getModules().stream().forEach(it -> {
                initModule(it);
            });
            return project;
    }

    /**
     *
     * @param module
     * @return
     */
    private Module initModule(Module module){
            module.getEntities().stream().forEach(it2 -> {
                        it2.getMethods().stream().forEach(it21 -> {
                            it21.getConditionList().size();
                            it21.getSearchResults().size();
                        });
                        it2.getMainFieldRelationShips().size();
                        it2.getMainEntityRelationShips().size();
                        it2.getFields().size();
                    }

            );
            return module;
        }

}
