package com.myclinic.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountDTO {
    private Integer id;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private Integer status;
    private String avatar;
    private String address;
}
