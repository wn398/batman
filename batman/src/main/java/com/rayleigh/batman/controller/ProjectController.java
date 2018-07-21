package com.rayleigh.batman.controller;

import com.rayleigh.batman.model.Project;
import com.rayleigh.batman.model.ProjectDataSource;
import com.rayleigh.batman.model.SysUser;
import com.rayleigh.batman.service.ProjectService;
import com.rayleigh.batman.service.SysUserService;
import com.rayleigh.core.controller.BaseController;
import com.rayleigh.core.enums.ResultStatus;
import com.rayleigh.core.model.DataTablesPageModel;
import com.rayleigh.core.model.ResultWrapper;
import com.rayleigh.core.service.BaseService;
import com.rayleigh.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wangn20 on 2017/6/13.
 */
@Controller
@RequestMapping("/projectCtl")
public class ProjectController extends BaseController{
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SysUserService sysUserService;

    @GetMapping(value = "/goAllEntity/{projectId}")
    public String goAllEntity(@PathVariable("projectId") String projectId,Model model){
        Project project = projectService.findOne(projectId);
        model.addAttribute("project",project);
        return "/page/project-all-entities";
    }

    @PostMapping(value = "/doAdd")
    @ResponseBody
    public ResultWrapper doAdd(@Valid @RequestBody Project project,HttpServletRequest request){
        //必须至少含有一个模块
        if(project.getModules().size()==0){
            return getFailureResultAndInfo(null,"请至少增加一个模块!");
        }
        //检查model名字不能重复，并且不能等于projectName
        List<String> modelNames = project.getModules().parallelStream().map(module -> module.getName()).collect(Collectors.toList());
        Set<String> modelNameSet = new HashSet<>();
        for(String name:modelNames){
            if(StringUtil.isEmpty(name)){
                return getFailureResultAndInfo(null,"模块名不能为空!");
            }else{
                if(name.equalsIgnoreCase(project.getName())){
                    return getFailureResultAndInfo(null,"模块名和项目名不能一样!");
                }
                modelNameSet.add(name);
            }
        }
        if(modelNames.size() !=modelNameSet.size()){
            return getFailureResultAndInfo(null,"模块名不能重复");
        }

//        if(project.getProjectDataSources().size()==0){
//            return getFailureResultAndInfo(null,"请至少增加一个主数据源!");
//        }
        List<ProjectDataSource> list = project.getProjectDataSources();
        if(null!=list && list.size() >0) {
            Set<String> dataSourceName = new HashSet<>();
            for (ProjectDataSource pds : list) {
                if (StringUtil.isEmpty(pds.getDataBaseName()) || StringUtil.isEmpty(pds.getDataSourceNickName()) || null == pds.getPort() || StringUtil.isEmpty(pds.getHostName()) || StringUtil.isEmpty(pds.getUsername())) {
                    return getFailureResultAndInfo(null, "数据源别名,数据库名称，端口号，机器名,用户名不能为空!");
                }
                dataSourceName.add(pds.getDataSourceNickName());
            }
            if (list.size() != dataSourceName.size()) {
                return getFailureResultAndInfo(null, "数据源别名不能重复!");
            }

            List<ProjectDataSource> mainProjectDataSource = list.parallelStream().filter(projectDataSource -> projectDataSource.getIsMainDataSource() == true).collect(Collectors.toList());
            if (mainProjectDataSource.size() != 1) {
                return getFailureResultAndInfo(null, "至少设置一个主数据源，主数据源只有一个!");
            }
        }
        String userId = (String)request.getSession().getAttribute("userId");
        SysUser sysUser = sysUserService.findOne(userId);
        project.setSysUser(sysUser);
        project.setHierachyDate(new Date());
        project = projectService.save(project);
        preventCirculation(project);
        return getSuccessResult(project);
    }
    //获取所有模块名字
    @RequestMapping(value = "/getModelNames/{projectId}")
    public void getModelNames(@PathVariable("projectId") String projectId, HttpServletResponse httpResponse)throws Exception{
        Project project = projectService.findOne(projectId);
        String modelNames = project.getModules().parallelStream().map(module -> module.getName()).collect(Collectors.toList()).parallelStream().collect(Collectors.joining(":"));
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json; charset=utf-8");
        httpResponse.getWriter().write(modelNames);
        return;
    }

