package com.myclinic.user.service;

import com.myclinic.user.dto.UserInfoDTO;
import com.myclinic.user.entity.User;
import com.myclinic.user.mapper.UserMapper;
import com.myclinic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    private final UserMapper userMapper;
    
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
}
