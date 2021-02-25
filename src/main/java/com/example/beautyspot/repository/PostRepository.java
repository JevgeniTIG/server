package com.example.beautyspot.repository;

import com.example.beautyspot.entity.Post;
import com.example.beautyspot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {


	List<Post> findAllByOrderByCreatedDateDesc();

	List<Post> findAllByActiveAndUserOrderByCreatedDateDesc(String activeValue, User user);

	List<Post> findAllByActiveOrderByCreatedDateDesc(String activeValue);

	List<Post> findAllByActiveAndCategoryOrderByCreatedDateDesc(String activeValue, String category);

	List<Post> findAllByCategoryOrderByCreatedDateDesc(String category);

	Optional<Post> findPostByIdAndUser(Long Id, User user);

	Optional<Post> findPostById(Long Id);









}
