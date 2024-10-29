package personal.cyy.playground.advice;

import com.alibaba.fastjson2.JSON;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import personal.cyy.playground.common.annotation.SkipLogging;

import java.lang.reflect.Method;

/**
 * @author www.exception.site (exception 教程网)
 * @date 2019/2/12
 * @time 14:03
 * @discription
 **/
@Aspect
@Component
public class WebLogAdvice {
    private final static Logger logger = LoggerFactory.getLogger(WebLogAdvice.class);

    /**
     * 以 controller 包下定义所有请求为切入点
     */
    @Pointcut("execution(public * personal.cyy.playground.controller.*.*(..))")
    public void webLog() {
    }

    /**
     * 在切点之前织入
     *
     * @param joinPoint
     * @throws Throwable
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 检查方法上是否有 @SkipLogging 注解
        if (isSkipLogging(joinPoint)) {
            return;
        }
        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 打印请求相关参数
        logger.info("========================================== Start ==========================================");
        // 打印请求 url
        logger.info("URL            : {}", request.getRequestURL().toString());
        // 打印 Http method
        logger.info("HTTP Method    : {}", request.getMethod());
        // 打印调用 controller 的全路径以及执行方法
        logger.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        // 打印请求的 IP
        logger.info("IP             : {}", request.getRemoteAddr());
        // 打印请求入参
        logger.info("Request Args   : {}", JSON.toJSONString(joinPoint.getArgs()));
    }

    /**
     * 在切点之后织入
     *
     * @throws Throwable
     */
    @After("webLog()")
    public void doAfter(JoinPoint joinPoint) throws Throwable {
        if (isSkipLogging(joinPoint)) {
            return;
        }
        logger.info("=========================================== End ===========================================");
        // 每个请求之间空一行
        logger.info("");
    }

    /**
     * 环绕
     *
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        if (isSkipLogging(proceedingJoinPoint)) {
            return result;
        }
        // 打印出参
        logger.info("Response Args  : {}", JSON.toJSONString(result));
        // 执行耗时
        logger.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
        return result;
    }

    private boolean isSkipLogging(JoinPoint joinPoint) {
        try {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            return method.isAnnotationPresent(SkipLogging.class);
        } catch (Exception e) {
            logger.error("Failed to check @SkipLogging annotation", e);
            return false;
        }
    }
}