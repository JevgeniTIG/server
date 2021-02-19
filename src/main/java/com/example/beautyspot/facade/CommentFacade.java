package com.example.beautyspot.facade;

import com.example.beautyspot.dto.CommentDTO;
import com.example.beautyspot.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentFacade {

	public CommentDTO commentToCommentDTO(Comment comment) {
		CommentDTO commentDTO = new CommentDTO();
		commentDTO.setId(comment.getId());
		commentDTO.setUserName(comment.getUserName());
		commentDTO.setMessage(comment.getMessage());

		return commentDTO;
	}
}
