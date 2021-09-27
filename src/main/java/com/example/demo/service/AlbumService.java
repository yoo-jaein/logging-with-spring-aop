package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.model.Album;
import com.example.demo.repository.AlbumRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumService {

	private final AlbumRepository albumRepository;

	public Optional<Album> findAlbumById(Long id) {
		return albumRepository.findById(id);
	}

	public List<Album> findAllAlbum() {
		return albumRepository.findAll();
	}

	public Album createAlbum(Album album) {
		return albumRepository.save(album);
	}

	public Album updateAlbum(Long id, Album album) {
		Optional<Album> albumOptional = albumRepository.findById(id);

		if (albumOptional.isEmpty()) {
			throw new RuntimeException("There is no Album with id" + id);
		}

		Album newAlbum = albumOptional.get();
		newAlbum.setArtist(album.getArtist());
		newAlbum.setPrice(album.getPrice());
		newAlbum.setTitle(album.getTitle());
		newAlbum.setStockQuantity(album.getStockQuantity());
		// MDC.put("Update Album with album_id=", id.toString());

		return albumRepository.save(newAlbum);
	}

	public void deleteAlbum(Long id) {
		albumRepository.deleteById(id);
	}

	public void deleteAllAlbum() {
		albumRepository.deleteAll();
	}
}
