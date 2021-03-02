package com.example.beautyspot.services;

import com.example.beautyspot.entity.Post;
import com.example.beautyspot.exceptions.PostNotFoundException;
import com.example.beautyspot.repository.ImageRepository;
import com.example.beautyspot.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;

@Service
public class DatabaseCleanUpService {

	private final PostRepository postRepository;
	private final ImageRepository imageRepository;
	private final ImageService imageService;

	@Autowired
	public DatabaseCleanUpService(PostRepository postRepository, ImageRepository imageRepository, ImageService imageService
	) {
		this.postRepository = postRepository;
		this.imageRepository = imageRepository;
		this.imageService = imageService;

	}

	public void cleanUp() throws IOException {
		if (postRepository.findAllByActiveOrderByCreatedDateDesc("yes").size() > 200) {

			int smallestId = postRepository.findAllByActiveOrderByCreatedDateDesc("yes").size() - 1;
			Long Id = postRepository.findAllByActiveOrderByCreatedDateDesc("yes").get(smallestId).getId();

			Post post = getPostById(Id);
			post.setActive("no");
			postRepository.save(post);
			imageService.deleteImages(Id);

		}
	}

	public Post getPostById(Long postId) {
		return postRepository.findPostById(postId)
				.orElseThrow(() -> new PostNotFoundException("Post" + postId + "cannot be found"));
	}

}
