package com.myclinic.appointment.client;

import com.myclinic.appointment.config.FeignConfig;
import com.myclinic.appointment.dto.UserInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
}

