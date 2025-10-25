package com.myclinic.user.service;

import com.myclinic.user.dto.UserInfoDTO;
import com.myclinic.user.dto.UserAccountDTO;
import com.myclinic.user.dto.ResetPasswordRequest;
import com.myclinic.user.entity.User;
import com.myclinic.user.mapper.UserMapper;
import com.myclinic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// import org.locationtech.jts.geom.Coordinate;
// import org.locationtech.jts.geom.GeometryFactory;
// import org.locationtech.jts.geom.Point;
// import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    public List<UserInfoDTO> getUsers() {
        List<User> list = userRepository.findAll();
        return userMapper.toDtoList(list);
    }

    public UserInfoDTO getUserById(Integer userId) {
        log.info("Fetching user by ID: {}", userId);
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElse(null);
    }
    
    public List<UserInfoDTO> getUsersByIds(List<Integer> userIds) {
        log.info("Fetching users by IDs: {}", userIds);
        List<User> users = userRepository.findByIds(userIds);
        return users.stream()
                .map(u -> userMapper.toDto(u))
                .toList();
    }

    public List<UserInfoDTO> getActiveDoctors() {
        log.info("Fetching active doctors");
        List<User> doctors = userRepository.findActiveDoctors();
        return doctors.stream()
                .map(u -> userMapper.toDto(u))
                .toList();
    }

    // public UserInfoDTO createUser(UserInfoDTO dto) {
    //     if (userRepository.existsByEmail(dto.getEmail())) {
    //         throw new RuntimeException("Email already exists!");
    //     }
    //     if (userRepository.existsByPhone(dto.getPhone())) {
    //         throw new RuntimeException("Phone already exists!");
    //     }
    //     if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
    //         throw new RuntimeException("Password is required");
    //     }

    //     User user = new User();
    //     user.setFullName(dto.getFullName());
    //     user.setBirth(dto.getBirth());

    //     User.Gender gender = User.Gender.female; // default
    //     if (dto.getGender() != null) {
    //         try {
    //             gender = User.Gender.valueOf(dto.getGender().toLowerCase());
    //         } catch (IllegalArgumentException e) {
    //             throw new RuntimeException("Invalid gender: " + dto.getGender());
    //         }
    //     }
    //     user.setGender(gender);

    //     User.Role role = User.Role.patient; // default
    //     if (dto.getRole() != null) {
    //         try {
    //             role = User.Role.valueOf(dto.getRole().toLowerCase());
    //         } catch (IllegalArgumentException e) {
    //             throw new RuntimeException("Invalid role: " + dto.getRole());
    //         }
    //     }
    //     user.setRole(role);

    //     user.setEmail(dto.getEmail());
    //     user.setPhone(dto.getPhone());
    //     user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
    //     user.setAvatar(dto.getAvatar());

    //     // Hash password
    //     user.setPassword(passwordEncoder.encode(dto.getPassword()));

    //     user.setAddress("");
    //     GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    //     Point defaultLocation = geometryFactory.createPoint(new Coordinate(0.0, 0.0));
    //     user.setLocation(defaultLocation);

    //     user.setCreatedAt(LocalDateTime.now());
    //     user.setUpdatedAt(LocalDateTime.now());

    //     userRepository.save(user);
    //     return userMapper.toDto(user);
    // }

    public UserInfoDTO updateUser(int userId, UserInfoDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID = " + userId));

        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getBirth() != null) user.setBirth(dto.getBirth());
        if (dto.getGender() != null) user.setGender(User.Gender.valueOf(dto.getGender()));
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getAvatar() != null) user.setAvatar(dto.getAvatar());
        if (dto.getPassword() != null) user.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public List<UserInfoDTO> getOnlyDoctors() {
        log.info("Fetching doctors with status != 2");
        List<User> doctors = userRepository.findByRoleAndStatusNot(User.Role.doctor, 2);
        return doctors.stream()
                .map(u -> userMapper.toDto(u))
                .toList();
    }

    @Transactional
    public void softDelete(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(2); // 2 = deleted
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // ========== ACCOUNT MANAGEMENT METHODS ==========
    
    /**
     * Lấy danh sách tài khoản (không bao gồm password)
     */
    public List<UserAccountDTO> getAllUserAccounts() {
        log.info("Fetching all user accounts");
        List<User> users = userRepository.findAll();
        return userMapper.toAccountDtoList(users);
    }
    
    /**
     * Reset password cho user theo email
     */
    @Transactional
    public boolean resetUserPassword(ResetPasswordRequest request) {
        log.info("Resetting password for email: {}", request.getEmail());
        
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            log.warn("User not found with email: {}", request.getEmail());
            return false;
        }
        
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        log.info("Password reset successfully for user: {}", request.getEmail());
        return true;
    }
    
    /**
     * Reset password về mặc định (123) cho user theo email
     */
    @Transactional
    public boolean resetPasswordToDefault(String email) {
        log.info("Resetting password to default for email: {}", email);
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            log.warn("User not found with email: {}", email);
            return false;
        }
        
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode("123")); // Password mặc định
        userRepository.save(user);
        
        log.info("Password reset to default successfully for user: {}", email);
        return true;
    }
    // ==================== ADMIN MANAGEMENT ====================
    public UserInfoDTO createUser(UserInfoDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setPassword(passwordEncoder.encode("123456")); // mật khẩu mặc định
        user.setRole(dto.getRole() != null
                ? User.Role.valueOf(dto.getRole().toLowerCase())
                : User.Role.patient);

        user.setStatus(1);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public UserInfoDTO updateUser(Integer userId, UserInfoDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFullName(dto.getFullName());
        user.setRole(User.Role.valueOf(dto.getRole()));
        user.setStatus(dto.getStatus());
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }

    public List<UserInfoDTO> searchUsers(String keyword) {
        List<User> users;

        if (keyword == null || keyword.isEmpty()) {
            users = userRepository.findAll();
        } else {
            users = userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
        }

        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }


}
