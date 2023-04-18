package com.example.demo.model.repositories;

import com.example.demo.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    boolean existsByEmail(String email);
    Optional<User> getByVerCode(String code);
    Optional<User> getByEmail(String email);
    @Query("SELECT u FROM users u WHERE u.registeredAt < :thresholdTime AND u.isVerified = false")
    List<User> findUnverifiedUsersRegisteredBefore(@Param("thresholdTime") LocalDateTime thresholdTime);
}
