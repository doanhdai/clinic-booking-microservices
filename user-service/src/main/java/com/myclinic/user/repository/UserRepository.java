package com.myclinic.user.repository;

import com.myclinic.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    @Query("SELECT u FROM User u WHERE u.id IN :userIds")
    List<User> findByIds(@Param("userIds") List<Integer> userIds);
    
    @Query("SELECT u FROM User u WHERE u.role = 'doctor' AND u.status = 1")
    List<User> findActiveDoctors();
}
