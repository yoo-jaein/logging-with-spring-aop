package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.MDC;
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
import com.example.demo.service.AlbumService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/album")
@RequiredArgsConstructor
public class AlbumController {

	private final AlbumService albumService;

	@Logging(item = "Album")
	@GetMapping("/{id}")
	public ResponseEntity<Album> getAlbum(@PathVariable Long id) {
		Optional<Album> albumOptional = albumService.findAlbumById(id);
		if (albumOptional.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(albumOptional.get(), HttpStatus.OK);
		}
	}

	@Logging(item = "Album")
	@GetMapping
	public ResponseEntity<List<Album>> getAllAlbums() {
		return new ResponseEntity<>(albumService.findAllAlbum(), HttpStatus.OK);
	}

	@Logging(item = "Album", action = "create")
	@PostMapping
	public ResponseEntity<Album> postAlbum(@RequestBody Album album) {
		return new ResponseEntity<>(albumService.createAlbum(album), HttpStatus.OK);
	}

	@Logging(item = "Album", action = "update")
	@PutMapping("/{id}")
	public ResponseEntity<Album> putAlbum(@PathVariable Long id, @RequestBody Album album) {
		try {
			Album newAlbum = albumService.updateAlbum(id, album);
			return new ResponseEntity<>(newAlbum, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@Logging(item = "Album", action = "delete")
	@DeleteMapping("/{id}")
	public ResponseEntity<HttpStatus> deleteAlbum(@PathVariable Long id) {
		albumService.deleteAlbum(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Logging(item = "Album", action = "delete")
	@DeleteMapping
	public ResponseEntity<HttpStatus> deleteAllAlbums() {
		albumService.deleteAllAlbum();
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
