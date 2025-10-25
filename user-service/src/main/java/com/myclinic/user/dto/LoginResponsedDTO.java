package com.myclinic.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponsedDTO {
    private String token;
    private String type = "Bearer";
    private Integer id;
    private String email;
    private String fullName;
    private String role;
}
