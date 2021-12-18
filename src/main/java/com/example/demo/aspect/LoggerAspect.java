package com.example.demo.aspect;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.demo.annotation.Logging;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LoggerAspect {

	private static final String format = "yyyy-MM-dd HH:mm:ss.SSS";

	@Around("@annotation(com.example.demo.annotation.Logging) && @annotation(logging)")
	public Object aroundLogger(ProceedingJoinPoint joinPoint, Logging logging) throws Exception {
		CustomLog customLog = new CustomLog();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		customLog.setCreatedAt(LocalDateTime.now().format(formatter));

		HttpServletRequest request =
			((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		customLog.setIp(getClientIpAddr(request));
		customLog.setUri(request.getRequestURI());
		customLog.setDomain(request.getServerName());

		Object result = null;
		try {
			result = joinPoint.proceed();
		} catch (Throwable t) {
			t.printStackTrace();
			customLog.setResult("fail-" + t.getMessage());
		}

		if (result instanceof ResponseEntity) {
			ResponseEntity responseEntity = (ResponseEntity) result;

			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				customLog.setResult("success");
			} else {
				customLog.setResult("fail-" + responseEntity.getStatusCode().getReasonPhrase());
			}
		}

		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		customLog.setMethod(methodSignature.getName());
		customLog.setItem(logging.item());
		customLog.setAction(logging.action());

		log.info(getMessage(customLog));

		return result;
	}

	private static String getClientIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		return ip;
	}

	private String getMessage(CustomLog customLog) throws JsonProcessingException {
		Map<String,String> map = new LinkedHashMap<>();

		map.put("createdAt", customLog.getCreatedAt());
		map.put("ip", customLog.getIp());
		map.put("item", customLog.getItem());
		map.put("action", customLog.getAction());
		map.put("result", customLog.getResult());
		map.put("uri", customLog.getUri());
		map.put("domain", customLog.getDomain());
		map.put("method", customLog.getMethod());

		return new ObjectMapper().writeValueAsString(map);
	}
}
