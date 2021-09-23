package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Book {
	@Id
	@GeneratedValue
	private Long id;
	private String author;
	private String isbn;
	private String publisher;
	private int stockQuantity;
	private int price;
}
