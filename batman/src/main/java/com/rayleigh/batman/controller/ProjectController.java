package com.rayleigh.batman.controller;

import com.rayleigh.batman.model.Project;
import com.rayleigh.batman.service.ProjectService;
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
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangn20 on 2017/6/13.
 */
@Controller
@RequestMapping("/projectCtl")
public class ProjectController extends BaseController{
    @Autowired
    private ProjectService projectService;


    @PostMapping(value = "/doAdd")
    @ResponseBody
    public ResultWrapper doAdd(@Valid @RequestBody Project project){
        project = projectService.save(project);
        preventCirculation(project);
        return getSuccessResult(project);
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
        model.addAttribute("projects",projectService.getAll());
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
    public ResultWrapper getAll(){
        List<Project> projects = projectService.getAll();
        //projects.forEach(project -> System.out.println(project.getModules().size()));
        return getSuccessResult(projects);
    }

    @PostMapping(value = "/partUpdate")
    @ResponseBody
    public ResultWrapper partUpdate(@Valid @RequestBody Project project){
        if(null!=project&&!StringUtil.isEmpty(project.getId())){
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

    @RequestMapping("/test")
    @ResponseBody
    public String test(){
        projectService.testListSql();
        return "success";
    }

    @GetMapping("/test2")
    public int testError(){
        return 9 / 0;  // 除 0异常
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
            model.addAttribute("project", project2);
            return "/page/entities-add";
        }else{
            return "error";
        }
    }

    //防止循环检测
    private Project preventCirculation( Project project) {
        if(project.getModules().size()>0){
            project.getModules().parallelStream().forEach(module -> module.setProject(null));
        }
        if(project.getEntities().size()>0){
            project.getEntities().parallelStream().forEach(entities -> entities.setProject(null));
        }
        return project;
    }

}
