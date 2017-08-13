package com.rayleigh.batman.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by wangn20 on 2017/7/6.
 */
@Controller
@RequestMapping("/")
public class PagePathController {
    //首页
    @RequestMapping(value={"/batman","/"})
    public String goBatman(){
        return "batman/batmanIndex";
    }
    //注册页面
    @RequestMapping(value = "/register")
    public String goRegisterPage(){
        return "batman/registration";
    }
    //登陆页面
    @RequestMapping(value = "/login")
    public String goLoginPage(){
        return "batman/login";
    }
    //返回到登陆页面
    @RequestMapping(value = "/goLogin")
    public String goLogin(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return "batman/login";
    }

    @RequestMapping("/test")
    public String goTest(){
        return "batman/sampleIndex";
    }
    //转向fragment下的页面
    @RequestMapping("/goFragment/{fragment}")
    public String goFragment(@PathVariable("fragment") String fragment){
        return "fragment/"+fragment;
    }
    //转向page目录下的页面
    @RequestMapping("/goPage/{page}")
    public String goPage(@PathVariable("page") String page){
        return "page/"+page;
    }
    //转向html下面的页面
    @RequestMapping("/goHtml/{html}")
    public String goHtml(@PathVariable("html") String html){
        return html;
    }
}
