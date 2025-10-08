package com.myclinic.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    
    private Integer id;
    private String fullName;
    private LocalDate birth;
    private String gender;
    private String address;
    private String email;
    private String phone;
    private String avatar;
    private String role;
    private Integer status;
}
