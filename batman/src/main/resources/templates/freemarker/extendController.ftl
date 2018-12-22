<#include "CopyRight.ftl">
package ${project.packageName}.extend.controller;

import ${project.packageName}.base.model.*;
import ${project.packageName}.extend.service.*;
import com.rayleigh.core.controller.BaseController;
import ${project.packageName}.base.controller.${entity.name}Controller;
import com.rayleigh.core.enums.ResultStatus;
import com.rayleigh.core.model.ResultWrapper;
import com.rayleigh.core.service.BaseService;
import com.rayleigh.core.util.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;

@Api(tags = "${entity.description} Controller", description = "${entity.description}相关api")
@RequestMapping("/${entity.name ?uncap_first}Ctl")
@Controller
public class ${entity.name}ExtendController extends ${entity.name}Controller{
@Autowired
private ${entity.name}ExtendService ${entity.name ?uncap_first}ExtendService;


}
