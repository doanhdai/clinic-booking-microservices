package com.myclinic.user.service;

import com.myclinic.user.dto.UserInfoDTO;
import com.myclinic.user.entity.User;
import com.myclinic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserInfoDTO getUserById(Integer userId) {
        log.info("Fetching user by ID: {}", userId);
        return userRepository.findById(userId)
                .map(this::convertToUserInfoDTO)
                .orElse(null);
    }
    
    public List<UserInfoDTO> getUsersByIds(List<Integer> userIds) {
        log.info("Fetching users by IDs: {}", userIds);
        List<User> users = userRepository.findByIds(userIds);
        return users.stream()
                .map(this::convertToUserInfoDTO)
                .collect(Collectors.toList());
    }
    
    public List<UserInfoDTO> getActiveDoctors() {
        log.info("Fetching active doctors");
        List<User> doctors = userRepository.findActiveDoctors();
        return doctors.stream()
                .map(this::convertToUserInfoDTO)
                .collect(Collectors.toList());
    }
    
    private UserInfoDTO convertToUserInfoDTO(User user) {
        UserInfoDTO dto = new UserInfoDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setBirth(user.getBirth());
        dto.setGender(user.getGender() != null ? user.getGender().name() : null);
        dto.setAddress(user.getAddress());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAvatar(user.getAvatar());
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        dto.setStatus(user.getStatus());
        return dto;
    }
}
