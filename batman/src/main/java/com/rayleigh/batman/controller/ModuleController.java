package com.rayleigh.batman.controller;

import com.rayleigh.batman.model.Module;
import com.rayleigh.batman.service.ModuleService;
import com.rayleigh.batman.service.ProjectService;
import com.rayleigh.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 * Created by wangn20 on 2017/6/13.
 */
@Controller
@RequestMapping("/moduleCtl")
public class ModuleController extends BaseController {
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private ProjectService projectService;

    @RequestMapping(value = "/getAll")
    @ResponseBody
    public List<Module> getAll(){
        return moduleService.getAll();
    }

    @RequestMapping(value = "/delById")
    @ResponseBody
    public Module deleteById(@RequestBody Module module){
        //设置项目更新时间
        projectService.setUpdateDate(moduleService.findOne(module.getId()).getProject().getId(),new Date());
        moduleService.deleteById(module.getId());
        return module;
    }

}
