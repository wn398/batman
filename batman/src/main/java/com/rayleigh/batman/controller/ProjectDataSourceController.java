package com.rayleigh.batman.controller;

import com.rayleigh.batman.model.Module;
import com.rayleigh.batman.model.Project;
import com.rayleigh.batman.model.ProjectDataSource;
import com.rayleigh.batman.service.ProjectDataSourceService;
import com.rayleigh.batman.service.ProjectService;
import com.rayleigh.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/projectDataSourceCtl")
public class ProjectDataSourceController extends BaseController {
    @Autowired
    private ProjectDataSourceService projectDataSourceService;
    @Autowired
    private ProjectService projectService;

    @RequestMapping(value = "/delById")
    @ResponseBody
    public ProjectDataSource deleteById(@RequestBody ProjectDataSource projectDataSource){
        //设置hierachyDate
        ProjectDataSource projectDataSource2 = projectDataSourceService.findOne(projectDataSource.getId());
        Project project = projectDataSource2.getProject();
        projectService.setUpdateDate(project.getId(),new Date());

        projectDataSourceService.deleteById(projectDataSource.getId());
        return projectDataSource;
    }


}
