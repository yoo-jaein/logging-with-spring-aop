package com.example.demo.aspect;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.demo.annotation.Logging;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LoggerAspect {

	private static final String format = "yyyy-MM-dd HH:mm:ss.SSS"; //2021-09-24 23:17:46.572

	@Around("@annotation(com.example.demo.annotation.Logging) && @annotation(logging)")
	public Object LogMethodInvocation(ProceedingJoinPoint joinPoint, Logging logging) throws Throwable {
		CustomLog customLog = new CustomLog();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		customLog.setCreatedAt(LocalDateTime.now().format(formatter));

		HttpServletRequest request =
			((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		customLog.setIp(getClientIpAddr(request)); //IPv6
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
		log.info("~호출한 메서드 정보~");

		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		Method method = methodSignature.getMethod();

		log.info("1) 호출한 메서드의 이름 : " + method.getName() + "()"); //getAllAlbums()
		log.info("2) 호출 결과 타입 : " + method.getReturnType().getCanonicalName()); //org.springframework.http.ResponseEntity
		log.info("2) 호출 결과 타입 : " + method.getGenericReturnType()); //org.springframework.http.ResponseEntity<java.util.List<com.example.demo.model.Album>>
		log.info("2) 호출 결과 타입 : " + method.getReturnType().getSimpleName()); //ResponseEntity

		log.info("3) 메서드 인자 : ");
		for (Object arg : joinPoint.getArgs()) {
			log.info("	" + arg.getClass().getSimpleName() + " 타입의 값 : " + arg.toString());
		}

		// for (Parameter parameter : method.getParameters()) {
		// 	log.info("parameter = " + parameter.getName());
		// 	log.info("parameter.getType() = " + parameter.getType());
		// }
		customLog.setItem(logging.item());
		customLog.setAction(logging.action());
		log.info("customLog = " + customLog.toString());

		return methodInvocationAlert;
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
}
