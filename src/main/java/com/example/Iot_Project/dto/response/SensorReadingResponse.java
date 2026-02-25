package com.example.Iot_Project.dto.response;

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
public class SensorReadingResponse {
    String id;
    String classroomId;
    String deviceId;
    Instant timestamp;
    Environment environment;
    Double voltage;
}
