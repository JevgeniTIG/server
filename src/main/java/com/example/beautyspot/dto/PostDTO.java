package com.example.beautyspot.dto;

import com.example.beautyspot.entity.User;
import lombok.Data;

import java.util.Set;

@Data
public class PostDTO {

	private Long id;
	private String title;
	private String caption;
	private String location;
	private String price;
	private String userName;
	private Integer likes;
	private Set<String> userLiked;
	private String status;
	private String createdDate;
	private String category;
	private String email;
	private String phone;
	private boolean showMail;
	private boolean showPhone;
	private String images;

}
