package com.myclinic.user.repository;

import com.myclinic.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    @Query("SELECT u FROM User u WHERE u.id IN :userIds")
    List<User> findByIds(@Param("userIds") List<Integer> userIds);
    
    @Query("SELECT u FROM User u WHERE u.role = 'doctor' AND u.status = 1")
    List<User> findActiveDoctors();

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<User> findByRoleAndStatusNot(User.Role role, Integer status);
    
    // Tìm user theo email
    Optional<User> findByEmail(String email);
}
