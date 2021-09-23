package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Album;

public interface AlbumRepository extends JpaRepository<Album, Long> {
}
