package com.example.Iot_Project.enity;

import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "classrooms")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Classroom {
    @Id
    String id;
    String name;
    String building;
    Integer capacity;
    Device device;
    List<Schedule> schedules;
    Config config;
    CurrentState currentState;
    Instant createdAt;
}
