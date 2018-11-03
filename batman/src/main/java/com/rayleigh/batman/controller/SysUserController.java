package com.rayleigh.batman.controller;

import com.rayleigh.batman.model.Project;
import com.rayleigh.batman.model.SysUser;
import com.rayleigh.batman.service.SysUserService;
import com.rayleigh.core.controller.BaseController;
import com.rayleigh.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangn20 on 2017/6/13.
 */
@Controller
@RequestMapping("/adminCtl")
public class SysUserController extends BaseController {
    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/login")
    public void login(SysUser sysUser, HttpServletRequest request, HttpServletResponse response, HttpServletRequest httpServletRequest)throws Exception{
        List<SysUser> list = sysUserService.findByNameAndPassword(sysUser.getName(),sysUser.getPassword());

        if(null !=list &&list.size()>0){
            request.getSession().setAttribute("userId",list.get(0).getId());
            SysUser sysUser2 = sysUserService.findOne(list.get(0).getId());
            List<Project> projectList = new ArrayList();
            sysUser2.getProjects().forEach(it->{
                Project project = new Project();
                project.setId(it.getId());
                project.setDescription(it.getDescription());
                project.setName(it.getName());
                projectList.add(project);
            });
            httpServletRequest.getSession().setAttribute("projects",projectList);
            String root = request.getRequestURI().split("/")[1];
            response.sendRedirect(new StringBuilder("/").append(root).toString());
        }else{
            String root = request.getRequestURI().split("/")[1];
            response.sendRedirect(new StringBuilder("/").append(root).append("/").append("login").toString());
        }
    }

    @RequestMapping("/register")
    public String register(SysUser sysUser, HttpServletRequest request, HttpServletResponse response){
        if(StringUtil.isEmpty(sysUser.getName())||StringUtil.isEmpty(sysUser.getPassword())){
            return "/register";
        }
        List<SysUser> sysUserList = sysUserService.findByName(sysUser.getName());
        if(null!=sysUserList&&sysUserList.size()>0){
            return "/register";
        }
        SysUser sysUser1 = sysUserService.save(sysUser);
        if(null !=sysUser1.getId()){
            try {
                String root = request.getRequestURI().split("/")[1];
                response.sendRedirect(new StringBuilder("/").append(root).append("/").append("login").toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }else{
            return "/register";
        }
    }

}
