package com.myclinic.user.service;

import com.myclinic.user.dto.UserInfoDTO;
import com.myclinic.user.dto.UserAccountDTO;
import com.myclinic.user.dto.ResetPasswordRequest;
import com.myclinic.user.entity.User;
import com.myclinic.user.mapper.UserMapper;
import com.myclinic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;

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
        user.setPassword(request.getNewPassword());
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
        user.setPassword("123"); // Password mặc định
        userRepository.save(user);
        
        log.info("Password reset to default successfully for user: {}", email);
        return true;
    }
}
