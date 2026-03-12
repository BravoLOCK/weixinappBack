package com.xiaowei.demo.aspect;

import com.xiaowei.demo.common.AopDebugProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Service 层方法日志切面（记录业务逻辑调用）
 */
@Aspect
@Component
@Slf4j
public class ServiceLogAspect {

    @Autowired
    private AopDebugProperties aopDebugProperties;


    /**
     * 切入 com.xiaowei 包下所有 service 的方法
     */
    @Pointcut("execution(* com.xiaowei..service..*(..))")
    public void serviceMethods() {
    }

    /**
     * 环绕通知记录 service 层方法的调用情况
     */
    @Around("serviceMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (Boolean.FALSE.equals(aopDebugProperties.getService())) {
            // 不开启，直接执行原方法
            return joinPoint.proceed();
        }


        long start = System.currentTimeMillis();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Logger logger = LoggerFactory.getLogger(targetClass);

        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        logger.info("\n====== ⚙️ Service 方法调用 ======\n" +
                        "  Method     : {}\n" +
                        "  Parameters : {}\n" +
                        "===============================",
                methodName, Arrays.toString(args));

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            logger.error("\n====== ⚙️ ❌ Service 异常 ======\n" +
                            "  Method     : {}\n" +
                            "  Error      : {}\n" +
                            "=============================",
                    methodName, e.getMessage(), e);
            throw e;
        }

        long end = System.currentTimeMillis();
        logger.info("\n====== ⚙️ ✅ Service 返回结果 ======\n" +
                        "  Method     : {}\n" +
                        "  Result     : {}\n" +
                        "⏱️ 耗时        : {} ms\n" +
                        "===============================",
                methodName, result, end - start);

        return result;
    }
}
