package com.myclinic.user.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RegisterRequestDTO {
    private String fullName;
    private LocalDate birth;
    private String gender;
    private String email;
    private String password;
}
