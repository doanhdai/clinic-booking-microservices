package com.myclinic.user.controller;

import com.myclinic.user.dto.UserInfoDTO;
import com.myclinic.user.entity.User;
import com.myclinic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;


    @GetMapping
    public ResponseEntity<List<UserInfoDTO>> getUsers() {
        log.info("GET /api/users - Fetching users");
        List<UserInfoDTO> user = userService.getUsers();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoDTO> getUserById(@PathVariable Integer userId) {
        log.info("GET /api/users/{} - Fetching user by ID", userId);
        UserInfoDTO user = userService.getUserById(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/batch")
    public ResponseEntity<List<UserInfoDTO>> getUsersByIds(@RequestParam List<Integer> userIds) {
        log.info("GET /api/users/batch - Fetching users by IDs: {}", userIds);
        List<UserInfoDTO> users = userService.getUsersByIds(userIds);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/doctors")
    public ResponseEntity<List<UserInfoDTO>> getActiveDoctors() {
        log.info("GET /api/users/doctors - Fetching active doctors");
        List<UserInfoDTO> doctors = userService.getActiveDoctors();
        return ResponseEntity.ok(doctors);
    }
}
