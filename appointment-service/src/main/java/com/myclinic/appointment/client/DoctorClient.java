package com.myclinic.appointment.client;

import com.myclinic.appointment.config.FeignConfig;
import com.myclinic.appointment.dto.DoctorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "DOCTOR-SERVICE",
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

    @GetMapping("/filter")
    List<DoctorDTO> getDoctorsWithFilters(
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String name);

    @GetMapping("/specialty/ids/{specialty}")
    List<Integer> getDoctorIdsBySpecialty(@PathVariable String specialty);
}


