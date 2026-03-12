package com.xiaowei.demo.aspect;

import com.xiaowei.demo.common.AopDebugProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 全局 Controller 层日志记录切面
 */
@Aspect
@Component
@Slf4j
public class ControllerLogAspect {
    @Autowired
    private AopDebugProperties aopDebugProperties;

    /**
     * 定义切点，匹配 com.xiaowei 包下所有子包中的 controller 类的公共方法
     */
    @Pointcut("execution(public * com.xiaowei..controller..*(..))")
    public void controllerMethods() {
    }

    /**
     * 环绕通知，用于记录 controller 层方法的请求和响应信息
     *
     * @param joinPoint 切入点对象，包含被拦截方法的信息
     * @return 被拦截方法的执行结果
     * @throws Throwable 如果执行过程中出现异常
     */
    @Around("controllerMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (Boolean.FALSE.equals(aopDebugProperties.getController())) {
            // 不开启，直接执行原方法
            return joinPoint.proceed();
        }

        // 记录开始时间
        long start = System.currentTimeMillis();
        // 获取目标类的 Class 对象
        Class<?> targetClass = joinPoint.getTarget().getClass();
        // 获取目标类的 Logger 对象
        Logger logger = LoggerFactory.getLogger(targetClass);

        // 获取当前请求的 HttpServletRequest 对象
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 提取请求的 URL、方法、IP 地址、处理方法和参数
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String ip = request.getRemoteAddr();
        String classMethod = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();


        String red = "\u001B[31m"; // ANSI 红色
        String line = IntStream.range(0, 200)  // 200 可以改成任意长度
                .mapToObj(i -> "#")
                .collect(Collectors.joining());
        System.out.println(red + line);


        // 记录请求信息
        logger.info("\n======   请求信息 ======\n" +
                        "  URL        : {}\n" +
                        "  Method     : {}\n" +
                        "  IP         : {}\n" +
                        "  Handler    : {}\n" +
                        "  Parameters : {}\n" +
                        "==========================",
                url, method, ip, classMethod, Arrays.toString(args));

        // 尝试执行目标方法，并记录执行结果或异常信息
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            // 记录异常信息，并重新抛出异常
            logger.error("\n======   ❌ 异常信息 ======\n" +
                            "  Handler    : {}\n" +
                            "  Error      : {}\n" +
                            "==========================",
                    classMethod, e.getMessage(), e);
            throw e;
        }

        // 记录结束时间，并计算耗时
        long end = System.currentTimeMillis();
        // 记录响应信息
        logger.info("\n======   ✅ 响应信息 ======\n" +
                        "  Handler    : {}\n" +
                        "  Response   : {}\n" +
                        "⏱️ 耗时        : {} ms\n" +
                        "==========================",
                classMethod, result, end - start);

        return result;
    }
}