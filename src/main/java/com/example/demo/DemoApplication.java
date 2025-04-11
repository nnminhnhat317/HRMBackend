package com.example.demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@SpringBootApplication
public class DemoApplication {


	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		System.out.println("Hello World!");
		//Tạo mật khẩu cho admin và encode với bcryt để lưu vào db
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// Mật khẩu gốc
		String rawPasswordAd = "admin Admin@123";
		System.out.println("Tài khoản admin gốc: " + rawPasswordAd);
		// Mã hóa bằng BCrypt
//		String encodedPasswordAd = encoder.encode(rawPasswordAd);
//		System.out.println("Mật khẩu admin đã mã hóa ngẫu nhiên: " + encodedPasswordAd);

		String rawPasswordUs = "user User@123";
		System.out.println("Tài khoản user gốc: " + rawPasswordUs);
//		String encodedPasswordUs = encoder.encode(rawPasswordUs);
//		System.out.println("Mật khẩu user đã mã hóa ngẫu nhiên: " + encodedPasswordUs);
	}
}
