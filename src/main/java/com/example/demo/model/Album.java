package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Album {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "album_id")
	private Long id;
	private String title;
	private String artist;
	@Column(name = "stock_quantity")
	private int stockQuantity;
	private int price;

	public String toString() {
		return id + " " + title + " " + artist + " " + stockQuantity + " " + price;
	}
}
