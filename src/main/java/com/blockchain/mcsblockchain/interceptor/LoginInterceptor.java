package com.blockchain.mcsblockchain.interceptor;

import com.blockchain.mcsblockchain.Utils.HttpContextUtil;
import com.blockchain.mcsblockchain.pojo.Return.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

//在前后端分离的情况下，比较流行的认证方案是 JWT认证 认证，和传统的session认证不同，jwt是一种无状态的认证方法，
// 也就是服务端不再保存任何认证信息。这里只是简单地判断一下前端的请求头里是否存有 token
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@RequestBody HttpServletRequest request, @RequestBody HttpServletResponse response, Object handler) throws IOException {

        //从header中获取token
        String token = "";
        Cookie[] cookies = request.getCookies();
        System.out.println("method:"+request.getMethod());
        //if(request.getMethod().equals("OPTIONS")) return false;
        try{
              if(cookies.length>0) {
                  for (Cookie c : cookies) {
                      if(Objects.equals(c.getName(), "token")) token = c.getValue();
                      System.out.println(c.getName() + ":" + c.getValue());
                  }
              }
          }
          catch (Exception e){
              System.out.println(e.getMessage());
          }

        //如果token为空
        if (StringUtils.isBlank(token)) {
            //System.out.println("token为空");
            System.out.println("Sb");
            setReturn(response,401,"用户未登录，请先登录");
            return false;
        }
        else{
            System.out.println("not");
            System.out.println("tokenssssss: "+token);
        }
        //在实际使用中还会:
        // 1、校验token是否能够解密出用户信息来获取访问者
        // 2、token是否已经过期

        return true;
    }



    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }

    //返回json格式错误信息
    private static void setReturn(HttpServletResponse response, Integer code, String msg) throws IOException, IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Allow-Origin", HttpContextUtil.getOrigin());
        httpResponse.setHeader("Access-Control-Allow-Headers","Content-Type");
        httpResponse.setHeader("Access-Control-Request-Method","GET,POST");
        httpResponse.setContentType("application/x-www-form-urlencoded;charset=utf-8");
        //UTF-8编码
        httpResponse.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-www-form-urlencoded;charset=utf-8");

        Result result = new Result(msg,code,"");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(result);
        httpResponse.getWriter().print(json);
    }

}