    @DeleteMapping(value = "/doDelete")
    @ResponseBody
    public ResultWrapper delete(@RequestBody Project project){
        try {
            projectService.deleteOne(project);
            return getSuccessResult(ResultStatus.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            return getFailureResult(ResultStatus.FAILURE);
        }
    }

    @RequestMapping(value = "/goUpdate")
    public String goUpdate(Model model, HttpServletRequest request){
        String id = request.getParameter("id");
        Project project = projectService.findOne(id);
        model.addAttribute("project",project);
        return "/page/project-update";
    }

    @RequestMapping(value = "/showAllProjectNames")
    public String showAllProjectNames(Model model, HttpServletRequest request){
        String userId = (String) request.getSession().getAttribute("userId");
        SysUser sysUser = sysUserService.findOne(userId);
        model.addAttribute("projects",sysUser.getProjects());
        return "/page/project-entities";
    }
////    到服务页面
//    @RequestMapping(value = "/showAllProjectNamesForMethod")
//    public String showAllProjectNamesForMethod(Model model){
//        model.addAttribute("projects",projectService.getAll());
//        return "/page/project-entities-for-method";
//    }

    @RequestMapping(value = "/getAll")
    @ResponseBody
    public ResultWrapper getAll(HttpServletRequest request){
        String userId = (String)request.getSession().getAttribute("userId");
        List<Project> projects= projectService.findByUserId(userId);
        return getSuccessResult(projects);
    }

    @PostMapping(value = "/partUpdate")
    @ResponseBody
    public ResultWrapper partUpdate(@Valid @RequestBody Project project,HttpServletRequest request){
        if(null!=project&&!StringUtil.isEmpty(project.getId())){
            //必须至少含有一个模块
            if(project.getModules().size()==0){
                return getFailureResultAndInfo(null,"请至少增加一个模块!");
            }
            //检查model名字不能重复，并且不能等于projectName
            List<String> modelNames = project.getModules().parallelStream().map(module -> module.getName()).collect(Collectors.toList());
            Set<String> modelNameSet = new HashSet<>();
            for(String name:modelNames){
                if(StringUtil.isEmpty(name)){
                    return getFailureResultAndInfo(null,"模块名不能为空!");
                }else{
                    if(name.equalsIgnoreCase(project.getName())){
                        return getFailureResultAndInfo(null,"模块名和项目名不能一样!");
                    }
                    modelNameSet.add(name);
                }
            }
            if(modelNames.size() !=modelNameSet.size()){
                return getFailureResultAndInfo(null,"模块名不能重复");
            }

            List<ProjectDataSource> list = project.getProjectDataSources();
            if(list.size()>0) {
                Set<String> dataSourceName = new HashSet<>();
                for (ProjectDataSource pds : list) {
                    if (StringUtil.isEmpty(pds.getDataBaseName()) || StringUtil.isEmpty(pds.getDataSourceNickName()) || null == pds.getPort() || StringUtil.isEmpty(pds.getHostName()) || StringUtil.isEmpty(pds.getUsername()) || StringUtil.isEmpty(pds.getPassword())) {
                        return getFailureResultAndInfo(null, "数据源别名,数据库名称，端口号，机器名不能为空,用户名,密码不能为空!");
                    }
                    dataSourceName.add(pds.getDataSourceNickName());
                }
                if (list.size() != dataSourceName.size()) {
                    return getFailureResultAndInfo(null, "数据源别名不能重复!");
                }

                List<ProjectDataSource> mainProjectDataSource = list.parallelStream().filter(projectDataSource -> projectDataSource.getIsMainDataSource() == true).collect(Collectors.toList());
                if (mainProjectDataSource.size() != 1) {
                    return getFailureResultAndInfo(null, "至少设置一个主数据源，主数据源只有一个!");
                }
            }

            String userId = (String)request.getSession().getAttribute("userId");
            SysUser sysUser = new SysUser();
            sysUser.setId(userId);
            project.setSysUser(sysUser);
            project.setHierachyDate(new Date());
            Project project1 =projectService.partUpdate(project);
            project1 = preventCirculation(project1);
            return getSuccessResult(project1);
        }else {
            return getFailureResultAndInfo(null,"工程id不能为空");
        }
    }
//    @PostMapping(value = "/doUpdate")
//    @ResponseBody
//    public ResultWrapper update(@RequestBody Project project){
//        if(null!=project&&!StringUtil.isEmpty(project.getId())) {
//                Project project1 = preventCirculation(project);
//            return getSuccessResult(project1);
//        }else{
//            return getFailureResultAndInfo(project,"工程id不能为空");
//        }
//    }

    @PostMapping(value = "/getByPage")
    @ResponseBody
    public DataTablesPageModel getByPage(DataTablesPageModel pageModel, Project project){
        int start = pageModel.getStart();
        int pageSize = pageModel.getPageSize();
        int pageStart = start/pageSize;
        Page page = projectService.getPageAll(pageStart,pageSize);
        pageModel.setPageSize(page.getSize());
        pageModel.setStart(page.getNumberOfElements());
        pageModel.setRecordsTotal(page.getTotalElements());
        pageModel.setRecordsFiltered(page.getTotalElements());
        pageModel.setData(page.getContent());
        return pageModel;
    }


    @RequestMapping("/goEntitiesList/{id}")
    public String goEntityList(@PathVariable String id,Model model){
        if(!StringUtil.isEmpty(id)) {
            Project project2 = projectService.findOne(id);
            model.addAttribute("project", project2);
            return "/page/entities-list";
        }else{
            return "error";
        }
    }

//    @RequestMapping("/goEntitiesListForMethod/{id}")
//    public String goEntityListForMethod(@PathVariable String id,Model model){
//        if(!StringUtil.isEmpty(id)) {
//            Project project2 = projectService.findOne(id);
//            model.addAttribute("project", project2);
//            return "/page/entities-list-method";
//        }else{
//            return "error";
//        }
//    }

    @RequestMapping("/goAddEntity/{id}")
    public String goAddEntity(@PathVariable String id,Model model){
        if(!StringUtil.isEmpty(id)) {
            Project project2 = projectService.findOne(id);
            List<ProjectDataSource> mainProjectDataSourceList = project2.getProjectDataSources().parallelStream().filter(projectDataSource -> projectDataSource.getIsMainDataSource()==true).collect(Collectors.toList());
            ProjectDataSource mainProjectDataSource = null;
            if(null!=mainProjectDataSourceList&&mainProjectDataSourceList.size()>0){
                mainProjectDataSource = mainProjectDataSourceList.get(0);
                mainProjectDataSource.setDataSourceNickName(mainProjectDataSource.getDataSourceNickName()+"(主数据源)");
            }
            List<ProjectDataSource> otherProjectDataSource = null;
            if(null!=project2.getProjectDataSources()&&project2.getProjectDataSources().size()>0){
                otherProjectDataSource = project2.getProjectDataSources().parallelStream().filter(projectDataSource -> projectDataSource.getIsMainDataSource()==false).collect(Collectors.toList());
            }
            model.addAttribute("mainDataSource",mainProjectDataSource);
            model.addAttribute("otherDataSource",otherProjectDataSource);
            model.addAttribute("project", project2);
            return "/page/entities-add";
        }else{
            return "error";
        }
    }

    //防止循环检测
    private Project preventCirculation( Project project) {
        if(null != project.getModules()&&project.getModules().size()>0){
            project.getModules().parallelStream().forEach(module -> module.setProject(null));
        }
        if(null != project.getEntities()&&project.getEntities().size()>0){
            project.getEntities().parallelStream().forEach(entities -> entities.setProject(null));
        }
        if(null != project.getProjectDataSources()&&project.getProjectDataSources().size()>0){
            project.getProjectDataSources().parallelStream().forEach(projectDataSource -> projectDataSource.setProject(null));
        }
        return project;
    }

}
