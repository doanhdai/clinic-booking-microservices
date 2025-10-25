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
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false, defaultValue = "10") Double radiusKm
    ) {
        // nếu chỉ có lat hoặc lng, coi như không dùng tìm kiếm theo location tọa độ
        if (!allOrNone(lat, lng)) {
            lat = lng  = null;
        }

        var result = doctorService.searchDoctorsAdvanced(
                specialization, fullName, address, lat, lng, radiusKm
        );
        return ResponseEntity.ok(result);
    }

    private boolean allOrNone(Double lat, Double lng) {
        int cnt = 0;
        if (lat != null) cnt++;
        if (lng != null) cnt++;
        return cnt == 0 || cnt == 2;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Integer userId) {
        DoctorDTO doctor = doctorService.getDoctorById(userId);
        if (doctor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(doctor);
    }

    ///-------------------------------------------------------------------
    @GetMapping("/filter")
    public ResponseEntity<List<DoctorDTO>> getDoctorsWithFilters(
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String name) {

        log.info("GET /api/doctors/filter - specialty: {}, name: {}", specialty, name);

        List<DoctorDTO> doctors = doctorService.getDoctorsBySpecializationAndName(specialty, name);

        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/specialty/ids/{specialty}")
    public ResponseEntity<List<Integer>> getDoctorIdsBySpecialty(@PathVariable String specialty) {
        List<Integer> doctorIds = doctorService.getDoctorIdsBySpecialty(specialty);
        return ResponseEntity.ok(doctorIds);
    }
}