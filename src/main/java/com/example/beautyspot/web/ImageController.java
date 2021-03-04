package com.example.beautyspot.web;

import com.example.beautyspot.entity.ImageModel;
import com.example.beautyspot.payload.response.MessageResponse;
import com.example.beautyspot.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/image")
@CrossOrigin

public class ImageController {
	@Autowired
	private ImageService imageService;

	@PostMapping("/upload")
	public ResponseEntity<MessageResponse> uploadImageToUser(@RequestParam("file") MultipartFile file,
															 Principal principal) throws IOException {
		imageService.uploadImageToUser(file, principal);

		return ResponseEntity.ok(new MessageResponse("Image successfully uploaded"));
	}

	@GetMapping("/profileImage")
	public ResponseEntity<ImageModel> getImageToUser(Principal principal) {
		ImageModel userImage = imageService.getImageToUser(principal);

		return new ResponseEntity<>(userImage, HttpStatus.OK);
	}

	@PostMapping("/{postId}/upload")
	public ResponseEntity<MessageResponse> uploadImageToPost(@PathVariable("postId") String postId,
															 @RequestParam("file") MultipartFile file
	) throws IOException {
		imageService.uploadImageToPost(file, Long.parseLong(postId));
		return ResponseEntity.ok(new MessageResponse("Image successfully uploaded"));
	}

	@GetMapping("/{postId}/images/{fileName}")
	public ResponseEntity<File> getImageToPost(@PathVariable("postId") String postId,
													 @PathVariable("fileName") String fileName ) throws IOException {
		File postImage = imageService.getImagesToPost(Long.parseLong(postId), fileName);

		return new ResponseEntity<>(postImage, HttpStatus.OK);
	}

//	@GetMapping("/{postId}/images/")
//	public ResponseEntity<List<File>> getImageToPost(@PathVariable("postId") String postId) throws IOException {
//		List<File> postImages = imageService.getImagesToPost(Long.parseLong(postId));
//
//		return new ResponseEntity<>(postImages, HttpStatus.OK);
//	}

	@PostMapping("/{postId}/delete")
	public ResponseEntity<MessageResponse> deletePostImages(@PathVariable("postId") String postId
	) throws IOException {
		imageService.deleteImages(Long.parseLong(postId));
		return ResponseEntity.ok(new MessageResponse("Image successfully deleted"));
	}


}
