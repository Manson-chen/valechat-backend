package com.vale.valechat.config;

import com.vale.valechat.controller.threadlocal.UserThreadLocal;
import com.vale.valechat.model.entity.User;
import com.vale.valechat.util.JWTUtils;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    //Controller逻辑执行之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println("------------------------");

//        //跨域请求会首先发一个option请求，直接返回正常状态并通过拦截器
//        if(request.getMethod().equals("OPTIONS")){
//            response.setStatus(HttpServletResponse.SC_OK);
//            return true;
//        }

        //判断访问的是我们的接口路径是否为HandLerMethod (controller 方法）
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 取得token
        String tokenHeader = request.getHeader(JWTUtils.TOKEN_HEADER);
        if (tokenHeader == null || !tokenHeader.startsWith(JWTUtils.TOKEN_PREFIX)) {
            JsonObject json=new JsonObject();
            json.addProperty("code","500");
            json.addProperty("data", "");
            json.addProperty("msg","You need to login first!");
            response.getWriter().append(json.toString());
            System.out.println("Token is not exist");
        }

        // 验证token是否有效--无效已做异常抛出，由全局异常处理后返回对应信息
        // TokenUtils.getTokenBody(tokenHeader);

        String token = request.getHeader(JWTUtils.TOKEN_HEADER);
        System.out.println(token);
        if (tokenHeader != null){
            tokenHeader = tokenHeader.replace(JWTUtils.TOKEN_PREFIX, "");
            boolean result= JWTUtils.validateToken(tokenHeader);
            if (result){
                // 将token放入ThreadLocal
                User user = JWTUtils.parseToken(tokenHeader);
                UserThreadLocal.set(user);
                System.out.println("Add token to thread local, Go on...");
                return true;
            }else{

            }
        }

        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            JsonObject json=new JsonObject();
            json.addProperty("code","500");
            json.addProperty("data", "");
            json.addProperty("msg","token verify fail");
            response.getWriter().append(json.toString());
            System.out.println("Authentication failed and failed the interceptor");
        } catch (Exception e) {
            return false;
        }
        /**
         * 还可以在此处检验用户存不存在等操作
         */
        return false;
    }

    //Controller逻辑执行完毕但是视图解析器还未进行解析之前
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        System.out.println("postHandle....");
    }

    //Controller逻辑和视图解析器执行完毕
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        System.out.println("afterCompletion....");
        UserThreadLocal.remove();
    }
}
