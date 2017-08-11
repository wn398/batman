package com.rayleigh.batman.controller;

import com.rayleigh.batman.service.SysUserService;
import com.rayleigh.core.controller.BaseController;
import com.rayleigh.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wangn20 on 2017/6/13.
 */
public class SysUserController extends BaseController {
    @Autowired
    private SysUserService sysUserService;

}
