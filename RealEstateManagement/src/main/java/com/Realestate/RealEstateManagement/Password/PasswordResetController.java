package com.Realestate.RealEstateManagement.Password;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/user")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    // ✅ Forgot Password - Generates Reset Token
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        passwordResetService.generateResetToken(email);
        return ResponseEntity.ok(Map.of("message", "Reset link sent to email"));
    }

    // ✅ Validate Reset Token
    @GetMapping("/validate-reset-token")
    public ResponseEntity<Map<String, String>> validateResetToken(@RequestParam String token) {
        boolean isValid = passwordResetService.validateResetToken(token);
        return ResponseEntity.ok(Map.of("valid", String.valueOf(isValid)));
    }

    // ✅ Reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody PasswordResetRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }
}
