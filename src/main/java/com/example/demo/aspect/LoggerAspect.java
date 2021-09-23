package com.example.demo.aspect;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LoggerAspect {
	@Around("@annotation(com.example.demo.annotation.Logging)")
	public Object LogMethodInvocation(ProceedingJoinPoint joinPoint) throws Throwable {
		CustomLog customLog = new CustomLog();
		customLog.setCreatedAt(LocalDateTime.now());

		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		customLog.setIp(getClientIpAddr(request));
		customLog.setUri(request.getRequestURI());
		customLog.setDomain(request.getServerName());

		// 메서드 호출 이전

		Object methodInvocationAlert = null;
		try {
			methodInvocationAlert = joinPoint.proceed();
			customLog.setResult("success");
		} catch(Throwable e) {
			customLog.setResult("fail-" + e.toString());
			log.error("메서드 호출 중 예외가 발생했습니다!");
		}

		// 메서드 호출 이후

		customLog.setItem("");
		customLog.setAction("");

		return methodInvocationAlert;
	}

	private static String getClientIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");

		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}

		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}

		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}

		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}

		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		return ip;
	}
}
