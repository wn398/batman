package com.rayleigh.core.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.*;

/**
 * json web token支持类
 */
public class JWTUtil {
    private static Random random = new Random();
    public static Claims parseJWT(String jsonWebToken, String base64Security){
        try  
        {  
            Claims claims = Jwts.parser()
                       .setSigningKey(DatatypeConverter.parseBase64Binary(base64Security))
                       .parseClaimsJws(jsonWebToken).getBody();  
            return claims;  
        }  
        catch(Exception ex)  
        {  
            return null;  
        }  
    }

    //根据自定义属性，过期时间间隔，base64码获取jwt
    public static String createJWT(Map propertyMap, long ttlMillis, String base64Security)
    {  
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
           
        long nowMillis = System.currentTimeMillis();  
        Date now = new Date(nowMillis);
           
        //生成签名密钥  
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(base64Security);  
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
           
        //添加构成JWT的参数
        JwtBuilder builder = Jwts.builder().setHeaderParam("type", "JWT");
        //添加自定义参数
        for(Map.Entry<String,Object> entry:(Set<Map.Entry<String,Object>>)propertyMap.entrySet()){
            builder.claim(entry.getKey(),entry.getValue());
        }
        //随机插入一个字串,每次生成的不一样
        builder.claim("random",random.nextLong());

        builder.signWith(signatureAlgorithm, signingKey);

         //添加Token过期时间  
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);  
            builder.setExpiration(exp).setNotBefore(now);  
        }
         //生成JWT  
        return builder.compact();  
    }

    public static void main(String[] args) {
        String base64;
        try {
            base64 = MD5Base64Util.getBase64Md5("王宁");
        }catch (Exception e){
            base64 = "tim";
        }
        System.out.println(base64);
//        Map<String, String> map = new HashMap<>();
//        map.put("role","rolerole");
//        map.put("userId","userIduserId");
//        map.put("unique_name","namename");
//
//        String jwt = createJWT(map,60000,base64);
//        map.put("haa","haha");
//        System.out.println(jwt);
//        System.out.println(createJWT(map,60000,base64));
//        Claims claims = parseJWT(jwt+"1",base64);
//        System.out.println(claims.getExpiration());
//        System.out.println("role:"+claims.get("role"));
//
//        System.out.println("userId:"+claims.get("userId"));
//        System.out.println("name:"+claims.get("unique_name"));

    }

}