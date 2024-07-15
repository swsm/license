package com.swsm.license.client.interceptor;

import com.alibaba.fastjson.JSON;
import com.swsm.license.client.model.LicenseVerify;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 证书校验拦截器
 * 如若需要校验证书，则解开@Component注释，并在WebMvcConfig中注册拦截器(springboot)或在spring-mvc.xml中配置拦截器(springmvc)
 */
@Slf4j
@Component
public class LicenseCheckInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LicenseVerify licenseVerify = new LicenseVerify();

        //校验证书是否有效
        boolean verifyResult = licenseVerify.verify();

        if (verifyResult) {
            return true;
        } else {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            Map<String, String> result = new HashMap<>(1);
            result.put("result", "您的证书无效，请核查服务器是否取得授权或重新申请证书！");

            response.getWriter().write(JSON.toJSONString(result));
            log.error("您的证书无效，请核查服务器是否取得授权或重新申请证书！");
            return false;
        }
    }

}
