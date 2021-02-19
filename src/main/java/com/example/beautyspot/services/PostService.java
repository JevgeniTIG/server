package com.example.beautyspot.services;

import com.example.beautyspot.dto.PostDTO;
import com.example.beautyspot.entity.ImageModel;
import com.example.beautyspot.entity.Post;
import com.example.beautyspot.entity.User;
import com.example.beautyspot.exceptions.PostNotFoundException;
import com.example.beautyspot.repository.ImageRepository;
import com.example.beautyspot.repository.PostRepository;
import com.example.beautyspot.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

	public static final Logger LOG = LoggerFactory.getLogger(PostService.class);

	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final ImageRepository imageRepository;

	@Autowired
	public PostService(PostRepository postRepository, UserRepository userRepository, ImageRepository imageRepository) {
		this.postRepository = postRepository;
		this.userRepository = userRepository;
		this.imageRepository = imageRepository;
	}

	public Post createPost(PostDTO postDTO, Principal principal) {
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

		LOG.info("Saving post for user " + user.getEmail());
		return postRepository.save(post);
	}

	public Post updatePost(PostDTO postDTO, Principal principal, Long postId) {
		Post post = getPostById(postId, principal);
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
			postsForCurrentPage = postRepository.findAllByOrderByCreatedDateDesc();

			if (highValue > postsForCurrentPage.size()) {
				highValue = postsForCurrentPage.size();
			}
			return postsForCurrentPage.subList(lowValue, highValue);
		}
		postsForCurrentPage = postRepository.findAllByCategoryOrderByCreatedDateDesc(category);
			if (highValue > postsForCurrentPage.size()) {
				highValue = postsForCurrentPage.size();
			}
			return postsForCurrentPage.subList(lowValue, highValue);
	}


	public List<Post> getAllPostsByCategory(String category) {
		return postRepository.findAllByCategoryOrderByCreatedDateDesc(category);
	}

	public Post getPostById(Long postId, Principal principal) {
		User user = getUserByPrincipal(principal);
		return postRepository.findPostByIdAndUser(postId, user)
				.orElseThrow(() -> new PostNotFoundException("Post cannot be found for user " + user.getEmail()));
	}

	public List<Post> getAllPostsForUser(Principal principal) {
		User user = getUserByPrincipal(principal);
		return postRepository.findAllByUserOrderByCreatedDateDesc(user);
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

	public void deletePost(Long postId, Principal principal){
		Post post = getPostById(postId, principal);
		List<ImageModel> imagesToPost = imageRepository.findAllByPostId(post.getId());
		postRepository.delete(post);
		if (!CollectionUtils.isEmpty(imagesToPost)) {
			imageRepository.deleteAll(imagesToPost);
		}
	}

	private User getUserByPrincipal(Principal principal) {
		String userName = principal.getName();
		return userRepository.findUserByUserName(userName)
				.orElseThrow(() -> new UsernameNotFoundException("Username " + userName + " not found"));
	}
}
