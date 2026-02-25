package com.example.Iot_Project.enity;

import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sensor_readings")
@CompoundIndex(name = "classroom_time_idx",
        def = "{'classroomId': 1, 'timestamp': -1}")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SensorReading {
    @Id
    String id;
    String classroomId;
    String deviceId;
    Instant timestamp;
    Environment environment;
    Double voltage;
}
