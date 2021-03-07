package com.example.beautyspot.services;

import com.example.beautyspot.config.Config;
import com.example.beautyspot.dto.PostDTO;
import com.example.beautyspot.entity.Post;
import com.example.beautyspot.entity.User;
import com.example.beautyspot.exceptions.PostNotFoundException;
import com.example.beautyspot.repository.ImageRepository;
import com.example.beautyspot.repository.PostRepository;
import com.example.beautyspot.repository.UserRepository;
import javassist.expr.NewArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

	public static final Logger LOG = LoggerFactory.getLogger(PostService.class);

	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final ImageRepository imageRepository;
	private final DatabaseCleanUpService databaseCleanUpService;
	private final ImageService imageService;

	@Autowired
	public PostService(PostRepository postRepository, UserRepository userRepository, ImageRepository imageRepository,
					   DatabaseCleanUpService databaseCleanUpService, ImageService imageService
	) {
		this.postRepository = postRepository;
		this.userRepository = userRepository;
		this.imageRepository = imageRepository;
		this.databaseCleanUpService = databaseCleanUpService;
		this.imageService = imageService;
	}


	public void uploadImageToPost(String filesNames, Long postId) throws IOException {

		LOG.info(filesNames);
		List<String> imagesPaths = new ArrayList<>();

		List<String> filesNamesList = Arrays.asList(filesNames.split(","));
		for (int i = 0; i < filesNamesList.size(); i++) {
			String fileName = filesNamesList.get(i);
			String filePath = Config.HOST + "image/" + postId + "/images/" + fileName;
			imagesPaths.add(filePath);
		}

		Post post = getPostById(postId);
		post.setImages(imagesPaths.toString().substring(1, imagesPaths.toString().indexOf("]")));
		postRepository.save(post);

	}

	public Post createPost(PostDTO postDTO, Principal principal) throws IOException {
		User user = getUserByPrincipal(principal);
		Post post = new Post();
		post.setUser(user);
		post.setTitle(postDTO.getTitle());
		post.setCaption(postDTO.getCaption());
		post.setLocation(postDTO.getLocation());
		post.setPrice(postDTO.getPrice());
		post.setStatus("ON SALE");
		post.setCategory(postDTO.getCategory());
		post.setNumberOfLikes(0);
		post.setShowMail(postDTO.isShowMail());
		post.setShowPhone(postDTO.isShowPhone());
		post.setActive("yes");
		post.setImages("");

		LOG.info("Saving post for user " + user.getEmail());

		databaseCleanUpService.cleanUp();
		return postRepository.save(post);
	}

	public Post updatePost(PostDTO postDTO, Principal principal, Long postId) {
		Post post = getPostByIdAndUser(postId, principal);
		post.setTitle(postDTO.getTitle());
		post.setCaption(postDTO.getCaption());
		post.setLocation(postDTO.getLocation());
		post.setCategory(postDTO.getCategory());
		post.setPrice(postDTO.getPrice());
		post.setStatus(postDTO.getStatus());

		return postRepository.save(post);
	}

	public List<Post> getAllPosts() {
		return postRepository.findAllByOrderByCreatedDateDesc();
	}

	public List<Post> getAllPostsForCurrentPage(int lowValue, int highValue, String category) {

		List<Post> postsForCurrentPage;
		if (category == null) {
			postsForCurrentPage = postRepository.findAllByActiveOrderByCreatedDateDesc("yes");

			if (highValue > postsForCurrentPage.size()) {
				highValue = postsForCurrentPage.size();
			}
			return postsForCurrentPage.subList(lowValue, highValue);
		}
		postsForCurrentPage = postRepository.findAllByActiveAndCategoryOrderByCreatedDateDesc("yes", category);
		if (highValue > postsForCurrentPage.size()) {
			highValue = postsForCurrentPage.size();
		}
		return postsForCurrentPage.subList(lowValue, highValue);
	}


	public List<Post> getAllPostsByCategory(String category) {
		return postRepository.findAllByCategoryOrderByCreatedDateDesc(category);
	}

	public Post getPostByIdAndUser(Long postId, Principal principal) {
		User user = getUserByPrincipal(principal);
		return postRepository.findPostByIdAndUser(postId, user)
				.orElseThrow(() -> new PostNotFoundException("Post cannot be found for user " + user.getEmail()));
	}

	public Post getPostById(Long postId) {

		return postRepository.findPostById(postId)
				.orElseThrow(() -> new PostNotFoundException("Post cannot be found"));
	}


	public List<Post> getAllPostsForUser(Principal principal) {
		User user = getUserByPrincipal(principal);
		return postRepository.findAllByActiveAndUserOrderByCreatedDateDesc("yes", user);
	}

	public Post likePost(Long postId, String userName) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new PostNotFoundException("Post cannot be found"));
		Optional<String> userLiked = post.getLikedUsers()
				.stream()
				.filter(u -> u.equals(userName)).findAny();

		if (userLiked.isPresent()) {
			post.setNumberOfLikes(post.getNumberOfLikes() - 1);
			post.getLikedUsers().remove(userName);
		} else {
			post.setNumberOfLikes(post.getNumberOfLikes() + 1);
			post.getLikedUsers().add(userName);
		}
		return postRepository.save(post);
	}

	public void deletePost(Long postId, Principal principal) throws IOException {
		Post post = getPostByIdAndUser(postId, principal);
		post.setActive("no");
		postRepository.save(post);
		imageService.deleteImages(postId);
	}

	private User getUserByPrincipal(Principal principal) {
		String userName = principal.getName();
		return userRepository.findUserByUserName(userName)
				.orElseThrow(() -> new UsernameNotFoundException("Username " + userName + " not found"));
	}

}
