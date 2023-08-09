package com.vale.valechat.oauth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.vale.valechat.common.BaseResponse;
import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.common.ResultUtils;
import com.vale.valechat.exception.BusinessException;
import com.vale.valechat.model.entity.User;
import com.vale.valechat.model.vo.UserVO;
import com.vale.valechat.service.UserService;
import com.vale.valechat.util.JWTUtils;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Resource
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getEmail();
        String avatar = oAuth2User.getAvatar();
        String userName = oAuth2User.getName();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        User user = userService.getOne(queryWrapper);

        UserVO userVO = new UserVO();

        if (user == null) {
            // register as a new customer
            System.out.println("register as a new customer");
            userVO = userService.createUserAfterOAuthLoginSuccess(email, userName, avatar);
        } else {
            // update existing customer
            System.out.println("update existing customer");
            user.setUserName(userName);
            user.setUserAvatar(avatar);
            userVO = userService.updateUserAfterOAuthLoginSuccess(user);
        }

        String token = JWTUtils.createToken(userVO.getEmail(), userVO.getUserName(), userVO.getUserRole());

//        Map<String, Object> result = new HashMap<String, Object>();
//        Map<String, Object> data = new HashMap<String, Object>();
//        result.put("message", "Login with oauth2 successfully");
//        result.put("code", 200);
//        data.put("token", JWTUtils.TOKEN_PREFIX + token);
//        data.put("user", userVO);
//        result.put("data", data);
        OAuth2UserVO oAuth2UserVO = new OAuth2UserVO();
        oAuth2UserVO.setUser(userVO);
        oAuth2UserVO.setToken(JWTUtils.TOKEN_PREFIX + token);
        BaseResponse<OAuth2UserVO> baseResponse = ResultUtils.success(oAuth2UserVO, "Login with oauth2 successfully");

        response.setContentType("text/html;charset=UTF-8");
//        String responseStr = new ObjectMapper().writeValueAsString(baseResponse);
//        System.out.println(responseStr);
        Gson gson = new Gson();
        String json = gson.toJson(baseResponse);
        System.out.println("------------" + json);

//        response.sendRedirect("http://localhost:8081/#/home");
//        response.setHeader(JWTUtils.TOKEN_HEADER, JWTUtils.TOKEN_PREFIX + token);
        PrintWriter writer = response.getWriter();
//        writer.write(json);
        writer.print("<script>window.opener.postMessage("+ json +  ",'http://localhost:8081');window.close();</script>");
        writer.flush();
    }
}
