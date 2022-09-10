package com.tanhua.commons.utils;

import io.jsonwebtoken.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;

/**
 * @Description: JWt加密解密工具
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */
public class JwtUtils {

    /**
     * generate token 生成token
     *
     * @param
     * @return
     */
    public static String getToken(Map map) {
        long currentTime = System.currentTimeMillis();
        return Jwts.builder().signWith(SignatureAlgorithm.HS512, Constants.JWT_SECRET).setExpiration(new Date(currentTime + Constants.JWT_TIME_OUT * 1000)).addClaims(map).compact();
    }


    /**
     * 获取Token中的claims信息
     *
     * @param token
     * @return
     */
    public static Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(Constants.JWT_SECRET).parseClaimsJws(token).getBody();
    }


    /**
     * 是否有效 true-有效，false-失效 ，解析token，得到失效异常，说明就是失效了，捕捉依赖异常
     *
     * @param token
     * @return
     */
    public static boolean verifyToken(String token) {

        if (StringUtils.isEmpty(token)) {
            return false;
        }
        try {
            Claims claims = Jwts.parser().setSigningKey(Constants.JWT_SECRET).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            System.out.println("token已过期");
            return false;
        }catch (SignatureException e){
            System.out.println("token不合法");
            return false;
        }
        return true;
    }
}