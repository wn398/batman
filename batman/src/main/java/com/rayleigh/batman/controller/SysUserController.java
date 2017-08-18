package com.rayleigh.batman.controller;

import com.rayleigh.batman.model.SysUser;
import com.rayleigh.batman.service.SysUserService;
import com.rayleigh.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    public String login(SysUser sysUser, HttpServletRequest request){
        List<SysUser> list = sysUserService.findByNameAndPassword(sysUser.getName(),sysUser.getPassword());

        if(null !=list &&list.size()>0){
            request.getSession().setAttribute("user",sysUser.getName());
            return  "/batman/batmanIndex";
        }else{
            return "/login";
        }
    }

    @RequestMapping("/register")
    public String register(SysUser sysUser, HttpServletRequest request, HttpServletResponse response){
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
