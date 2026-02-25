package com.example.Iot_Project.repository;

import com.example.Iot_Project.enity.ControlLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface ControlLogRepository extends MongoRepository<ControlLog, String> {
    List<ControlLog> findByClassroomIdAndTimestampBetween(
            String classroomId,
            Instant start,
            Instant end
    );
}
