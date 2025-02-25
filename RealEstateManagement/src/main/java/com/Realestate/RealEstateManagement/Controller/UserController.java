package com.Realestate.RealEstateManagement.Controller;

import com.Realestate.RealEstateManagement.Entity.Role;
import com.Realestate.RealEstateManagement.Entity.User;
import com.Realestate.RealEstateManagement.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/v1/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        return userService.registerUser(user.getUsername(), user.getEmail(), user.getPassword(), user.getRole());
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody User user) {
        return userService.loginUser(user.getEmail(), user.getPassword());
    }

    @GetMapping("/users-by-role")
    public List<User> getUsersByRole(@RequestParam("role") String role) {
        return userService.getUsersByRole(Role.valueOf(role.toUpperCase()));
    }
}
