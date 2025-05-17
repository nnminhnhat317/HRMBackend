package com.example.demo.controller;

import com.example.demo.configJWT.JwtUtils;
import com.example.demo.entity.Users;
import com.example.demo.repository.UsersRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UsersRepository userRepository;
    private final JwtUtils jwtUtils;
    public AuthController(UsersRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //Nhận dữ liệu JSON từ request body dưới dạng Map<String, String> vì nguyên tắc bảo mật dùng DTO
    //DTO chỉ duy nhất cung cấp username và password, field khác kh cần
    //Nếu dùng Entity Users thì vi phạm ngtac và lỗi mapping khi không liệt kê toàn bo filed của User
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        //lấy username,password tu request
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        // lấy username tu db theo username cua request
        Users userdb = userRepository.findByUsername(username);
        // so sanh 2 password thong qua bcrypt
        if (userdb != null && passwordEncoder.matches(password, userdb.getPassword())) {
            //generate token
            String token = jwtUtils.generateToken(userdb.getUsername(), userdb.getRole(), userdb.getEmployeeId().getId());
            //Trả về JWT token dưới dạng JSON cho client qua reponse
            Map<String, Object> response = new HashMap<>();
            response.put("username", userdb.getUsername());
            response.put("role", userdb.getRole());
            response.put("employeeId", userdb.getEmployeeId().getId());
            response.put("token", token);
            ResponseEntity.ok().body(response);
            return ResponseEntity.ok().body(response);
        } else {
    //trả về mã lỗi HTTP 401: nhưng cần DTO
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password"));
        }
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> userRequest) {
        // Kiểm tra username đã tồn tại chưa
        if (userRepository.findByUsername(userRequest.get("username")) != null) {
            throw new RuntimeException("Username already exists!");
        }
        // Mã hóa mật khẩu trước khi lưu vào database
        String hashedPassword = passwordEncoder.encode(userRequest.get("password"));
        //Tạo user lưu vào db
        Users user = new Users();
        user.setUsername(userRequest.get("username"));
        user.setPassword(hashedPassword);
        user.setRole("user");
        // Lưu user vào database
        userRepository.save(user);

        // Trả về response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        return response;
    }
}

