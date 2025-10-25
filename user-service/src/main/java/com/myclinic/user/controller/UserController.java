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
    public ResponseEntity<List<User>> getUsers() {
        log.info("GET /api/users - Fetching users");
        List<User> user = userService.getUsers();
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

    @PostMapping
    public ResponseEntity<UserInfoDTO> createUser(@RequestBody UserInfoDTO dto) {
        log.info("POST /api/users - Creating new user");
        UserInfoDTO newUser = userService.createUser(dto);
        return ResponseEntity.status(201).body(newUser);
    }


    @PutMapping("/{userId}")
    public ResponseEntity<UserInfoDTO> updateUser(
            @PathVariable("userId") int userId,
            @RequestBody UserInfoDTO dto) {

        log.info("PUT /api/users/{} - Updating user", userId);
        UserInfoDTO updatedUser = userService.updateUser(userId, dto);

        if (updatedUser == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/only-doctors")
    public ResponseEntity<List<UserInfoDTO>> getListDoctors() {
        log.info("GET /api/users/only-doctors - Fetching doctors");
        List<UserInfoDTO> doctors = userService.getOnlyDoctors();
        return ResponseEntity.ok(doctors);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDeleteUser(@PathVariable Integer id) {
        log.info("DELETE /api/users/{} - Soft deleting user", id);
        userService.softDelete(id);
        return ResponseEntity.ok("Đã vô hiệu hóa user");
    }
}
