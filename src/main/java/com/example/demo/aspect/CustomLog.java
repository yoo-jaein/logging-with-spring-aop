package com.example.demo.aspect;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomLog {
	private String createdAt;
	private String ip;
	private String item;
	private String action;
	private String result;
	private String uri;
	private String domain;
	//List<> detail - 상세 내역

	public String toString() {
		return createdAt + " " + ip + " " + item + " " + action + " " +  result + " " + uri + " " + domain;
	}
}
