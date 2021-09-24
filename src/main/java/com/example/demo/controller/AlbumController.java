package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.annotation.Logging;
import com.example.demo.model.Album;
import com.example.demo.repository.AlbumRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/album")
@RequiredArgsConstructor
public class AlbumController {
	private final AlbumRepository albumRepository;

	@Logging
	@GetMapping("/{id}")
	public ResponseEntity<Album> getAlbum(@PathVariable Long id) {
		Optional<Album> albumOptional = albumRepository.findById(id);
		if (albumOptional.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(albumOptional.get(), HttpStatus.OK);
		}
	}

	@Logging
	@GetMapping
	public ResponseEntity<List<Album>> getAllAlbums() {
		return new ResponseEntity<>(albumRepository.findAll(), HttpStatus.OK);
	}

	@Logging
	@PostMapping
	public ResponseEntity<Album> postAlbum(@RequestBody Album album) {
		return new ResponseEntity<>(albumRepository.save(album), HttpStatus.OK);
	}

	@Logging
	@PutMapping("/{id}")
	public ResponseEntity<Album> putAlbum(@PathVariable Long id, @RequestBody Album album) {
		Optional<Album> albumOptional = albumRepository.findById(id);

		if (albumOptional.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(albumRepository.save(album), HttpStatus.OK);
	}

	@Logging
	@DeleteMapping("/{id}")
	public ResponseEntity<HttpStatus> deleteAlbum(@PathVariable Long id) {
		albumRepository.deleteById(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Logging
	@DeleteMapping
	public ResponseEntity<HttpStatus> deleteAllAlbums() {
		albumRepository.deleteAll();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
