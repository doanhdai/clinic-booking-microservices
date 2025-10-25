package com.myclinic.user.service;


import com.myclinic.common.security.JwtUtils;
import com.myclinic.user.dto.LoginRequestDTO;
import com.myclinic.user.dto.LoginResponsedDTO;
import com.myclinic.user.dto.RegisterRequestDTO;
import com.myclinic.user.entity.User;
import com.myclinic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final CloudinaryService cloudinaryService;

    public void register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setBirth(request.getBirth());
        user.setGender(User.Gender.valueOf(request.getGender().toLowerCase()));

        // Không cho client set role
        user.setRole(User.Role.patient);
        //avatar mặc định
        user.setAvatar(generateInitialAvatar(request.getFullName()));
        user.setStatus(1);
        userRepository.save(user);
    }


    public LoginResponsedDTO login(LoginRequestDTO request) {
        // Xác thực email và password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        //Lấy user sau khi xác thực
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        //Sinh JWT token
        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());

        // Trả response chứa thông tin người dùng
        return new LoginResponsedDTO(
                token,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name() // enum → string
        );
    }
    private String generateInitialAvatar(String fullName) {
        String[] parts = fullName.trim().split(" ");
        String initials = parts.length >= 2
                ? ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase()
                : fullName.substring(0, 1).toUpperCase();
        //  API miễn phí để tạo ảnh avatar có chữ:
        return "https://ui-avatars.com/api/?name=" + initials + "&background=random";
    }

}
