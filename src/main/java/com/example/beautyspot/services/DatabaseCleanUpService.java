package com.example.beautyspot.services;

import com.example.beautyspot.entity.ImageModel;
import com.example.beautyspot.entity.Post;
import com.example.beautyspot.exceptions.PostNotFoundException;
import com.example.beautyspot.repository.ImageRepository;
import com.example.beautyspot.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

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
		if (postRepository.findAllByActiveOrderByCreatedDateDesc("yes").size() > 200) {

			int smallestId = postRepository.findAllByActiveOrderByCreatedDateDesc("yes").size() - 1;
			Long Id = postRepository.findAllByActiveOrderByCreatedDateDesc("yes").get(smallestId).getId();

			Post post = getPostById(Id);
			List<ImageModel> imagesToPost = imageRepository.findAllByPostId(post.getId());
			post.setActive("no");
			postRepository.save(post);

			if (!CollectionUtils.isEmpty(imagesToPost)) {
				imageRepository.deleteAll(imagesToPost);
			}

		}
	}

	public Post getPostById(Long postId) {
		return postRepository.findPostById(postId)
				.orElseThrow(() -> new PostNotFoundException("Post" + postId + "cannot be found"));
	}

}
