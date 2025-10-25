package com.myclinic.user.controller;

import com.myclinic.user.dto.UserInfoDTO;
import com.myclinic.user.dto.UserAccountDTO;
import com.myclinic.user.dto.ResetPasswordRequest;
import com.myclinic.user.entity.User;
import com.myclinic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @GetMapping("/batch")
    public List<UserInfoDTO> getUsersByIds(@RequestParam("userIds") List<Integer> userIds) {
        return userService.getUsersByIds(userIds);
    }


    // ========== ACCOUNT MANAGEMENT APIs ==========
    
    /**
     * Lấy danh sách tài khoản (không bao gồm password)
     */
    @GetMapping("/accounts")
    public ResponseEntity<List<UserAccountDTO>> getAllUserAccounts() {
        log.info("GET /api/users/accounts - Fetching all user accounts");
        List<UserAccountDTO> accounts = userService.getAllUserAccounts();
        return ResponseEntity.ok(accounts);
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
    
    /**
     * Reset password cho user theo email
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetUserPassword(@RequestBody ResetPasswordRequest request) {
        log.info("POST /api/users/reset-password - Resetting password for email: {}", request.getEmail());

        boolean success = userService.resetUserPassword(request);
        if (success) {
            return ResponseEntity.ok().body("Password reset successuflly for user: " + request.getEmail());
        } else {
            return ResponseEntity.badRequest().body("User not found with email: " + request.getEmail());
        }
    }

    /**
     * Reset password về mặc định (123) cho user theo email
     */
    @PostMapping("/reset-password-default")
    public ResponseEntity<?> resetPasswordToDefault(@RequestParam String email) {
        log.info("POST /api/users/reset-password-default - Resetting password to default for email: {}", email);

        boolean success = userService.resetPasswordToDefault(email);
        if (success) {
            return ResponseEntity.ok().body("Password reset to default (123) successfully for user: " + email);
        } else {
            return ResponseEntity.badRequest().body("User not found with email: " + email);
        }
    }
    // ==================== ADMIN MANAGEMENT ====================

    // phần này chưa check đk gì hết 
    @PostMapping("/admin")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> createUser(@RequestBody UserInfoDTO userInfo) {
        log.info("POST /api/users/admin - Creating new user");
        UserInfoDTO createdUser = userService.createUser(userInfo);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/admin/{userId}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> updateUser(@PathVariable Integer userId, @RequestBody UserInfoDTO userInfo) {
        log.info("PUT /api/users/admin/{} - Updating user", userId);
        UserInfoDTO updatedUser = userService.updateUser(userId, userInfo);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/admin/{userId}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId) {
        log.info("DELETE /api/users/admin/{} - Deleting user", userId);
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/admin/search")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<UserInfoDTO>> searchUsers(@RequestParam(required = false) String keyword) {
        log.info("GET /api/users/admin/search?keyword={} - Searching users", keyword);
        List<UserInfoDTO> users = userService.searchUsers(keyword);
        return ResponseEntity.ok(users);
    }

    // phần này ai làm nó conflict với cái mình ( tiệp) làm rồi 
    // nếu k check role vs token mà để api public là hỏng à, nó gửi req tùm lum 

    // @PostMapping
    // public ResponseEntity<UserInfoDTO> createUser(@RequestBody UserInfoDTO dto) {
    //     log.info("POST /api/users - Creating new user");
    //     UserInfoDTO newUser = userService.createUser(dto);
    //     return ResponseEntity.status(201).body(newUser);
    // }


    // @PutMapping("/{userId}")
    // public ResponseEntity<UserInfoDTO> updateUser(
    //         @PathVariable("userId") int userId,
    //         @RequestBody UserInfoDTO dto) {

    //     log.info("PUT /api/users/{} - Updating user", userId);
    //     UserInfoDTO updatedUser = userService.updateUser(userId, dto);

    //     if (updatedUser == null) return ResponseEntity.notFound().build();
    //     return ResponseEntity.ok(updatedUser);
    // }

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
