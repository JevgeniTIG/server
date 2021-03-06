package com.example.beautyspot.web;

import com.example.beautyspot.dto.PostDTO;
import com.example.beautyspot.entity.Post;
import com.example.beautyspot.facade.PostFacade;
import com.example.beautyspot.payload.response.MessageResponse;
import com.example.beautyspot.services.PostService;
import com.example.beautyspot.validations.ResponseErrorValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/post")
@CrossOrigin
public class PostController {

	@Autowired
	private PostFacade postFacade;
	@Autowired
	private PostService postService;
	@Autowired
	private ResponseErrorValidator responseErrorValidator;

	@PostMapping("/create")
	public ResponseEntity<Object> createPost(@Valid @RequestBody PostDTO postDTO, BindingResult bindingResult, Principal principal
											 ) throws IOException {
		ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
		if (!ObjectUtils.isEmpty(errors)) return errors;

		Post post = postService.createPost(postDTO, principal);
		PostDTO createPost = postFacade.postToPostDTO(post);

		return new ResponseEntity<>(createPost, HttpStatus.OK);
	}

	@GetMapping("/all")
	public ResponseEntity<List<PostDTO>> getAllPosts() {
		List<PostDTO> postDTOList = postService.getAllPosts()
				.stream()
				.map(postFacade::postToPostDTO)
				.collect(Collectors.toList());
		return new ResponseEntity<>(postDTOList, HttpStatus.OK);
	}

	@GetMapping(value = {"/all/{lowValue}/{highValue}","/all/{lowValue}/{highValue}/{category}"})
	public ResponseEntity<List<PostDTO>> getAllPostsForCurrentPage(@PathVariable("lowValue") int lowValue,
																   @PathVariable("highValue") int highValue,
																   @PathVariable(value = "category", required = false) String category) {
		List<PostDTO> postDTOList = postService.getAllPostsForCurrentPage(lowValue, highValue, category)
				.stream()
				.map(postFacade::postToPostDTO)
				.collect(Collectors.toList());
		return new ResponseEntity<>(postDTOList, HttpStatus.OK);
	}

	@GetMapping("/{category}/all")
	public ResponseEntity<List<PostDTO>> getAllPostsByCategory(@PathVariable("category") String category) {
		List<PostDTO> postDTOList = postService.getAllPostsByCategory(category)
				.stream()
				.map(postFacade::postToPostDTO)
				.collect(Collectors.toList());
		return new ResponseEntity<>(postDTOList, HttpStatus.OK);
	}

	@GetMapping("/user/posts")
	public ResponseEntity<List<PostDTO>> getAllPostsForUser(Principal principal) {
		List<PostDTO> postDTOList = postService.getAllPostsForUser(principal)
				.stream()
				.map(postFacade::postToPostDTO)
				.collect(Collectors.toList());
		return new ResponseEntity<>(postDTOList, HttpStatus.OK);
	}

	@PostMapping("/{postId}/{userName}/like")
	public ResponseEntity<PostDTO> likePost(@PathVariable("postId") String postId,
											@PathVariable("userName") String userName) {
		Post post = postService.likePost(Long.parseLong(postId), userName);
		PostDTO postDTO = postFacade.postToPostDTO(post);

		return new ResponseEntity<>(postDTO, HttpStatus.OK);
	}

	@PostMapping("/{postId}/delete")
	public ResponseEntity<MessageResponse> deletePost(@PathVariable("postId") String postId, Principal principal) throws IOException {
		postService.deletePost(Long.parseLong(postId), principal);

		return new ResponseEntity<>(new MessageResponse("Post deleted"), HttpStatus.OK);
	}

	@PostMapping("/{postId}/update")
	public ResponseEntity<Object> updatePost(@PathVariable("postId") String postId,
											 @Valid @RequestBody PostDTO postDTO, BindingResult bindingResult, Principal principal) {
		ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
		if (!ObjectUtils.isEmpty(errors)) return errors;

		Post post = postService.updatePost(postDTO, principal, Long.parseLong(postId));
		PostDTO postUpdated = postFacade.postToPostDTO(post);


		return new ResponseEntity<>(postUpdated, HttpStatus.OK);
	}


	@PostMapping("/{postId}/upload")
	public ResponseEntity<MessageResponse> uploadImageToPost(@PathVariable("postId") String postId,
															 @RequestParam("filesNames") String filesNames
	) throws IOException {
		postService.uploadImageToPost(filesNames, Long.parseLong(postId));
		return ResponseEntity.ok(new MessageResponse("Image successfully uploaded"));
	}


}
