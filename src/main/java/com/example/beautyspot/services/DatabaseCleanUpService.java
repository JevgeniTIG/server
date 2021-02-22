package com.example.beautyspot.services;

import com.example.beautyspot.repository.ImageRepository;
import com.example.beautyspot.repository.PostRepository;
import com.example.beautyspot.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseCleanUpService {

	private final PostRepository postRepository;
	private final ImageRepository imageRepository;

	@Autowired
	public DatabaseCleanUpService(PostRepository postRepository, ImageRepository imageRepository
	) {
		this.postRepository = postRepository;
		this.imageRepository = imageRepository;
	}

	public void cleanUp() {
		if (postRepository.findAllByOrderByCreatedDateDesc().size() > 17) {
			int smallestId = postRepository.findAllByOrderByCreatedDateDesc().size() - 1;
			Long Id = postRepository.findAllByOrderByCreatedDateDesc().get(smallestId).getId();
			postRepository.deleteById(Id);

		}
	}
}
