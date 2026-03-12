package com.xiaowei.demo.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
         log.info("LogInterceptor ⽬标⽅法执⾏前执⾏..");
         return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        log.info("LogInterceptor ⽬标⽅法执⾏后执⾏..");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        log.info("LogInterceptor ⽬标⽅法完执⾏完毕后执行..");
        //注意：尽管有Exception参数，但此处并不会处理异常
    }
}

