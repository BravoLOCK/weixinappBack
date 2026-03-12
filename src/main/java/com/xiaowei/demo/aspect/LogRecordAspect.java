package com.xiaowei.demo.aspect;

import com.xiaowei.demo.common.AopDebugProperties;
import com.xiaowei.demo.common.LogRecord;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 只对使用自定义注解的特定方法记录日志
 * 记录 @LogRecord 注解标记的方法调用日志
 */
@Aspect
@Component
@Slf4j
public class LogRecordAspect {

    @Autowired
    private AopDebugProperties aopDebugProperties;

    /**
     * 切点：拦截所有带有 @LogRecord 注解的方法
     */
    @Pointcut("@annotation(com.xiaowei.demo.common.LogRecord)")
    public void logRecordMethods() {
    }

    /**
     * 环绕通知：记录方法执行详情
     */
    @Around("logRecordMethods() && @annotation(logRecord)")
    public Object around(ProceedingJoinPoint joinPoint, LogRecord logRecord) throws Throwable {
        if (Boolean.FALSE.equals(aopDebugProperties.getLogRecord())) {
            return joinPoint.proceed(); // 不开启则跳过
        }

        long start = System.currentTimeMillis();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Logger logger = LoggerFactory.getLogger(targetClass);

        String description = logRecord.value(); // 注解中的描述信息
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        logger.info("\n======   LogRecord 方法调用 ======\n" +
                        "  描述        : {}\n" +
                        "  方法        : {}\n" +
                        "  参数        : {}\n" +
                        "=====================================",
                description, methodName, Arrays.toString(args));

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            logger.error("\n======  ❌ LogRecord 异常 ======\n" +
                            "  方法        : {}\n" +
                            "  异常信息     : {}\n" +
                            "=====================================",
                    methodName, e.getMessage(), e);
            throw e;
        }

        long end = System.currentTimeMillis();
        logger.info("\n======  ✅ LogRecord 返回结果 ======\n" +
                        "  方法        : {}\n" +
                        "  返回值      : {}\n" +
                        "⏱️ 耗时        : {} ms\n" +
                        "=====================================",
                methodName, result, end - start);

        return result;
    }
}
