package com.example.beautyspot.repository;

import com.example.beautyspot.entity.Post;
import com.example.beautyspot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findAllByUserOrderByCreatedDateDesc(User user);

	List<Post> findAllByOrderByCreatedDateDesc();

	List<Post> findAllByCategoryOrderByCreatedDateDesc(String category);

	Optional<Post> findPostByIdAndUser(Long Id, User user);

	void deleteById(Long Id);


}
