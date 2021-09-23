package com.example.demo.aspect;

import java.util.Map;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.json.classic.JsonLayout;

public class CustomLayout extends JsonLayout {
	@Override
	protected void addCustomDataToJsonMap(Map<String, Object> map, ILoggingEvent event) {
		map.put("만든 이", "yoo-jaein");
		super.addCustomDataToJsonMap(map, event);
	}
}
