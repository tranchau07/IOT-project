package com.example.Iot_Project.repository.mongo;

import com.example.Iot_Project.document.Classroom;
import com.example.Iot_Project.model.Device;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface ClassroomRepository extends MongoRepository<Classroom, String> {
    boolean existsByName(String name);
}
