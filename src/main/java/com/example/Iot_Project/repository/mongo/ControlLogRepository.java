package com.example.Iot_Project.repository.mongo;

import com.example.Iot_Project.document.ControlLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface ControlLogRepository extends MongoRepository<ControlLog, String> {
    List<ControlLog> findByClassroomIdAndTimestampBetween(
            String classroomId,
            Instant start,
            Instant end
    );

    Page<ControlLog> findByClassroomIdAndTimestampBetween(
            String classroomId,
            Instant start,
            Instant end,
            Pageable pageable
    );
}
