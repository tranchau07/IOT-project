package com.example.Iot_Project.document;

import com.example.Iot_Project.model.Environment;
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
    @org.springframework.data.mongodb.core.index.Indexed(expireAfterSeconds = 15552000) // TTL: 6 months
    Instant timestamp;
    Environment environment;
    Double voltage;
    Boolean smokeDetected;
    Boolean doorOpen;
}
