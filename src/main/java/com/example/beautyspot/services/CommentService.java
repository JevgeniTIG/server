package com.example.beautyspot.services;

import com.example.beautyspot.dto.CommentDTO;
import com.example.beautyspot.entity.Comment;
import com.example.beautyspot.entity.Post;
import com.example.beautyspot.entity.User;
import com.example.beautyspot.exceptions.PostNotFoundException;
import com.example.beautyspot.repository.CommentRepository;
import com.example.beautyspot.repository.PostRepository;
import com.example.beautyspot.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

	public static final Logger LOG = LoggerFactory.getLogger(CommentService.class);

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	@Autowired
	public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
		this.commentRepository = commentRepository;
		this.postRepository = postRepository;
		this.userRepository = userRepository;
	}

	public Comment saveComment(Long postId, CommentDTO commentDTO, Principal principal) {
		User user = getUserByPrincipal(principal);
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new PostNotFoundException("Post cannot be found for user " + user.getEmail()));
		Comment comment = new Comment();
		comment.setPost(post);
		comment.setUserId(user.getId());
		comment.setUserName(user.getUsername());
		comment.setMessage(commentDTO.getMessage());

		LOG.info("Saving comment for Post: " + post.getId());
		return commentRepository.save(comment);
	}

	public List<Comment> getAllCommentsForPost(Long postId) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new PostNotFoundException("Post cannot be found"));
		List<Comment> comments = commentRepository.findAllByPost(post);
		return comments;
	}

	public void deleteComment(Long commentId) {
		Optional<Comment> comment = commentRepository.findById(commentId);
		comment.ifPresent(commentRepository::delete);
	}

	private User getUserByPrincipal(Principal principal) {
		String userName = principal.getName();
		return userRepository.findUserByUserName(userName)
				.orElseThrow(() -> new UsernameNotFoundException("Username " + userName + " not found"));
	}
}
