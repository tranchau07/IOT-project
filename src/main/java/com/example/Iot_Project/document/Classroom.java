package com.example.Iot_Project.document;

import com.example.Iot_Project.model.Config;
import com.example.Iot_Project.model.CurrentState;
import com.example.Iot_Project.model.Device;
import com.example.Iot_Project.model.Schedule;
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

    @Builder.Default
    boolean faultLatched = false;

    @org.springframework.data.annotation.Version
    Long version; // Optimistic locking to prevent concurrent read-modify-write race conditions
}
