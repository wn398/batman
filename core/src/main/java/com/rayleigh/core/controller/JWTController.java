package com.rayleigh.core.controller;

import com.rayleigh.core.model.LoginInModel;
import com.rayleigh.core.model.ResultWrapper;
import com.rayleigh.core.util.JWTUtil;
import com.rayleigh.core.util.MD5Base64Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/jwtCtl")
public class JWTController extends BaseController {

    @Value("${jwt.expire.millisecond}")
    private Long jwtExpireTime;
    //私钥
    @Value("${jwt.secret.key}")
    private String base64Key;

    @PostMapping("/getToken")
    public ResultWrapper getToken(@RequestBody LoginInModel loginModel){
        Map<String, String> map = new HashMap<>();
        map.put("username",loginModel.getUsername());
        map.put("password",loginModel.getPassword());

        //如果为空则默认10分钟
        if(null ==jwtExpireTime){
            jwtExpireTime = 600000l;
        }
        String token = JWTUtil.createJWT(map,jwtExpireTime,base64Key);
        return getSuccessResult(token);
    }






}
