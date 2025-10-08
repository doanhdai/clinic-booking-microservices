package com.myclinic.doctor.service;

import com.myclinic.doctor.client.UserClient;
import com.myclinic.doctor.dto.DoctorDTO;
import com.myclinic.doctor.dto.UserInfoDTO;
import com.myclinic.doctor.entity.Doctor;
import com.myclinic.doctor.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {
    
    private final DoctorRepository doctorRepository;
    private final UserClient userClient;
    
    public List<DoctorDTO> getAllDoctors() {
        log.info("Fetching all doctors");
        List<Doctor> doctors = doctorRepository.findAll();
        return convertToDoctorDTOs(doctors);
    }
    
    public List<DoctorDTO> getDoctorsBySpecialization(String specialization) {
        log.info("Fetching doctors by specialization: {}", specialization);
        List<Doctor> doctors = doctorRepository.findBySpecializationContaining(specialization);
        return convertToDoctorDTOs(doctors);
    }
    
    public List<DoctorDTO> searchDoctors(String keyword) {
        log.info("Searching doctors with keyword: {}", keyword);
        List<Doctor> doctors = doctorRepository.findByDescriptionContaining(keyword);
        return convertToDoctorDTOs(doctors);
    }
    
    public List<DoctorDTO> searchDoctorsBySpecializationAndKeyword(String specialization, String keyword) {
        log.info("Searching doctors with specialization: {} and keyword: {}", specialization, keyword);
        List<Doctor> doctors = doctorRepository.findBySpecializationAndDescriptionContaining(specialization, keyword);
        return convertToDoctorDTOs(doctors);
    }
    
    private List<DoctorDTO> convertToDoctorDTOs(List<Doctor> doctors) {
        if (doctors.isEmpty()) {
            return List.of();
        }
        
        // Lấy danh sách user IDs
        List<Integer> userIds = doctors.stream()
                .map(Doctor::getUserId)
                .collect(Collectors.toList());
        
        try {
            // Gọi user-service để lấy thông tin user
            List<UserInfoDTO> userInfos = userClient.getUsersByIds(userIds);
            
            // Tạo map để lookup nhanh
            Map<Integer, UserInfoDTO> userMap = userInfos.stream()
                    .collect(Collectors.toMap(UserInfoDTO::getId, user -> user));
            
            // Convert doctors to DTOs
            return doctors.stream()
                    .map(doctor -> convertToDoctorDTO(doctor, userMap.get(doctor.getUserId())))
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Error fetching user information: {}", e.getMessage());
            // Trả về danh sách doctor không có thông tin user
            return doctors.stream()
                    .map(this::convertToDoctorDTOWithoutUser)
                    .collect(Collectors.toList());
        }
    }
    
    private DoctorDTO convertToDoctorDTO(Doctor doctor, UserInfoDTO userInfo) {
        DoctorDTO dto = new DoctorDTO();
        dto.setUserId(doctor.getUserId());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setDescription(doctor.getDescription());
        dto.setBankNumber(doctor.getBankNumber());
        dto.setBankName(doctor.getBankName());
        dto.setBankId(doctor.getBankId());
        dto.setCreatedAt(doctor.getCreatedAt());
        dto.setUpdatedAt(doctor.getUpdatedAt());
        
        if (userInfo != null) {
            dto.setFullName(userInfo.getFullName());
            dto.setEmail(userInfo.getEmail());
            dto.setPhone(userInfo.getPhone());
            dto.setAddress(userInfo.getAddress());
            dto.setAvatar(userInfo.getAvatar());
            dto.setGender(userInfo.getGender());
            dto.setBirth(userInfo.getBirth() != null ? userInfo.getBirth().toString() : null);
        }
        
        return dto;
    }
    
    private DoctorDTO convertToDoctorDTOWithoutUser(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setUserId(doctor.getUserId());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setDescription(doctor.getDescription());
        dto.setBankNumber(doctor.getBankNumber());
        dto.setBankName(doctor.getBankName());
        dto.setBankId(doctor.getBankId());
        dto.setCreatedAt(doctor.getCreatedAt());
        dto.setUpdatedAt(doctor.getUpdatedAt());
        return dto;
    }
}
