package com.example.demo.aspect;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CustomLog {
	private String createdAt;
	private String ip;
	private String item;
	private String action;
	private String result;
	private String uri;
	private String domain;
	private String method;
	//List<> detail - 상세 내역
}
