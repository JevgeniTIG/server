package com.example.beautyspot.entity;

import lombok.Data;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;

@Data
@Entity
public class ImageModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(columnDefinition = "LONGBLOB")
	private byte[] imageBytes;

	@JsonIgnore
	private Long userId;

	@JsonIgnore
	private Long postId;
}
