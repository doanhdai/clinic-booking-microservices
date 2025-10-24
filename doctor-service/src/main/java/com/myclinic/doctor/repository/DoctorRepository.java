package com.myclinic.doctor.repository;

import com.myclinic.doctor.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    @Query("SELECT d FROM Doctor d WHERE d.specialization LIKE %:specialization%")
    List<Doctor> findBySpecializationContaining(@Param("specialization") String specialization);

    @Query("SELECT d FROM Doctor d WHERE d.description LIKE %:keyword%")
    List<Doctor> findByDescriptionContaining(@Param("keyword") String keyword);

    @Query("SELECT d FROM Doctor d WHERE d.specialization LIKE %:specialization% AND d.description LIKE %:keyword%")
    List<Doctor> findBySpecializationAndDescriptionContaining(@Param("specialization") String specialization,
                                                              @Param("keyword") String keyword);

    @Query(value = """
            SELECT d.*
            FROM doctors d
            WHERE (:spec IS NULL OR d.specialization LIKE CONCAT('%', :spec, '%'))
                    AND ST_Distance_Sphere(
                            d.location,
                            ST_SRID(POINT(:lng, :lat), 4326)
                          ) <= (:radiusKm * 1000)
            ORDER BY ST_Distance_Sphere(
                             d.location,
                             ST_SRID(POINT(:lng, :lat), 4326)
                           )
            """, nativeQuery = true)
    List<Doctor> searchDoctorsBySpecAndLocation(
            @Param("spec") String spec,
            @Param("lng") double lng,
            @Param("lat") double lat,
            @Param("radiusKm") double radiusKm
    );

    // Nếu không truyền toạ độ => chỉ lọc theo chuyên khoa
    @Query(value = """
           SELECT d.*
           FROM doctors d
           WHERE (:spec IS NULL OR d.specialization LIKE CONCAT('%', :spec, '%'))
           """, nativeQuery = true)
    List<Doctor> searchNoGeo(@Param("spec") String spec);

}