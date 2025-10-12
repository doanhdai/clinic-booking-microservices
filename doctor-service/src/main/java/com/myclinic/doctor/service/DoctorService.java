package com.myclinic.doctor.service;

import com.myclinic.doctor.client.UserClient;
import com.myclinic.doctor.dto.DoctorDTO;
import com.myclinic.doctor.dto.UserInfoDTO;
import com.myclinic.doctor.entity.Doctor;
import com.myclinic.doctor.mapper.DoctorMapper;
import com.myclinic.doctor.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserClient userClient;
    private final DoctorMapper doctorMapper;

    public List<DoctorDTO> getAllDoctors() {
        log.info("Fetching all doctors");
        List<Doctor> doctors = doctorRepository.findAll();
        return enrichDoctorDTOs(doctors);
    }

    public List<DoctorDTO> getDoctorsBySpecialization(String specialization) {
        log.info("Fetching doctors by specialization: {}", specialization);
        List<Doctor> doctors = doctorRepository.findBySpecializationContaining(specialization);
        return enrichDoctorDTOs(doctors);
    }

    public List<DoctorDTO> searchDoctors(String keyword) {
        log.info("Searching doctors with keyword: {}", keyword);
        List<Doctor> doctors = doctorRepository.findByDescriptionContaining(keyword);
        return enrichDoctorDTOs(doctors);
    }

    public List<DoctorDTO> searchDoctorsBySpecializationAndKeyword(String specialization, String keyword) {
        log.info("Searching doctors with specialization: {} and keyword: {}", specialization, keyword);
        List<Doctor> doctors = doctorRepository.findBySpecializationAndDescriptionContaining(specialization, keyword);
        return enrichDoctorDTOs(doctors);
    }

    public List<DoctorDTO> searchDoctorsAdvanced(
            String specialization,
            String fullName,
            String address,
            Double lat, Double lng, Double radiusKm
    ) {
        // 1) Lọc sơ bộ ở DB (giảm dữ liệu kéo về)
        final boolean hasGeo = lat != null && lng != null && radiusKm != null;

        List<Doctor> doctors = hasGeo
                ? doctorRepository.searchDoctorsBySpecAndLocation(
                blankToNull(specialization),
                lng, lat, radiusKm) // chú ý: lng trước, lat sau
                : (isNotBlank(specialization)
                ? doctorRepository.searchNoGeo(specialization)
                : doctorRepository.findAll()); // không geo & không spec -> lấy tất cả để còn lọc theo tên

        if (doctors.isEmpty()) return List.of();

        // 2) Enrich: ghép dữ liệu user + map sang DTO
        List<DoctorDTO> doctorDTOs = enrichDoctorDTOs(doctors);

        // 3) Lọc theo fullName (nếu có yêu cầu)
        if (isNotBlank(fullName)) {
            final String nameKw = fullName.toLowerCase();
            doctorDTOs = doctorDTOs.stream()
                    .filter(dto -> containsIgnoreCaseAndAccent(dto.getFullName(), nameKw))
                    .toList();
        }

        // 4) Lọc theo address (nếu có yêu cầu)
        if (isNotBlank(address)) {
            String kw = address.toLowerCase();
            doctorDTOs = doctorDTOs.stream()
                    .filter(d -> containsIgnoreCaseAndAccent(d.getAddress(), address))
                    .toList();
        }
        return doctorDTOs;
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }

    //Hàm này để chuyển đổi chuỗi về không dấu vì tiếng việt thì thường có dấu
    private static String normalize(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{M}");
        return pattern.matcher(normalized).replaceAll("").toLowerCase();
    }

    //Tìm kiếm khớp không theo dấu/hoa/thường: Bich hay bích đều khớp với Bích
    private static boolean containsIgnoreCaseAndAccent(String source, String keyword) {
        if (source == null || keyword == null) return false;
        return normalize(source).contains(normalize(keyword));
    }

    public DoctorDTO getDoctorById(Integer userId) {
        log.info("getDoctorById with keyword: {}", userId);
        Optional<Doctor> doctor = doctorRepository.findById(userId);
        Doctor hasDoctor;
        if (doctor.isPresent()) hasDoctor = doctor.get();
        else return null;
        return enrichDoctorDTO(hasDoctor);
    }

    /**
     * enrichDoctorDTOs : Kết hợp dữ liệu từ Doctor (DB) + UserInfoDTO (user-service)
     */
    private DoctorDTO enrichDoctorDTO(Doctor doctor) {
        if (doctor == null) return null;
        try {
            // Lấy thông tin user từ user-service
            UserInfoDTO userInfo = userClient.getUserById(doctor.getUserId());

            // Dùng mapper để merge entity + user info
            return doctorMapper.toDto(doctor, userInfo);

        } catch (Exception e) {
            log.error("Error fetching user information: {}", e.getMessage());
            // Lỗi thì là do id của doctor chưa có id đó ở bảng user
            // => chỉ trả về phần Doctor (không có user info)
            return doctorMapper.toDto(doctor);
        }
    }

    private List<DoctorDTO> enrichDoctorDTOs(List<Doctor> doctors) {
        if (doctors.isEmpty()) return List.of();

        List<Integer> userIds = doctors.stream()
                .map(Doctor::getUserId)
                .toList();

        try {
            // Lấy thông tin user từ user-service
            List<UserInfoDTO> userInfos = userClient.getUsersByIds(userIds);
            Map<Integer, UserInfoDTO> userMap = userInfos.stream()
                    .collect(Collectors.toMap(UserInfoDTO::getId, u -> u));

            // Dùng mapper để merge entity + user info
            return doctors.stream()
                    .map(d -> doctorMapper.toDto(d, userMap.get(d.getUserId())))
                    .toList();

        } catch (Exception e) {
            log.error("Error fetching user information: {}", e.getMessage());
            // Lỗi thì là do id của doctor chưa có id đó ở bảng user
            // => chỉ trả về phần Doctor (không có user info)
            return doctorMapper.toDtoList(doctors);
        }
    }

}
