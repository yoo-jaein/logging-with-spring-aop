package com.example.demo.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LoggerAspect {
	@Around("@annotation(com.example.demo.annotation.Logging)")
	public Object LogMethodInvocation(ProceedingJoinPoint joinPoint) throws Throwable {
		log.debug("메서드 호출 이전");

		Object methodInvocationAlert = null;
		try {
			methodInvocationAlert = joinPoint.proceed();
		} catch(Throwable e) {
			log.error("메서드 호출 중 예외가 발생했습니다!");
		}

		log.debug("메서드 호출 이후");
		return methodInvocationAlert;
	}
}
