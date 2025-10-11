package com.myclinic.appointment.client;

import com.myclinic.appointment.config.FeignConfig;
import com.myclinic.appointment.dto.DoctorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "doctor-service",               // trùng tên đăng ký trên Eureka
        path = "/api/doctors",
        configuration = FeignConfig.class
)
public interface DoctorClient {
    /**
     * Đặt tên hàm trong Client không bắt buộc giống ở Controller,
     * chỉ cần trùng các annotation REST (@GetMapping, @PostMapping, @RequestParam, @PathVariable, …).
     * sẽ tự tìm đúng hàm bên Controller
     */

    @GetMapping("/{userId}")
    DoctorDTO getDoctorById(@PathVariable("userId") Integer userId);
}


