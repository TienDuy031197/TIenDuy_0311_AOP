package com.github.icovn.aops;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


@Aspect
@Component
public class Handler {
    private static int count = 0;
    private static Logger logger = LoggerFactory.getLogger(Handler.class);
    private boolean isChecked = false;

    @Before("execution(* com.github.icovn.aop..*(..))")
    public void login() throws IOException, AuthenticationException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        String username;
        String pass;
        while (!isChecked) {
            logger.info("Username: ");
            username = bf.readLine();
            logger.info("Password: ");
            pass = bf.readLine();
            if (username.equals("admin") && pass.equals("admin")) {
                logger.info("Login Success!");
                isChecked = true;
                break;
            } else {
                logger.info("Login failed!");
                logger.info("Re-login: ");
                if (count++ > 1) {
                    throw new AuthenticationException("end of login !");
                }
            }
        }
    }

    @Around("execution(* com.github.icovn.aop..*(..))")
    public Object logInputOutputTime(ProceedingJoinPoint joinPoint) throws Throwable {
        CodeSignature signatures = (CodeSignature) joinPoint.getSignature();
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        logger.info("Time: " + executionTime);

        Object[] signatureArgs = joinPoint.getArgs();
        if (signatureArgs.length == 0) {
            logger.info("No Input!");
        } else {
            logger.info("Input: \t");
            for (int i = 0; i < signatureArgs.length; i++) {
                logger.info("Param : " + (i + 1) + ":" + signatures.getParameterNames()[i]);
            }
        }
        logger.info("return type: " + proceed.getClass().getName());
        logger.info("Return value: " + proceed);
        return proceed;
    }
}
