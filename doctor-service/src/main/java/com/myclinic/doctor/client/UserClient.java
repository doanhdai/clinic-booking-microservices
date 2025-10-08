package com.myclinic.doctor.client;

import com.myclinic.doctor.dto.UserInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", url = "http://localhost:8081")
public interface UserClient {
    
    @GetMapping("/api/users/{userId}")
    UserInfoDTO getUserById(@PathVariable("userId") Integer userId);
    
    @GetMapping("/api/users/batch")
    List<UserInfoDTO> getUsersByIds(@RequestParam("userIds") List<Integer> userIds);
}
