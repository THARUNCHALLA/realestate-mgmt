package com.Realestate.RealEstateManagement.Services;

import com.Realestate.RealEstateManagement.Entity.Role;
import com.Realestate.RealEstateManagement.Entity.User;
import com.Realestate.RealEstateManagement.JWTUtil.JwtUtil;
import com.Realestate.RealEstateManagement.Repository.UserRepository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<Map<String, String>> registerUser(String username, String email, String password, Role role) {
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "User already exists!"));
        }

        User newUser = new User(username, email, passwordEncoder.encode(password), role);
        userRepository.save(newUser);

        logger.info("User registered successfully as " + role + "!");
        return ResponseEntity.ok(Map.of("message", "User registered successfully!", "role", role.toString()));
    }

    public ResponseEntity<Map<String, String>> loginUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            logger.warning("Login failed: User not found!");
            return ResponseEntity.badRequest().body(Map.of("message", "User not found!"));
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warning("Login failed: Invalid credentials!");
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid credentials!"));
        }

        String token = jwtUtil.generateToken(email);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Login successful!");
        response.put("role", user.getRole().toString());
        response.put("token", token);

        logger.info("User logged in successfully!");
        return ResponseEntity.ok(response);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public ResponseEntity<Map<String, String>> sendPasswordResetToken(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email not found"));
        }

        String resetToken = "RANDOM_GENERATED_TOKEN";
        return ResponseEntity.ok(Map.of("message", "Password reset token sent to email", "token", resetToken));
    }

    public ResponseEntity<Map<String, String>> validateResetToken(String token) {
        if ("RANDOM_GENERATED_TOKEN".equals(token)) {
            return ResponseEntity.ok(Map.of("message", "Token is valid"));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid token"));
    }

    public ResponseEntity<Map<String, String>> resetPassword(String token, String newPassword) {
        if (!"RANDOM_GENERATED_TOKEN".equals(token)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid token"));
        }

        return ResponseEntity.ok(Map.of("message", "Password successfully reset"));
    }
}
