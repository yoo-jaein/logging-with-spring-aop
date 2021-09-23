package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Album {
	@Id
	@GeneratedValue
	private Long id;
	private String title;
	private String artist;
	private int stockQuantity;
	private int price;
}
