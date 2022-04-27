package com.example.forum;

import com.cloudinary.Cloudinary;
import com.example.forum.model.Role;
import com.example.forum.model.dto.UserDTO;
import com.example.forum.service.CloudinaryService;
import com.example.forum.service.UserService;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@SpringBootTest
class ForumApplicationTests {

	UserService userService;

	//CloudinaryService cloudinaryService;

	@Test
	void multipartFileLoadUsingCloudinaryService() {

		//cloudinaryService.uploadFile();
	}

	@Test
	void newTest(){
		UserDTO userDTO = new UserDTO();
		userDTO.setId(11);
		userDTO.setRole(Role.ROLE_ADMIN);
		userDTO.setEmail("email");
		userDTO.setName("name");
		userDTO.setPictureUrl("uuuurrrrllll");

		userService.update(userDTO);

		//assert
	}

}
