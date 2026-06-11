package com.example.Iot_Project.repository.jpa;

import com.example.Iot_Project.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
    @Transactional
    void deleteByExpiryTimeBefore(Date now);
}
