package com.example.Iot_Project.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Environment {
    Double temperature;
    Double humidity;
    Integer occupancy;
    Integer lightLevel;
    Boolean motionDetected;
}
