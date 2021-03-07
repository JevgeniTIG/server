package com.example.beautyspot.web;

import com.example.beautyspot.entity.ImageModel;
import com.example.beautyspot.payload.response.MessageResponse;
import com.example.beautyspot.services.ImageService;
import com.example.beautyspot.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;

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
	public ResponseEntity<byte[]> getImageToPost(@PathVariable("postId") String postId,
												 @PathVariable("fileName") String fileName) throws IOException {
		File postImage = imageService.getImagesToPost(Long.parseLong(postId), fileName);
		byte[] fileContents = Files.readAllBytes(postImage.toPath());

		return ResponseEntity
				.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(fileContents);
	}


	@PostMapping("/{postId}/delete")
	public ResponseEntity<MessageResponse> deletePostImages(@PathVariable("postId") String postId
	) throws IOException {
		imageService.deleteImages(Long.parseLong(postId));
		return ResponseEntity.ok(new MessageResponse("Image successfully deleted"));
	}


}
