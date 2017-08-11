package com.rayleigh.batman.controller;

import com.rayleigh.batman.model.Module;
import com.rayleigh.batman.service.ModuleService;
import com.rayleigh.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by wangn20 on 2017/6/13.
 */
@Controller
@RequestMapping("/moduleCtl")
public class ModuleController extends BaseController {
    @Autowired
    private ModuleService moduleService;

    @RequestMapping(value = "/getAll")
    @ResponseBody
    public List<Module> getAll(){
        return moduleService.getAll();
    }

    @RequestMapping(value = "/delById")
    @ResponseBody
    public Module deleteById(@RequestBody Module module){
        moduleService.deleteById(module.getId());
        return module;
    }

}
