package com.Realestate.RealEstateManagement.Password;

import com.Realestate.RealEstateManagement.Entity.User;

import com.Realestate.RealEstateManagement.Repository.UserRepository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                JavaMailSender mailSender) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    public void generateResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30)); // 30-minute expiry
        tokenRepository.save(resetToken);

        sendResetEmail(user.getEmail(), token);
    }


    private void sendResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText("Click the link to reset your password: http://localhost:3000/reset-password/" + token);
        mailSender.send(message);
    }

    // ✅ Validate Token
    public boolean validateResetToken(String token) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);
        return resetToken.isPresent() && resetToken.get().getExpiryDate().isAfter(LocalDateTime.now());
    }


    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken); // Delete expired token
            throw new RuntimeException("Token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user); // ✅ Save new password

        tokenRepository.delete(resetToken); // ✅ Remove used token
    }
}
