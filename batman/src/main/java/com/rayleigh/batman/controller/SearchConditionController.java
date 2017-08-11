package com.rayleigh.batman.controller;

import com.rayleigh.batman.service.SearchConditionService;
import com.rayleigh.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/searchConditionCtl")
public class SearchConditionController extends BaseController{
@Autowired
private SearchConditionService searchConditionService;

}
