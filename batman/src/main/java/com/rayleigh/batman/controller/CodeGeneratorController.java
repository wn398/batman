package com.rayleigh.batman.controller;

import com.rayleigh.batman.model.*;
import com.rayleigh.batman.service.*;
import com.rayleigh.batman.util.*;
import com.rayleigh.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
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
    private SysUserService sysUserService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private EntityService entityService;
    @Autowired
    private CodeGenerateService codeGenerateService;

    @Value("${batman.aesEncode.rule}")
    private String aesEndoeRule;

    private Map<String, String> tempMap = new HashMap<>();

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
        tempMap.put("port",request.getServerPort()+"");
        tempMap.put("root",request.getRequestURI().split("/")[1]);
        //生成的每个项目根据时间生成对应目录
        String basePath = new StringBuilder("/").append(projectId).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        Project project = projectService.findOne(projectId);

        codeGenerateService.produceProjectStandard(generatorBasePath, project);

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

    //生成项目module standard部分代码并编译生成jar包
    @RequestMapping("/generateProjectStandardJar/{projectId}")
    public void generatorByProjectStandardJar(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId)  {


        String realPath = request.getServletContext().getRealPath("/");
        tempMap.put("port",request.getServerPort()+"");
        tempMap.put("root",request.getRequestURI().split("/")[1]);
        //生成的每个项目根据时间生成对应目录
        String basePath = new StringBuilder("/").append(projectId).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        Project project = projectService.findOne(projectId);
        for(Module module :project.getModules()){
            codeGenerateService.produceModuleStandardJar(generatorBasePath,module,project);
        }
    }

    //生成某个module standard部分代码并编译生成jar包
    @RequestMapping("/generateModuleStandardJar/{moduleId}")
    public void generatorModuleStandardJar(HttpServletRequest request, HttpServletResponse response, @PathVariable("moduleId") String moduleId)  {


        String realPath = request.getServletContext().getRealPath("/");
        tempMap.put("port",request.getServerPort()+"");
        tempMap.put("root",request.getRequestURI().split("/")[1]);
        Module module = moduleService.findOne(moduleId);
        Project project = module.getProject();
        //生成的每个项目生成对应目录
        String basePath = new StringBuilder("/").append(project.getId()).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        codeGenerateService.produceModuleStandardJar(generatorBasePath,module,project);

    }

    //生成某个module standard部分代码
    @RequestMapping("/generateModuleStandard/{moduleId}")
    public void generatorModuleStandard(HttpServletRequest request, HttpServletResponse response, @PathVariable("moduleId") String moduleId) {


        String realPath = request.getServletContext().getRealPath("/");
        tempMap.put("port",request.getServerPort()+"");
        tempMap.put("root",request.getRequestURI().split("/")[1]);
        Module module = moduleService.findOne(moduleId);
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

        //FileCompressUtil.compress(sourceDir,targetFile);
        try {
            String fileName = new String(new StringBuilder(module.getName()).append("Standard").append(".zip").toString().getBytes("UTF-8"), "iso-8859-1");//为了解决中文名称乱码问题
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
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    //生成某个module extend部分代码
    @RequestMapping("/generateModuleExtend/{moduleId}")
    public void generatorModuleExtend(HttpServletRequest request, HttpServletResponse response, @PathVariable("moduleId") String moduleId)  {

        String realPath = request.getServletContext().getRealPath("/");
        tempMap.put("port",request.getServerPort()+"");
        tempMap.put("root",request.getRequestURI().split("/")[1]);
        Module module = moduleService.findOne(moduleId);
        Project project = module.getProject();
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


        try {

            String fileName = new String(new StringBuilder(module.getName()).append("Extend.zip").toString().getBytes("UTF-8"), "iso-8859-1");//为了解决中文名称乱码问题
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
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    //生成项目扩展部分代码
    @RequestMapping("/generateProjectExtend/{projectId}")
    public void generatorByProjectExtend(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId) throws IOException {
        String realPath = request.getServletContext().getRealPath("/");
        tempMap.put("port",request.getServerPort()+"");
        tempMap.put("root",request.getRequestURI().split("/")[1]);
        //生成的每个项目生成对应目录
        String basePath = new StringBuilder("/").append(projectId).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        Project project = projectService.findOne(projectId);

        codeGenerateService.produceProjectExtend(generatorBasePath, project);

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

    //生成项目全部代码,标准部分代码也是生成
    @RequestMapping("/generateProject/{projectId}")
    public void generatorByProject(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId) throws IOException {
        String realPath = request.getServletContext().getRealPath("/");
        tempMap.put("port",request.getServerPort()+"");
        tempMap.put("root",request.getRequestURI().split("/")[1]);
        //生成的每个项目根据id生成对应目录
        String basePath = new StringBuilder("/").append(projectId).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        Project project = projectService.findOne(projectId);

        codeGenerateService.produceProjectAll(generatorBasePath, project,tempMap.get("port"),tempMap.get("root"));

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

    //生成项目全部代码,标准部分代码打包到仓库
    @RequestMapping("/generateProjectWithStandJar/{projectId}")
    public void generatorByProjectWithStandardJar(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId) throws IOException {
        String realPath = request.getServletContext().getRealPath("/");
        tempMap.put("port",request.getServerPort()+"");
        tempMap.put("root",request.getRequestURI().split("/")[1]);
        //生成的每个项目根据id生成对应目录
        String basePath = new StringBuilder("/").append(projectId).toString();
        String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();

        Project project = projectService.findOne(projectId);

        codeGenerateService.produceProjectAllWithStandardJar(generatorBasePath, project,tempMap.get("port"),tempMap.get("root"));

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







}
