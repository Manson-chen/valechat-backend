package com.vale.valechat.util;

import com.vale.valechat.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtils {
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    //token到期时间10小时
    private static final int EXPIRATION_TIME= 10*60*60*1000;
    //密钥盐
    private static final String SECRET_KEY="#vale_chat#";


    /**
     * Generate token
     * @param email
     * @param username
     * @param userRole
     * @return
     */
    public static String createToken(String email, String username, Integer userRole) {
        Date expireTime =new Date(System.currentTimeMillis() + EXPIRATION_TIME);
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("username", username);
        claims.put("role", userRole);

        String token = Jwts.builder()
                .addClaims(claims)
                .setExpiration(expireTime)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
        return token;
    }

    /**
     * validate Token
     * @param token
     * @return
     */
    public static boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
            String email = String.valueOf(claims.get("email"));
            String username = String.valueOf(claims.get("username"));
            String userRole = String.valueOf(claims.get("role"));
            System.out.println(email + " " + username + " " + userRole);
            return true;
        } catch (Exception e) {
            // Token 解析或验证失败
            return false;
        }
    }

    /**
     * 解析Token
     * @param token
     * @return
     */
    public static User parseToken(String token){
        User user = new User();
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        String email = String.valueOf(claims.get("email"));
        String username = String.valueOf(claims.get("username"));
        int userRole = (int) claims.get("role");
        user.setEmail(email);
        user.setUserName(username);
        user.setUserRole(userRole);
        return user;
    }


}
