package com.rayleigh.batman.controller;

import com.rayleigh.batman.model.Entities;
import com.rayleigh.batman.model.Module;
import com.rayleigh.batman.model.Project;
import com.rayleigh.batman.repository.EntitiesRepository;
import com.rayleigh.batman.service.ProjectService;
import com.rayleigh.batman.util.BuildProjectDirUtil;
import com.rayleigh.batman.util.GeneratorStringUtil;
import com.rayleigh.core.async.AsyncServiceUtil;
import com.rayleigh.core.dynamicDataSource.TargetDataSource;
import com.rayleigh.core.controller.BaseController;
import com.rayleigh.core.model.ResultWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

@Controller
@RequestMapping("/testCtl")
public class TestController extends BaseController{
    private Logger logger = LoggerFactory.getLogger(TestController.class);
    @Autowired
    private ProjectService projectService;
    @Value("server.generator-path")
    private String basePath;
    @Autowired
    private Configuration configuration;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AsyncServiceUtil asyncServiceUtil;
    @Autowired
    private EntitiesRepository entitiesRepository;

    @GetMapping("/testDynamicDataSource")
    @ResponseBody
    @TargetDataSource("ds1")
    public ResultWrapper test(){
        List<Map<String,Object>> list = jdbcTemplate.queryForList("select * from course_student");
        return getSuccessResult(list);
    }

    @GetMapping("/generateProjectDir")
    @ResponseBody
    public void testMakeProject(HttpServletRequest request){
        logger.info("realPath:"+request.getServletContext().getRealPath("/"));
        Project project = projectService.getAll().get(0);
        logger.info("projectName:"+project.getName());
        project.getModules().stream().forEach(module -> logger.info("moduleName:"+module.getName()));
        BuildProjectDirUtil.createDirForProject(request.getServletContext().getRealPath("/")+basePath,project);
    }

    @GetMapping("/pom")
    @ResponseBody
    public void testGeneratePom(HttpServletRequest request) throws Exception {

        freemarker.template.Template template =configuration.getTemplate("modulePom.ftl");

        Map<String, String> map = new HashMap<>();
        map.put("projectName","testProject");
        map.put("moduleName","testModel");
        //map.put("basePackage","com.test");
        map.put("springBootVersion","1.5.2.RELEASE");

        templateProcess(map, template);

    }

    @GetMapping("/application")
    @ResponseBody
    public void testApplication()throws Exception{
        Template template = configuration.getTemplate("application.ftl");
        Map<String, String> map = new HashMap<>();
        String port = new Random().nextInt(9000)+8080+"";
        map.put("moduleName","testModel");
        map.put("serverPort",port);

        templateProcess(map, template);
    }

    @GetMapping("/testEntity")
    @ResponseBody
    public void testEntity()throws Exception{
       // configuration.setClassForTemplateLoading(GeneratorStringUtil.class,"com.rayleigh.generator.util");
        Template template = configuration.getTemplate("entity.ftl");

        Map map = new HashMap();
        Project project = projectService.getAll().get(0);
        Module module = project.getModules().get(0);
        //Entities entities = module.getEntities().get(0);

        logger.info("projectName:"+project.getName());
        logger.info("moduleName:"+module.getName());
        //logger.info("entitiesName:"+entities.getName());

        //entities.getFields().parallelStream().forEach(field -> logger.info("field:"+field.getName()));
        for(Entities entities1:module.getEntities()){
            System.out.println("----------------------------------------------------------------------------------------");
            map.put("project",project);
            map.put("entity",entities1);
            map.put("GeneratorStringUtil",new GeneratorStringUtil());
            templateProcess(map, template);
        }
    }

    @GetMapping("/testRepository")
    @ResponseBody
    public void testRepository() throws Exception{
        Map map = new HashMap();
        Project project = projectService.getAll().get(0);
        Entities entities = project.getEntities().get(0);
        map.put("project",project);
        map.put("entity",entities);

        Template template = configuration.getTemplate("repository.ftl");

        templateProcess(map, template);
    }

    @GetMapping("/testExtendRepository")
    @ResponseBody
    public void testExtendRepository() throws Exception{
        Map map = new HashMap();
        Project project = projectService.getAll().get(0);
        Entities entities = project.getEntities().get(0);
        map.put("project",project);
        map.put("entity",entities);

        Template template = configuration.getTemplate("extendRepository.ftl");

        templateProcess(map, template);
    }


    @GetMapping("/testService")
    @ResponseBody
    public void testService() throws Exception{
        Map map = new HashMap();
        Project project = projectService.getAll().get(0);
        Entities entities = project.getEntities().get(0);
        map.put("project",project);
        map.put("entity",entities);

        Template template = configuration.getTemplate("service.ftl");

        templateProcess(map, template);
    }

