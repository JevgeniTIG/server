package com.example.beautyspot.services;

import com.example.beautyspot.constants.ImagesLocationConstant;
import com.example.beautyspot.entity.ImageModel;
import com.example.beautyspot.entity.User;
import com.example.beautyspot.repository.ImageRepository;
import com.example.beautyspot.repository.UserRepository;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class ImageService {

	public static final Logger LOG = LoggerFactory.getLogger(ImageService.class);

	private ImageRepository imageRepository;
	private UserRepository userRepository;

	@Autowired
	public ImageService(ImageRepository imageRepository, UserRepository userRepository) {
		this.imageRepository = imageRepository;
		this.userRepository = userRepository;

	}

	public ImageModel uploadImageToUser(MultipartFile file, Principal principal) throws IOException {
		User user = getUserByPrincipal(principal);
		LOG.info("Uploading image profile to user " + user.getEmail());
		ImageModel userProfileImage = imageRepository.findByUserId(user.getId()).orElse(null);
		if (!ObjectUtils.isEmpty(userProfileImage)) {
			imageRepository.delete(userProfileImage);
		}
		ImageModel imageModel = new ImageModel();
		imageModel.setUserId(user.getId());
		imageModel.setImageBytes(compressBytes(file.getBytes()));
		imageModel.setName(file.getOriginalFilename());
		return imageRepository.save(imageModel);
	}

	public void uploadImageToPost(MultipartFile file, Long postId) throws IOException {

		String dir = ImagesLocationConstant.POST_IMAGES_LOCATION + postId;
		String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
		Path uploadPath = Paths.get(dir);

		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		try (InputStream inputStream = file.getInputStream()) {
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(inputStream, filePath);
		} catch (IOException e) {
			throw new IOException("Could not create a folder " + postId);
		}

		LOG.info("Uploading image to post " + postId);
	}

	public ImageModel getImageToUser(Principal principal) {
		User user = getUserByPrincipal(principal);
		ImageModel imageModel = imageRepository.findByUserId(user.getId()).orElse(null);
		if (!ObjectUtils.isEmpty(imageModel)) {
			imageModel.setImageBytes(decompressBytes(imageModel.getImageBytes()));
		}
		return imageModel;
	}

	public File getImagesToPost(Long postId, String fileName) throws IOException {

		File file = new File(ImagesLocationConstant.POST_IMAGES_LOCATION + postId + '/' + fileName);

//		if (file.isDirectory()) {
//			imagesToPost = Files.walk(Paths.get(ImagesLocationConstant.POST_IMAGES_LOCATION + postId))
//					.filter(Files::isRegularFile)
//					.map(Path::toFile)
//					.collect(Collectors.toList());
//			return imagesToPost;
//		}
//		else {
//			LOG.info("No such directory ../images/" + postId);
//			return new ArrayList<>();
//		}
		return file;
	}

//	public List<File> getImagesToPost(Long postId) throws IOException {
//		List<File> imagesToPost;
//		File file = new File(ImagesLocationConstant.POST_IMAGES_LOCATION + postId);
//
//		if (file.isDirectory()) {
//				imagesToPost = Files.walk(Paths.get(ImagesLocationConstant.POST_IMAGES_LOCATION + postId))
//						.filter(Files::isRegularFile)
//						.map(Path::toFile)
//						.collect(Collectors.toList());
//				return imagesToPost;
//			}
//		else {
//			LOG.info("No such directory ../images/" + postId);
//			return new ArrayList<>();
//		}
//	}

	public void deleteImages(Long postId) throws IOException {
		String dirToDelete = ImagesLocationConstant.POST_IMAGES_LOCATION + postId;
		Path deletePath = Paths.get(dirToDelete);
		try {
			FileUtils.deleteDirectory(new File(String.valueOf(deletePath)));
		} catch (IOException e) {
			throw new IOException("Could not delete folder " + postId);
		}
	}

	private byte[] compressBytes(byte[] data) {
		Deflater deflater = new Deflater();
		deflater.setInput(data);
		deflater.finish();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		byte[] buffer = new byte[1024];
		while (!deflater.finished()) {
			int count = deflater.deflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		try {
			outputStream.close();
		} catch (IOException e) {
			LOG.error("Cannot compress bytes");
		}
		System.out.println("Compressed image byte size " + outputStream.toByteArray().length);
		return outputStream.toByteArray();
	}

	private static byte[] decompressBytes(byte[] data) {
		Inflater inflater = new Inflater();
		inflater.setInput(data);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		byte[] buffer = new byte[1024];
		try {
			while (!inflater.finished()) {
				int count = inflater.inflate(buffer);
				outputStream.write(buffer, 0, count);
			}
			outputStream.close();
		} catch (IOException | DataFormatException e) {
			LOG.error("Cannot decompress bytes");
		}
		return outputStream.toByteArray();
	}

	private User getUserByPrincipal(Principal principal) {
		String userName = principal.getName();
		return userRepository.findUserByUserName(userName)
				.orElseThrow(() -> new UsernameNotFoundException("Username " + userName + " not found"));
	}

	private <T> Collector<T, ?, T> toSinglePostCollector() {
		return Collectors.collectingAndThen(Collectors.toList(),
				list -> {
					if (list.size() != 1) {
						throw new IllegalStateException();
					}
					return list.get(0);
				}
		);

	}
}
