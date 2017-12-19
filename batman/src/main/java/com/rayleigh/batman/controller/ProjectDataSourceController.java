package com.rayleigh.batman.controller;

import com.rayleigh.batman.model.Module;
import com.rayleigh.batman.model.ProjectDataSource;
import com.rayleigh.batman.service.ProjectDataSourceService;
import com.rayleigh.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/projectDataSourceCtl")
public class ProjectDataSourceController extends BaseController {
    @Autowired
    private ProjectDataSourceService projectDataSourceService;

    @RequestMapping(value = "/delById")
    @ResponseBody
    public ProjectDataSource deleteById(@RequestBody ProjectDataSource projectDataSource){
        projectDataSourceService.deleteById(projectDataSource.getId());
        return projectDataSource;
    }


}