    @GetMapping("/testServiceImp")
    @ResponseBody
    public void testServiceImp() throws Exception{
        Map map = new HashMap();
        Project project = projectService.getAll().get(0);
        Entities entities = project.getEntities().get(0);
        map.put("project",project);
        map.put("entity",entities);

        Template template = configuration.getTemplate("serviceImpl.ftl");

        templateProcess(map, template);
    }

    @GetMapping("/testExtendService")
    @ResponseBody
    public void testExtendService() throws Exception{
        Map map = new HashMap();
        Project project = projectService.getAll().get(0);
        Entities entities = project.getEntities().get(0);
        map.put("project",project);
        map.put("entity",entities);

        Template template = configuration.getTemplate("extendService.ftl");

        templateProcess(map, template);
    }

    private void templateProcess(Map map, Template template) throws IOException, TemplateException {
        try(Writer writer = new OutputStreamWriter(System.out,"utf-8");) {
            template.process(map, writer);
            writer.flush();
        }
    }

    @GetMapping("/testExtendServiceImp")
    @ResponseBody
    public void testExtendServiceImp() throws Exception{
        Map map = new HashMap();
        Project project = projectService.getAll().get(0);
        Entities entities = project.getEntities().get(0);
        map.put("project",project);
        map.put("entity",entities);

        Template template = configuration.getTemplate("extendServiceImp.ftl");

        templateProcess(map, template);
    }

    @GetMapping("/testGetJsonDate")
    @ResponseBody
    public Project tess(){
        Project project = projectService.getAll().get(0);
        return project;
    }

    @GetMapping("/testAsyncService")
    @ResponseBody
    public String tess3(){
        logger.info("开始testAsyncService开始");
        asyncServiceUtil.doAsync(()->{
            logger.info("开始异步处理任务");
            try{
                Thread.sleep(5000L);
            }catch (Exception e){
                e.printStackTrace();
            }
            logger.info("结束异步处理任务!");
            return new AsyncResult("返回结果");
        });
        logger.info("主线程处理testAsyncService结束");

        return "success";
    }

    @GetMapping("/testLog")
    @ResponseBody
    public String test55(){
        logger.info("info Logger");
        logger.debug("debug Logger");
        logger.warn("warn Logger");
        logger.error("error logger");
        return "success";
    }

    @GetMapping("/testComand")
    @ResponseBody
    public String test333()throws Exception{
        Map<String, String> map = System.getenv();
        for(Iterator<String> itr = map.keySet().iterator();itr.hasNext();){
            String key = itr.next();
            System.out.println(key + "=" + map.get(key));
        }
        String os2=System.getProperty("os.name");
        logger.info(os2);
        String m2= map.get("M2_HOME");
        logger.info("m2:"+m2);
        ProcessBuilder processBuilder;
        if(os2.startsWith("Windows")) {
            processBuilder = new ProcessBuilder(m2 + "/bin/mvn.cmd", "package");
        }else{
            processBuilder = new ProcessBuilder(m2 + "/bin/mvn", "package");
        }
        //processBuilder.directory(new File("C:\\MYWORK\\maven\\apache-maven-3.3.9\\bin"));
        Process process = processBuilder.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    @GetMapping("/getMaxFiled/{entityId}")
    @ResponseBody
    public ResultWrapper getMaxField(@PathVariable("entityId") String entityId){
        Date date = entitiesRepository.getMaxFieldUpdateDateByEntityId(entityId);
        return getSuccessResult(date);
    }

    public static void main(String[] args)throws Exception {
        Map<String, String> map = System.getenv();
        for(Iterator<String> itr = map.keySet().iterator();itr.hasNext();){
            String key = itr.next();
            System.out.println(key + "=" + map.get(key));
        }
        String os2=System.getProperty("os.name");
       // logger.info(os2);
        String m2= map.get("M2_HOME");
      //  logger.info("m2:"+m2);
        ProcessBuilder processBuilder;
        if(os2.startsWith("Windows")) {
            processBuilder = new ProcessBuilder(m2 + "/bin/mvn.cmd", "package");
        }else{
            processBuilder = new ProcessBuilder(m2 + "/bin/mvn", "package");
        }
        //processBuilder.directory(new File("C:\\MYWORK\\maven\\apache-maven-3.3.9\\bin"));
        Process process = processBuilder.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line);
        }
        System.out.println(stringBuilder.toString());
        //Runtime.getRuntime().exec("mvn");
        //ProcessBuilder processBuilder = new ProcessBuilder("mvn", "-v");

//        ProcessBuilder processBuilder = new ProcessBuilder("C:\\MYWORK\\maven\\apache-maven-3.3.9\\bin\\mvn","package");
//        //processBuilder.directory(new File("C:\\MYWORK\\maven\\apache-maven-3.3.9\\bin"));
//        Process process = processBuilder.start();
//        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
//        String line;
//        while ((line = br.readLine()) != null) {
//            System.out.println(line);
//        }


    }

}
