package com.myclinic.doctor.client;

import com.myclinic.doctor.config.FeignConfig;
import com.myclinic.doctor.dto.UserInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "user-service",               // trùng tên đăng ký trên Eureka
        path = "/api/users",
        configuration = FeignConfig.class
)
public interface UserClient {
    /**
     * Đặt tên hàm trong UserClient không bắt buộc giống ở UserController,
     * chỉ cần trùng các annotation REST (@GetMapping, @PostMapping, @RequestParam, @PathVariable, …).
     * sẽ tự tìm đúng hàm bên UserController
     */

    @GetMapping("/{userId}")
    UserInfoDTO getUserById(@PathVariable("userId") Integer userId);
    
    @GetMapping("/batch")
    List<UserInfoDTO> getUsersByIds(@RequestParam("userIds") List<Integer> userIds);
}
