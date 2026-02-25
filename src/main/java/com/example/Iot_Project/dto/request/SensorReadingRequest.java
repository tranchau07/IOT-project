package com.example.Iot_Project.dto.request;

import com.example.Iot_Project.enity.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SensorReadingRequest {
    String classroomId;
    String deviceId;
    Instant timestamp;
    Environment environment;
    Double voltage;
}
