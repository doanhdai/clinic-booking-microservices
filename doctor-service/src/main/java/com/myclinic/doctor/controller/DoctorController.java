package com.myclinic.doctor.controller;

import com.myclinic.doctor.dto.DoctorDTO;
import com.myclinic.doctor.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DoctorController {
    
    private final DoctorService doctorService;
    
    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        log.info("GET /api/doctors - Fetching all doctors");
        List<DoctorDTO> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }
    
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<DoctorDTO>> getDoctorsBySpecialization(
            @PathVariable String specialization) {
        log.info("GET /api/doctors/specialization/{} - Fetching doctors by specialization", specialization);
        List<DoctorDTO> doctors = doctorService.getDoctorsBySpecialization(specialization);
        return ResponseEntity.ok(doctors);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<DoctorDTO>> searchDoctors(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String specialization) {
        
        log.info("GET /api/doctors/search - keyword: {}, specialization: {}", keyword, specialization);
        
        List<DoctorDTO> doctors;
        
        if (specialization != null && keyword != null) {
            doctors = doctorService.searchDoctorsBySpecializationAndKeyword(specialization, keyword);
        } else if (specialization != null) {
            doctors = doctorService.getDoctorsBySpecialization(specialization);
        } else if (keyword != null) {
            doctors = doctorService.searchDoctors(keyword);
        } else {
            doctors = doctorService.getAllDoctors();
        }
        
        return ResponseEntity.ok(doctors);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Integer userId) {
        log.info("GET /api/doctors/{} - Fetching doctor by ID", userId);
        // TODO: Implement getDoctorById method in service
        return ResponseEntity.notFound().build();
    }
}
