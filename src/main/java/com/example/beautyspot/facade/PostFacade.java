package com.example.beautyspot.facade;

import com.example.beautyspot.dto.PostDTO;
import com.example.beautyspot.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostFacade {
	public PostDTO postToPostDTO(Post post) {
		PostDTO postDTO = new PostDTO();
		postDTO.setUserName(post.getUser().getUsername());
		postDTO.setId(post.getId());
		postDTO.setTitle(post.getTitle());
		postDTO.setCaption(post.getCaption());
		postDTO.setLocation(post.getLocation());
		postDTO.setLikes(post.getNumberOfLikes());
		postDTO.setUserLiked(post.getLikedUsers());
		postDTO.setPrice(post.getPrice());
		postDTO.setStatus(post.getStatus());

		String year = (post.getCreatedDate().toString().substring(0, 4));
		String month = (post.getCreatedDate().toString().substring(5, 7));
		String date = (post.getCreatedDate().toString().substring(8, 10));



		postDTO.setCreatedDate(date + '-' + month + '-' + year);
		postDTO.setCategory(post.getCategory());
		postDTO.setEmail(post.getUser().getEmail());
		postDTO.setPhone(post.getUser().getPhone());
		postDTO.setShowMail(post.isShowMail());
		postDTO.setShowPhone(post.isShowPhone());


		return postDTO;
	}
}
